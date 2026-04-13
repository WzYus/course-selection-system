package cn.detect.cs.controller;

import cn.detect.cs.dto.CourseResponse;
import cn.detect.cs.dto.CreateCourseRequest;
import cn.detect.cs.service.CourseService;
import cn.detect.cs.service.CourseSyncService;
import cn.detect.cs.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final SecurityUtil securityUtil;
    private final CourseSyncService courseSyncService;

    /*
    @GetMapping("/sync-es")
    public String syncES() {
        courseSyncService.syncAllCourses();
        return "同步完成";
    }*/

    /**
     * 搜索课程
     * @param keyword
     * @param minCredit
     * @param maxCredit
     * @return
     */
    @GetMapping
    public ResponseEntity<Page<CourseResponse>> searchCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer minCredit,
            @RequestParam(required = false) Integer maxCredit,
            Pageable pageable) {
        Page<CourseResponse> courses = courseService.searchCourses(keyword, minCredit, maxCredit, pageable);
        return ResponseEntity.ok(courses);
    }

    /**
     * 根据ID获取课程详情
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id) {
        CourseResponse course = courseService.getCourseById(id);
        return ResponseEntity.ok(course);
    }

    /**
     * 获取当前教师所教的课程列表
     * @param pageable
     * @return
     */
    @GetMapping("/teacher")
    public ResponseEntity<Page<CourseResponse>> getMyCourses(Pageable pageable) {
        Long teacherId = securityUtil.getCurrentTeacherId();
        return ResponseEntity.ok(courseService.getCoursesByTeacher(teacherId, pageable));
    }

    /**
     * 教师创建课程
     * @param request
     * @return
     */
    @PostMapping("/teacher/courses")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<CourseResponse> createCourseByTeacher(@Valid @RequestBody CreateCourseRequest request) {
        Long teacherId = securityUtil.getCurrentTeacherId();
        CourseResponse response = courseService.createCourseByTeacher(teacherId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}