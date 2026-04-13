package cn.detect.cs.controller;

import cn.detect.cs.dto.PasswordChangeRequest;
import cn.detect.cs.dto.StudentProfileResponse;
import cn.detect.cs.dto.StudentProfileUpdateRequest;
import cn.detect.cs.entity.AppUser;
import cn.detect.cs.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentProfileController {

    private final StudentService studentService;

    // ==================== 个人信息管理 ====================

    /**
     * 获取个人信息
     * @return
     */
    @GetMapping("/profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentProfileResponse> getProfile() {
        Long currentUserId = getCurrentUserId();
        StudentProfileResponse profile = studentService.getProfile(currentUserId);
        return ResponseEntity.ok(profile);
    }

    /**
     * 更新个人信息
     * @param request
     * @return
     */
    @PutMapping("/profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentProfileResponse> updateProfile(
            @Valid @RequestBody StudentProfileUpdateRequest request) {
        Long currentUserId = getCurrentUserId();
        StudentProfileResponse updated = studentService.updateProfile(currentUserId, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * 修改密码
     * @param request
     * @return
     */
    @PutMapping("/password")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        Long currentUserId = getCurrentUserId();
        studentService.changePassword(currentUserId, request);
        return ResponseEntity.ok("密码修改成功");
    }

    private Long getCurrentUserId() {
        AppUser userDetails = (AppUser) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userDetails.getId();
    }
}