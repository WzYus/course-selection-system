package cn.detect.cs.service;

import cn.detect.cs.document.CourseDocument;
import cn.detect.cs.dto.*;
import cn.detect.cs.entity.*;
import cn.detect.cs.common.BusinessException;
import cn.detect.cs.repository.*;
import cn.detect.cs.repository.elastic.CourseDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    @Autowired
    private CourseSyncService courseSyncService;
    private final AppUserRepository appUserRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final AdminRepository adminRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;
    private final EnrollmentRepository enrollmentRepository;

    // ==================== 学生管理 ====================

    /**
     * 创建学生
     * @param request
     */
    @Transactional
    public void createStudent(CreateStudentRequest request) {
        // 用户名唯一性
        if (appUserRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessException("用户名已存在");
        }
        // 学号唯一性
        if (studentRepository.findByStudentNumber(request.getStudentNumber()).isPresent()) {
            throw new BusinessException("学号已存在");
        }

        AppUser appUser = new AppUser();
        appUser.setUsername(request.getUsername());
        appUser.setPassword(passwordEncoder.encode(request.getPassword()));
        appUser.setRole("ROLE_STUDENT");
        appUser.setEnabled(true);
        appUser = appUserRepository.save(appUser);

        Student student = new Student();
        student.setName(request.getName());
        student.setAge(request.getAge());
        student.setStudentNumber(request.getStudentNumber());
        student.setMajor(request.getMajor());
        student.setGrade(request.getGrade());
        student.setUser(appUser);
        studentRepository.save(student);
    }

    /**
     * 更新学生信息
     * @param studentId
     * @param request
     */
    @Transactional
    public void updateStudent(Long studentId, UpdateStudentRequest request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException("学生不存在"));
        if (request.getName() != null) {
            student.setName(request.getName());
        }
        if (request.getAge() != null) {
            student.setAge(request.getAge());
        }
        if (request.getMajor() != null) {
            student.setMajor(request.getMajor());
        }
        if (request.getGrade() != null) {
            student.setGrade(request.getGrade());
        }
        studentRepository.save(student);
    }

    /**
     * 删除学生
     * @param studentId
     */
    @Transactional
    public void deleteStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException("学生不存在"));
        AppUser user = student.getUser();
        // 删除学生记录
        studentRepository.delete(student);
        // 删除用户记录（级联删除选课记录？需要根据业务决定，选课记录应一并删除）
        if (user != null) {
            appUserRepository.delete(user);
        }
    }

    // ==================== 教师管理 ====================

    /**
     * 创建教师
     * @param request
     */
    @Transactional
    public void createTeacher(CreateTeacherRequest request) {
        if (appUserRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessException("用户名已存在");
        }
        if (teacherRepository.findByTeacherNumber(request.getTeacherNumber()).isPresent()) {
            throw new BusinessException("工号已存在");
        }

        AppUser appUser = new AppUser();
        appUser.setUsername(request.getUsername());
        appUser.setPassword(passwordEncoder.encode(request.getPassword()));
        appUser.setRole("ROLE_TEACHER");
        appUser.setEnabled(true);
        appUser = appUserRepository.save(appUser);

        Teacher teacher = new Teacher();
        teacher.setName(request.getName());
        teacher.setTeacherNumber(request.getTeacherNumber());
        teacher.setTitle(request.getTitle());
        teacher.setDepartment(request.getDepartment());
        teacher.setUser(appUser);
        teacherRepository.save(teacher);
    }

    /**
     * 更新教师信息
     * @param teacherId
     * @param request
     */
    @Transactional
    public void updateTeacher(Long teacherId, UpdateTeacherRequest request) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new BusinessException("教师不存在"));
        if (request.getName() != null) {
            teacher.setName(request.getName());
        }
        if (request.getTitle() != null) {
            teacher.setTitle(request.getTitle());
        }
        if (request.getDepartment() != null) {
            teacher.setDepartment(request.getDepartment());
        }
        teacherRepository.save(teacher);
    }

    /**
     * 删除教师
     * @param teacherId
     */
    @Transactional
    public void deleteTeacher(Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new BusinessException("教师不存在"));
        AppUser user = teacher.getUser();
        teacherRepository.delete(teacher);
        if (user != null) {
            appUserRepository.delete(user);
        }
    }

    // ==================== 管理员管理 ====================

    /**
     * 创建管理员
     * @param request
     */
    @Transactional
    public void createAdmin(CreateAdminRequest request) {
        if (appUserRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessException("用户名已存在");
        }

        AppUser appUser = new AppUser();
        appUser.setUsername(request.getUsername());
        appUser.setPassword(passwordEncoder.encode(request.getPassword()));
        appUser.setRole("ROLE_ADMIN");
        appUser.setEnabled(true);
        appUser = appUserRepository.save(appUser);

        Admin admin = new Admin();
        admin.setName(request.getName());
        admin.setPhone(request.getPhone());
        admin.setUser(appUser);
        adminRepository.save(admin);
    }

    /**
     * 更新管理员信息
     * @param adminId
     * @param request
     */
    @Transactional
    public void updateAdmin(Long adminId, UpdateAdminRequest request) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException("管理员不存在"));
        if (request.getName() != null) {
            admin.setName(request.getName());
        }
        if (request.getPhone() != null) {
            admin.setPhone(request.getPhone());
        }
        adminRepository.save(admin);
    }

    /**
     * 删除管理员
     * @param adminId
     */
    @Transactional
    public void deleteAdmin(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException("管理员不存在"));
        AppUser user = admin.getUser();
        adminRepository.delete(admin);
        if (user != null) {
            appUserRepository.delete(user);
        }
    }

    // ==================== 用户状态管理 ====================

    /**
     * 切换用户状态
     * @param userId
     * @param enabled
     */
    @Transactional
    public void toggleUserStatus(Long userId, boolean enabled) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        user.setEnabled(enabled);
        appUserRepository.save(user);
    }

    // ==================== 用户列表（带角色筛选） ====================

    /**
     * 获取用户列表
     * @param role
     * @param pageable
     * @return
     */
    public Page<UserInfoResponse> listUsers(String role, Pageable pageable) {
        Specification<AppUser> spec = (root, query, cb) -> {
            if (role != null && !role.isEmpty()) {
                return cb.equal(root.get("role"), role);
            }
            return cb.conjunction();
        };
        Page<AppUser> page = appUserRepository.findAll(spec, pageable);
        return page.map(this::convertToUserInfoResponse);
    }

    // ==================== 课程管理 ====================

    /**
     * 创建课程
     * @param request
     * @return
     */
    @Transactional
    public CourseResponse createCourse(CreateCourseRequest request) {
        if (courseRepository.findByCourseCode(request.getCourseCode()).isPresent()) {
            throw new BusinessException("课程编号已存在");
        }

        Teacher teacher = null;
        if (request.getTeacherId() != null) {
            teacher = teacherRepository.findById(request.getTeacherId())
                    .orElseThrow(() -> new BusinessException("教师不存在"));
        }

        Course course = new Course();
        course.setCourseCode(request.getCourseCode());
        course.setName(request.getName());
        course.setCredit(request.getCredit());
        course.setTeacherEntity(teacher);
        if (teacher != null) {
            course.setTeacher(teacher.getName()); // 冗余字段
        }
        course.setSchedule(request.getSchedule());
        course.setCapacity(request.getCapacity());
        course.setSelectedCount(0);
        course.setDescription(request.getDescription());

        Course saved = courseRepository.save(course);
        courseSyncService.syncCourse(saved);
        return convertToCourseResponse(saved);
    }


    /**
     * 更新课程
     * @param courseId
     * @param request
     * @return
     */
    @Transactional
    public CourseResponse updateCourse(Long courseId, CreateCourseRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("课程不存在"));
        // 更新字段
        if (request.getName() != null) {
            course.setName(request.getName());
        }
        if (request.getCredit() != null) {
            course.setCredit(request.getCredit());
        }
        if (request.getSchedule() != null) {
            course.setSchedule(request.getSchedule());
        }
        if (request.getCapacity() != null) {
            course.setCapacity(request.getCapacity());
        }
        if (request.getDescription() != null) {
            course.setDescription(request.getDescription());
        }

        // 更新教师关联
        if (request.getTeacherId() != null) {
            Teacher teacher = teacherRepository.findById(request.getTeacherId())
                    .orElseThrow(() -> new BusinessException("教师不存在"));
            course.setTeacherEntity(teacher);
            course.setTeacher(teacher.getName());
        }
        Course saved = courseRepository.save(course);
        courseSyncService.syncCourse(saved);
        return convertToCourseResponse(saved);
    }

    /**
     * 删除课程
     * @param courseId
     */
    @Transactional
    public void deleteCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("课程不存在"));
        courseSyncService.deleteCourseSync(courseId);
        // 可选：删除选课记录
        courseRepository.delete(course);
    }

    private CourseResponse convertToCourseResponse(Course course) {
        CourseResponse response = new CourseResponse();
        response.setId(course.getId());
        response.setCourseCode(course.getCourseCode());
        response.setName(course.getName());
        response.setCredit(course.getCredit());
        response.setTeacher(course.getTeacher()); // 冗余教师姓名
        response.setSchedule(course.getSchedule());
        response.setCapacity(course.getCapacity());
        response.setSelectedCount(course.getSelectedCount());
        response.setDescription(course.getDescription());
        return response;
    }

    private UserInfoResponse convertToUserInfoResponse(AppUser user) {
        UserInfoResponse dto = new UserInfoResponse();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole()); // 允许 null
        dto.setEnabled(user.isEnabled());

        String role = user.getRole();
        if (role == null) {
            // 角色为空，直接返回，不进行后续查询
            return dto;
        }

        switch (role) {
            case "ROLE_STUDENT":
                studentRepository.findByUserId(user.getId()).ifPresent(s -> {
                    dto.setName(s.getName());
                    dto.setStudentNumber(s.getStudentNumber());
                });
                break;
            case "ROLE_TEACHER":
                teacherRepository.findByUserId(user.getId()).ifPresent(t -> {
                    dto.setName(t.getName());
                    dto.setTeacherNumber(t.getTeacherNumber());
                });
                break;
            case "ROLE_ADMIN":
                adminRepository.findByUserId(user.getId()).ifPresent(a -> {
                    dto.setName(a.getName());
                    dto.setPhone(a.getPhone());
                });
                break;
            default:
                // 未知角色，不处理
                break;
        }
        return dto;
    }

    /**
     * 获取选课记录
     * @param courseId
     * @param courseName
     * @param year
     * @param pageable
     * @return
     */
    public Page<AdminEnrollmentResponse> getEnrollments(Long courseId, String courseName, Integer year, Pageable pageable) {
        Page<Enrollment> page = enrollmentRepository.findEnrollmentsWithFilters(courseId, courseName, year, pageable);
        return page.map(this::convertToAdminEnrollmentResponse);
    }

    private AdminEnrollmentResponse convertToAdminEnrollmentResponse(Enrollment enrollment) {
        AdminEnrollmentResponse dto = new AdminEnrollmentResponse();
        dto.setEnrollmentId(enrollment.getId());
        dto.setStudentName(enrollment.getStudent().getName());
        dto.setStudentNumber(enrollment.getStudent().getStudentNumber());
        dto.setCourseName(enrollment.getCourse().getName());
        dto.setCourseCode(enrollment.getCourse().getCourseCode());
        dto.setYear(enrollment.getYear());
        dto.setEnrollmentTime(enrollment.getEnrollmentTime());
        dto.setStatus(enrollment.getStatus());
        dto.setGrade(enrollment.getGrade());
        dto.setRegularGrade(enrollment.getRegularGrade());
        dto.setExamGrade(enrollment.getExamGrade());
        return dto;
    }
}