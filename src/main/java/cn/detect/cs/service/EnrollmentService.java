package cn.detect.cs.service;

import cn.detect.cs.dto.*;
import cn.detect.cs.entity.Course;
import cn.detect.cs.entity.Enrollment;
import cn.detect.cs.entity.Student;
import cn.detect.cs.common.BusinessException;
import cn.detect.cs.notification.DropNotificationMessage;
import cn.detect.cs.notification.EnrollNotificationMessage;
import cn.detect.cs.repository.CourseRepository;
import cn.detect.cs.repository.EnrollmentRepository;
import cn.detect.cs.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final RabbitMQSender rabbitMQSender;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private static final Logger logger = LoggerFactory.getLogger(EnrollmentService.class);

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 学生选课
     * @param userId
     * @param courseId
     */
    @Transactional
    public void enroll(Long userId, Long courseId) {
        RLock lock = redissonClient.getLock("course:" + courseId);
        try {
            if (lock.tryLock(5, 30, TimeUnit.SECONDS)) {
                Student student = studentRepository.findByUserId(userId)
                        .orElseThrow(() -> new BusinessException("学生不存在"));

                Course course = courseRepository.findById(courseId)
                        .orElseThrow(() -> new BusinessException("课程不存在"));

                Optional<Enrollment> existingOpt = enrollmentRepository.findByStudentIdAndCourseId(student.getId(), courseId);
                if (existingOpt.isPresent()) {
                    Enrollment existing = existingOpt.get();
                    if ("SELECTED".equals(existing.getStatus())) {
                        throw new BusinessException("您已选过该课程，请勿重复选课");
                    } else {
                        existing.setStatus("SELECTED");
                        existing.setEnrollmentTime(LocalDateTime.now());
                        existing.setYear(Year.now().getValue());
                        enrollmentRepository.save(existing);
                        int updated = courseRepository.incrementSelectedCount(courseId);
                        if (updated == 0) {
                            throw new BusinessException("课程已满，无法选课");
                        }
                        // 选课成功后发送通知
                        EnrollNotificationMessage notification = new EnrollNotificationMessage();
                        notification.setStudentId(student.getId());
                        notification.setStudentName(student.getName());
                        notification.setStudentEmail(student.getEmail()); // 如果 Student 有 email 字段
                        notification.setCourseId(course.getId());
                        notification.setCourseName(course.getName());
                        notification.setMessage("选课成功，请按时上课。");
                        rabbitMQSender.sendEnrollNotification(notification);
                        return;
                    }
                }

                int updated = courseRepository.incrementSelectedCount(courseId);
                if (updated == 0) {
                    throw new BusinessException("课程已满，无法选课");
                }

                Enrollment enrollment = new Enrollment();
                enrollment.setStudent(student);
                enrollment.setCourse(course);
                enrollment.setStatus("SELECTED");
                enrollment.setYear(Year.now().getValue());
                enrollmentRepository.save(enrollment);
                // 选课成功后发送通知
                EnrollNotificationMessage notification = new EnrollNotificationMessage();
                notification.setStudentId(student.getId());
                notification.setStudentName(student.getName());
                notification.setStudentEmail(student.getEmail()); // 如果 Student 有 email 字段
                notification.setCourseId(course.getId());
                notification.setCourseName(course.getName());
                notification.setMessage("选课成功，请按时上课。");
                rabbitMQSender.sendEnrollNotification(notification);
            } else {
                throw new BusinessException("系统繁忙，请稍后重试");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("锁等待被中断");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 教师查询某课程的选课学生
     * @param courseId
     * @param teacherId
     * @param year
     * @param name
     * @param pageable
     * @return
     */
    public Page<StudentGradeDTO> getCourseStudentsWithFilters(Long courseId, Long teacherId, Integer year, String name, Pageable pageable) {
        Course course = courseRepository.findByIdAndTeacherEntityId(courseId, teacherId)
                .orElseThrow(() -> new BusinessException("课程不存在或无权限查看"));
        Page<Enrollment> page = enrollmentRepository.findSelectedByCourseIdWithFilters(courseId, year, name, pageable);
        return page.map(this::mapToStudentGradeDTO);
    }

    /**
     * 学生查询某学年的已选课程
     * @param userId
     * @param year
     * @return
     */
    public List<CourseResponse> getMyCoursesByYear(Long userId, Integer year) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("学生不存在"));
        List<Enrollment> enrollments = enrollmentRepository.findSelectedByStudentIdAndYear(student.getId(), year);
        return enrollments.stream()
                .map(e -> mapToCourseResponse(e.getCourse()))
                .collect(Collectors.toList());
    }

    /**
     * 学生退课
     * @param userId
     * @param courseId
     */
    @Transactional
    public void drop(Long userId, Long courseId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("学生不存在"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("课程不存在"));

        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(student.getId(), courseId)
                .orElseThrow(() -> new BusinessException("未找到选课记录"));

        if (!"SELECTED".equals(enrollment.getStatus())) {
            throw new BusinessException("该课程已经退课，无法重复退课");
        }

        enrollment.setStatus("DROPPED");
        enrollmentRepository.save(enrollment);
        courseRepository.decrementSelectedCount(courseId);

        DropNotificationMessage msg = new DropNotificationMessage(
                student.getId(), student.getName(), student.getEmail(),
                courseId, course.getName()
        );
        rabbitMQSender.sendDropNotification(msg);
    }

    /**
     * 查询当前学生的已选课程
     * @param userId
     * @return
     */
    public List<CourseResponse> getMyCourses(Long userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("学生不存在"));

        List<Enrollment> enrollments = enrollmentRepository.findSelectedByStudentId(student.getId());
        return enrollments.stream()
                .map(e -> mapToCourseResponse(e.getCourse()))
                .collect(Collectors.toList());
    }

    /**
     * 获取我的课表
     * @param userId
     * @return
     */
    public List<TimetableItem> getMyTimetable(Long userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("学生不存在"));
        List<Enrollment> enrollments = enrollmentRepository.findSelectedByStudentId(student.getId());
        return enrollments.stream()
                .map(e -> parseSchedule(e.getCourse()))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<TimetableItem> parseSchedule(Course course) {
        String scheduleStr = course.getSchedule();
        if (scheduleStr == null || scheduleStr.isEmpty()) {
            return Collections.emptyList();
        }
        List<TimetableItem> items = new ArrayList<>();
        String[] parts = scheduleStr.split(";");
        for (String part : parts) {
            TimetableItem item = parseSingleSchedule(course, part.trim());
            if (item != null) {
                items.add(item);
            }
        }
        return items;
    }

    private TimetableItem parseSingleSchedule(Course course, String schedulePart) {
        String[] tokens = schedulePart.split(" ");
        if (tokens.length < 3) {
            return null;
        }
        String day = tokens[0];
        String sectionRange = tokens[1];
        String location = tokens[2];
        String[] sections = sectionRange.replace("节", "").split("-");
        if (sections.length != 2) {
            return null;
        }
        Integer start = Integer.parseInt(sections[0]);
        Integer end = Integer.parseInt(sections[1]);
        TimetableItem item = new TimetableItem();
        item.setDay(day);
        item.setStartSection(start);
        item.setEndSection(end);
        item.setLocation(location);
        item.setCourseName(course.getName());
        item.setTeacher(course.getTeacher());
        item.setCourseCode(course.getCourseCode());
        return item;
    }

    /**
     * 获取学生成绩列表
     * @param userId
     * @return
     */
    public List<GradeResponse> getMyGrades(Long userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("学生不存在"));

        List<Enrollment> enrollments = enrollmentRepository.findAllByStudentId(student.getId());
        return enrollments.stream()
                .filter(e -> e.getGrade() != null)
                .map(this::mapToGradeResponse)
                .collect(Collectors.toList());
    }

    private GradeResponse mapToGradeResponse(Enrollment enrollment) {
        GradeResponse response = new GradeResponse();
        response.setEnrollmentId(enrollment.getId());
        response.setCourseName(enrollment.getCourse().getName());
        response.setCourseCode(enrollment.getCourse().getCourseCode());
        response.setCredit(enrollment.getCourse().getCredit());
        response.setGrade(enrollment.getGrade());
        response.setRegularGrade(enrollment.getRegularGrade());
        response.setExamGrade(enrollment.getExamGrade());
        return response;
    }

    private CourseResponse mapToCourseResponse(Course course) {
        CourseResponse response = new CourseResponse();
        response.setId(course.getId());
        response.setCourseCode(course.getCourseCode());
        response.setName(course.getName());
        response.setCredit(course.getCredit());
        response.setTeacher(course.getTeacher());
        response.setSchedule(course.getSchedule());
        response.setCapacity(course.getCapacity());
        response.setSelectedCount(course.getSelectedCount());
        response.setDescription(course.getDescription());
        return response;
    }

    public List<StudentGradeDTO> getCourseStudents(Long courseId, Long teacherId) {
        Course course = courseRepository.findByIdAndTeacherEntityId(courseId, teacherId)
                .orElseThrow(() -> new BusinessException("课程不存在或无权限查看"));
        List<Enrollment> enrollments = enrollmentRepository.findSelectedByCourseId(courseId);
        return enrollments.stream()
                .map(this::mapToStudentGradeDTO)
                .collect(Collectors.toList());
    }

    private StudentGradeDTO mapToStudentGradeDTO(Enrollment enrollment) {
        Student student = enrollment.getStudent();
        StudentGradeDTO dto = new StudentGradeDTO();
        dto.setEnrollmentId(enrollment.getId());
        dto.setStudentId(student.getId());
        dto.setStudentName(student.getName());
        dto.setStudentNumber(student.getStudentNumber());
        dto.setRegularGrade(enrollment.getRegularGrade());
        dto.setExamGrade(enrollment.getExamGrade());
        dto.setGrade(enrollment.getGrade());
        return dto;
    }

    /**
     * 教师更新学生成绩
     * @param enrollmentId
     * @param teacherId
     * @param request
     */
    @Transactional
    public void updateGrade(Long enrollmentId, Long teacherId, GradeUpdateRequest request) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new BusinessException("选课记录不存在"));
        Course course = enrollment.getCourse();
        if (!course.getTeacherEntity().getId().equals(teacherId)) {
            throw new BusinessException("无权限修改该课程成绩");
        }
        if (request.getRegularGrade() != null) {
            if (request.getRegularGrade().compareTo(BigDecimal.ZERO) < 0 || request.getRegularGrade().compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new BusinessException("平时成绩必须在0-100之间");
            }
            enrollment.setRegularGrade(request.getRegularGrade());
        }
        if (request.getExamGrade() != null) {
            if (request.getExamGrade().compareTo(BigDecimal.ZERO) < 0 || request.getExamGrade().compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new BusinessException("期末成绩必须在0-100之间");
            }
            enrollment.setExamGrade(request.getExamGrade());
        }
        if (request.getGrade() != null) {
            if (request.getGrade().compareTo(BigDecimal.ZERO) < 0 || request.getGrade().compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new BusinessException("总评成绩必须在0-100之间");
            }
            enrollment.setGrade(request.getGrade());
        } else {
            BigDecimal regular = enrollment.getRegularGrade() != null ? enrollment.getRegularGrade() : BigDecimal.ZERO;
            BigDecimal exam = enrollment.getExamGrade() != null ? enrollment.getExamGrade() : BigDecimal.ZERO;
            BigDecimal total = regular.multiply(BigDecimal.valueOf(0.4)).add(exam.multiply(BigDecimal.valueOf(0.6)));
            enrollment.setGrade(total);
        }
        enrollmentRepository.save(enrollment);
    }

    /**
     * 教师为学生选课
     * @param teacherId
     * @param studentNumber
     * @param courseCode
     */
    @Transactional
    public void enrollByTeacher(Long teacherId, String studentNumber, String courseCode) {
        studentNumber = studentNumber != null ? studentNumber.trim() : "";
        courseCode = courseCode != null ? courseCode.trim() : "";
        logger.info("教师选课 - 学号: {}, 课程编号: {}", studentNumber, courseCode);

        // 1. 查询课程
        Course course = courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new BusinessException("课程编号不存在，请确认"));

        // 2. 验证教师权限
        if (course.getTeacherEntity() == null || !course.getTeacherEntity().getId().equals(teacherId)) {
            throw new BusinessException("无权限操作该课程，该课程不属于您");
        }

        // 3. 查询学生
        Student student = studentRepository.findByStudentNumber(studentNumber)
                .orElseThrow(() -> new BusinessException("学生不存在，请确认学号"));

        // 4. 检查是否已有选课记录
        Optional<Enrollment> existingOpt = enrollmentRepository.findByStudentIdAndCourseId(student.getId(), course.getId());
        if (existingOpt.isPresent()) {
            Enrollment existing = existingOpt.get();
            if ("SELECTED".equals(existing.getStatus())) {
                throw new BusinessException("该学生已选过此课程");
            } else {
                // 之前退课了，重新激活选课
                existing.setStatus("SELECTED");
                existing.setEnrollmentTime(LocalDateTime.now());
                existing.setYear(Year.now().getValue());
                enrollmentRepository.save(existing);
                // 增加课程已选人数
                int updated = courseRepository.incrementSelectedCount(course.getId());
                if (updated == 0) {
                    throw new BusinessException("课程已满，无法选课");
                }
                // 注意：这里不要 return，继续执行后面的邮件发送
            }
        } else {
            // 全新选课，检查容量
            int updated = courseRepository.incrementSelectedCount(course.getId());
            if (updated == 0) {
                throw new BusinessException("课程已满，无法选课");
            }
            // 创建新记录
            Enrollment enrollment = new Enrollment();
            enrollment.setStudent(student);
            enrollment.setCourse(course);
            enrollment.setStatus("SELECTED");
            enrollment.setYear(Year.now().getValue());
            enrollmentRepository.save(enrollment);
        }

        // 5. 发送选课成功邮件通知（统一在最后执行）
        try {
            EnrollNotificationMessage msg = new EnrollNotificationMessage();
            msg.setStudentId(student.getId());
            msg.setStudentName(student.getName());
            msg.setStudentEmail(student.getEmail());
            msg.setCourseId(course.getId());
            msg.setCourseName(course.getName());
            msg.setMessage("教师为您手动选课成功");
            rabbitMQSender.sendEnrollNotification(msg);
            logger.info("选课通知已发送至消息队列，学生邮箱: {}", student.getEmail());
        } catch (Exception e) {
            logger.error("发送选课通知失败: {}", e.getMessage());
            // 不影响主流程
        }
    }
}