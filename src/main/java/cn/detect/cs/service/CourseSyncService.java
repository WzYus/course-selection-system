package cn.detect.cs.service;

import cn.detect.cs.document.CourseDocument;
import cn.detect.cs.entity.Course;
import cn.detect.cs.repository.CourseRepository;
import cn.detect.cs.repository.elastic.CourseDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseSyncService {

    private final CourseDocumentRepository courseDocumentRepository;
    private final CourseRepository courseRepository;  // 可选，用于全量同步

    // 单个课程同步（新增或更新）
    public void syncCourse(Course course) {
        if (course == null) return;
        CourseDocument doc = convertToDocument(course);
        courseDocumentRepository.save(doc);
    }

    // 删除课程同步
    public void deleteCourseSync(Long courseId) {
        courseDocumentRepository.deleteById(courseId);
    }

    // 全量同步（初始化时使用）
    public void syncAllCourses() {
        List<Course> courses = courseRepository.findAll();
        List<CourseDocument> documents = courses.stream()
                .map(this::convertToDocument)
                .collect(Collectors.toList());
        courseDocumentRepository.saveAll(documents);
    }

    private CourseDocument convertToDocument(Course course) {
        CourseDocument doc = new CourseDocument();
        doc.setId(course.getId());
        doc.setCourseCode(course.getCourseCode());
        doc.setName(course.getName());
        doc.setTeacher(course.getTeacher());
        doc.setCredit(course.getCredit());
        doc.setSchedule(course.getSchedule());
        doc.setCapacity(course.getCapacity());
        doc.setSelectedCount(course.getSelectedCount());
        doc.setDescription(course.getDescription());
        return doc;
    }
}