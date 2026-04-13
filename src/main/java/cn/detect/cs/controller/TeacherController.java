package cn.detect.cs.controller;

import cn.detect.cs.dto.TeacherProfileResponse;
import cn.detect.cs.dto.TeacherProfileUpdateRequest;
import cn.detect.cs.dto.PasswordChangeRequest;
import cn.detect.cs.service.TeacherService;
import cn.detect.cs.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
@PreAuthorize("hasRole('TEACHER')")
public class TeacherController {

    private final TeacherService teacherService;
    private final SecurityUtil securityUtil;

    /**
     * 获取教师个人信息
     * @return
     */
    @GetMapping("/profile")
    public ResponseEntity<TeacherProfileResponse> getProfile() {
        Long teacherId = securityUtil.getCurrentTeacherId();
        return ResponseEntity.ok(teacherService.getProfile(teacherId));
    }

    /**
     * 更新教师个人信息
     * @param request
     * @return
     */
    @PutMapping("/profile")
    public ResponseEntity<TeacherProfileResponse> updateProfile(@Valid @RequestBody TeacherProfileUpdateRequest request) {
        Long teacherId = securityUtil.getCurrentTeacherId();
        return ResponseEntity.ok(teacherService.updateProfile(teacherId, request));
    }

    /**
     * 修改密码
     * @param request
     * @return
     */
    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        Long userId = securityUtil.getCurrentUserId();
        teacherService.changePassword(userId, request);
        return ResponseEntity.ok(Map.of("message", "密码修改成功"));
    }
}