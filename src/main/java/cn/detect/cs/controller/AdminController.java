package cn.detect.cs.controller;

import cn.detect.cs.dto.*;
import cn.detect.cs.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    // ==================== 用户管理 ====================

    /**
     * 获取用户列表（按角色筛选）
     * @param role
     * @param pageable
     * @return
     */
    @GetMapping("/users")
    public ResponseEntity<Page<UserInfoResponse>> listUsers(
            @RequestParam(required = false) String role,
            Pageable pageable) {
        return ResponseEntity.ok(adminService.listUsers(role, pageable));
    }

    /**
     * 切换用户状态
     * @param userId
     * @param enabled
     * @return
     */
    @PutMapping("/users/{userId}/status")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long userId, @RequestParam boolean enabled) {
        adminService.toggleUserStatus(userId, enabled);
        return ResponseEntity.ok(Map.of("message", "用户状态更新成功"));
    }

    // ==================== 学生管理 ====================

    /**
     * 创建学生
     * @param request
     * @return
     */
    @PostMapping("/students")
    public ResponseEntity<?> createStudent(@Valid @RequestBody CreateStudentRequest request) {
        adminService.createStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "学生创建成功"));
    }

    /**
     * 更新学生信息
     * @param studentId
     * @param request
     * @return
     */
    @PutMapping("/students/{studentId}")
    public ResponseEntity<?> updateStudent(@PathVariable Long studentId, @Valid @RequestBody UpdateStudentRequest request) {
        adminService.updateStudent(studentId, request);
        return ResponseEntity.ok(Map.of("message", "学生信息更新成功"));
    }

    /**
     * 删除学生
     * @param studentId
     * @return
     */
    @DeleteMapping("/students/{studentId}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long studentId) {
        adminService.deleteStudent(studentId);
        return ResponseEntity.ok(Map.of("message", "学生删除成功"));
    }

    // ==================== 教师管理 ====================

    /**
     * 创建教师
     * @param request
     * @return
     */
    @PostMapping("/teachers")
    public ResponseEntity<?> createTeacher(@Valid @RequestBody CreateTeacherRequest request) {
        adminService.createTeacher(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "教师创建成功"));
    }

    /**
     * 更新教师信息
     * @param teacherId
     * @param request
     * @return
     */
    @PutMapping("/teachers/{teacherId}")
    public ResponseEntity<?> updateTeacher(@PathVariable Long teacherId, @Valid @RequestBody UpdateTeacherRequest request) {
        adminService.updateTeacher(teacherId, request);
        return ResponseEntity.ok(Map.of("message", "教师信息更新成功"));
    }

    /**
     * 删除教师
     * @param teacherId
     * @return
     */
    @DeleteMapping("/teachers/{teacherId}")
    public ResponseEntity<?> deleteTeacher(@PathVariable Long teacherId) {
        adminService.deleteTeacher(teacherId);
        return ResponseEntity.ok(Map.of("message", "教师删除成功"));
    }

    // ==================== 管理员管理 ====================

    /**
     * 创建管理员
     * @param request
     * @return
     */
    @PostMapping("/admins")
    public ResponseEntity<?> createAdmin(@Valid @RequestBody CreateAdminRequest request) {
        adminService.createAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "管理员创建成功"));
    }

    /**
     * 更新管理员信息
     * @param adminId
     * @param request
     * @return
     */
    @PutMapping("/admins/{adminId}")
    public ResponseEntity<?> updateAdmin(@PathVariable Long adminId, @Valid @RequestBody UpdateAdminRequest request) {
        adminService.updateAdmin(adminId, request);
        return ResponseEntity.ok(Map.of("message", "管理员信息更新成功"));
    }

    /**
     * 删除管理员
     * @param adminId
     * @return
     */
    @DeleteMapping("/admins/{adminId}")
    public ResponseEntity<?> deleteAdmin(@PathVariable Long adminId) {
        adminService.deleteAdmin(adminId);
        return ResponseEntity.ok(Map.of("message", "管理员删除成功"));
    }

    // ==================== 课程管理 ====================

    /**
     * 创建课程
     * @param request
     * @return
     */
    @PostMapping("/courses")
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CreateCourseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createCourse(request));
    }

    /**
     * 更新课程
     * @param courseId
     * @param request
     * @return
     */
    @PutMapping("/courses/{courseId}")
    public ResponseEntity<CourseResponse> updateCourse(@PathVariable Long courseId, @Valid @RequestBody CreateCourseRequest request) {
        return ResponseEntity.ok(adminService.updateCourse(courseId, request));
    }

    /**
     * 删除课程
     * @param courseId
     * @return
     */
    @DeleteMapping("/courses/{courseId}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long courseId) {
        adminService.deleteCourse(courseId);
        return ResponseEntity.ok(Map.of("message", "课程删除成功"));
    }

    // ==================== 选课管理 ====================

    /**
     * 获取选课记录
     * @param courseId
     * @param courseName
     * @param year
     * @param pageable
     * @return
     */
    @GetMapping("/enrollments")
    public ResponseEntity<Page<AdminEnrollmentResponse>> getEnrollments(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) Integer year,
            Pageable pageable) {
        return ResponseEntity.ok(adminService.getEnrollments(courseId, courseName, year, pageable));
    }
}