package cn.detect.cs.controller;

import cn.detect.cs.dto.*;
import cn.detect.cs.service.EnrollmentService;
import cn.detect.cs.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final SecurityUtil securityUtil;

    /**
     * 学生选课
     * @param courseId
     * @return
     */
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> enroll(@RequestParam Long courseId) {
        Long studentId = securityUtil.getCurrentUserId();
        enrollmentService.enroll(studentId, courseId);
        return ResponseEntity.ok(Map.of("message", "选课成功"));
    }

    /**
     * 学生退课
     * @param courseId
     * @return
     */
    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> drop(@PathVariable Long courseId) {
        Long studentId = securityUtil.getCurrentUserId();
        enrollmentService.drop(studentId, courseId);
        return ResponseEntity.ok(Map.of("message", "退课成功"));
    }

    /**
     * 查看当前学生已选课程列表
     * @param year
     * @return
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<CourseResponse>> getMyCourses(
            @RequestParam(required = false) Integer year) {
        Long userId = securityUtil.getCurrentUserId();
        List<CourseResponse> courses;
        if (year != null) {
            courses = enrollmentService.getMyCoursesByYear(userId, year);
        } else {
            courses = enrollmentService.getMyCourses(userId);
        }
        return ResponseEntity.ok(courses);
    }

    /**
     * 查看我的课表
     * @return
     */
    @GetMapping("/timetable")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<TimetableItem>> getMyTimetable() {
        Long userId = securityUtil.getCurrentUserId();
        List<TimetableItem> timetable = enrollmentService.getMyTimetable(userId);
        return ResponseEntity.ok(timetable);
    }

    /**
     * 教师查看所授课程的学生列表
     * @param courseId
     * @param year
     * @param name
     * @param pageable
     * @return
     */
    @GetMapping("/course/{courseId}/students")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Page<StudentGradeDTO>> getCourseStudents(
            @PathVariable Long courseId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String name,
            Pageable pageable) {
        Long teacherId = securityUtil.getCurrentTeacherId();
        return ResponseEntity.ok(enrollmentService.getCourseStudentsWithFilters(courseId, teacherId, year, name, pageable));
    }

    /**
     * 教师更新学生成绩
     * @param enrollmentId
     * @param request
     * @return
     */
    @PutMapping("/grades/{enrollmentId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> updateGrade(@PathVariable Long enrollmentId,
                                         @Valid @RequestBody GradeUpdateRequest request) {
        Long teacherId = securityUtil.getCurrentTeacherId();
        enrollmentService.updateGrade(enrollmentId, teacherId, request);
        return ResponseEntity.ok(Map.of("message", "成绩更新成功"));
    }

    /**
     * 教师为学生选课
     * @param request
     * @return
     */
    @PostMapping("/teacher/enroll")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> enrollForStudent(@Valid @RequestBody TeacherEnrollRequest request) {
        System.out.println("接收到的请求: studentNumber=" + request.getStudentNumber() + ", courseCode=" + request.getCourseCode());
        Long teacherId = securityUtil.getCurrentTeacherId();
        enrollmentService.enrollByTeacher(teacherId, request.getStudentNumber(), request.getCourseCode());
        return ResponseEntity.ok(Map.of("message", "选课成功"));
    }
}