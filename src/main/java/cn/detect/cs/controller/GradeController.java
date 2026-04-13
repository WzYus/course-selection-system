package cn.detect.cs.controller;

import cn.detect.cs.dto.GradeResponse;
import cn.detect.cs.service.EnrollmentService;
import cn.detect.cs.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeController {

    private final EnrollmentService enrollmentService;
    private final SecurityUtil securityUtil;

    /**
     * 获取当前学生的成绩列表
     * @return
     */
    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<GradeResponse>> getMyGrades() {
        Long userId = securityUtil.getCurrentUserId();
        List<GradeResponse> grades = enrollmentService.getMyGrades(userId);
        return ResponseEntity.ok(grades);
    }
}