package cn.detect.cs.service;

import cn.detect.cs.document.CourseDocument;
import cn.detect.cs.dto.CourseResponse;
import cn.detect.cs.dto.CreateCourseRequest;
import cn.detect.cs.entity.Course;
import cn.detect.cs.common.BusinessException;
import cn.detect.cs.entity.Teacher;
import cn.detect.cs.repository.CourseRepository;
import cn.detect.cs.repository.TeacherRepository;
import cn.detect.cs.repository.elastic.CourseDocumentRepository;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final CourseSyncService courseSyncService;
    private final CourseDocumentRepository courseDocumentRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    // ==================== 基于 Elasticsearch 的搜索（使用 NativeSearchQueryBuilder） ====================
    public Page<CourseResponse> searchCourses(String keyword, Integer minCredit, Integer maxCredit, Pageable pageable) {
        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();

        // 关键词搜索（OR 条件）
        if (keyword != null && !keyword.trim().isEmpty()) {
            boolBuilder.should(m -> m.match(t -> t.field("name").query(keyword)))
                    .should(m -> m.match(t -> t.field("teacher").query(keyword)))
                    .should(m -> m.match(t -> t.field("description").query(keyword)))
                    .minimumShouldMatch("1");
        }

        // 学分范围过滤（AND 条件）
        if (minCredit != null) {
            boolBuilder.filter(f -> f.range(r -> r.number(n -> n.field("credit").gte(minCredit.doubleValue()))));
        }
        if (maxCredit != null) {
            boolBuilder.filter(f -> f.range(r -> r.number(n -> n.field("credit").lte(maxCredit.doubleValue()))));
        }

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(q -> q.bool(boolBuilder.build()))
                .withPageable(pageable)
                .build();

        SearchHits<CourseDocument> searchHits = elasticsearchOperations.search(nativeQuery, CourseDocument.class);
        List<CourseResponse> content = searchHits.getSearchHits().stream()
                .map(hit -> convertDocumentToResponse(hit.getContent()))
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, searchHits.getTotalHits());
    }

    // 重载方法，兼容原有接口（name 和 teacher 作为关键词）
    public Page<CourseResponse> searchCourses(String name, String teacher, Integer minCredit, Integer maxCredit, Pageable pageable) {
        StringBuilder keywordBuilder = new StringBuilder();
        if (name != null) keywordBuilder.append(name).append(" ");
        if (teacher != null) keywordBuilder.append(teacher);
        String keyword = keywordBuilder.toString().trim();
        return searchCourses(keyword.isEmpty() ? null : keyword, minCredit, maxCredit, pageable);
    }

    // 降级 MySQL 搜索（可选）
    public Page<CourseResponse> searchCoursesMySQL(String name, String teacher, Integer minCredit, Integer maxCredit, Pageable pageable) {
        Page<Course> coursePage = courseRepository.searchCourses(name, teacher, minCredit, maxCredit, pageable);
        return coursePage.map(this::convertToResponse);
    }

    // ==================== 其他业务方法 ====================
    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new BusinessException("课程不存在"));
        return convertToResponse(course);
    }

    public Page<CourseResponse> getCoursesByTeacher(Long teacherId, Pageable pageable) {
        return courseRepository.findByTeacherEntityId(teacherId, pageable)
                .map(this::convertToResponse);
    }

    @Transactional
    public CourseResponse createCourseByTeacher(Long teacherId, CreateCourseRequest request) {
        if (courseRepository.findByCourseCode(request.getCourseCode()).isPresent()) {
            throw new BusinessException("课程编号已存在");
        }
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new BusinessException("教师不存在"));

        Course course = new Course();
        course.setCourseCode(request.getCourseCode());
        course.setName(request.getName());
        course.setCredit(request.getCredit());
        course.setTeacherEntity(teacher);
        course.setTeacher(teacher.getName());
        course.setSchedule(request.getSchedule());
        course.setCapacity(request.getCapacity());
        course.setSelectedCount(0);
        course.setDescription(request.getDescription());

        Course saved = courseRepository.save(course);
        courseSyncService.syncCourse(saved);
        return convertToResponse(saved);
    }

    // ==================== 私有辅助方法 ====================
    private CourseResponse convertToResponse(Course course) {
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

    private CourseResponse convertDocumentToResponse(CourseDocument doc) {
        CourseResponse response = new CourseResponse();
        response.setId(doc.getId());
        response.setCourseCode(doc.getCourseCode());
        response.setName(doc.getName());
        response.setCredit(doc.getCredit());
        response.setTeacher(doc.getTeacher());
        response.setSchedule(doc.getSchedule());
        response.setCapacity(doc.getCapacity());
        response.setSelectedCount(doc.getSelectedCount());
        response.setDescription(doc.getDescription());
        return response;
    }
}