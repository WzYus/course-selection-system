package cn.detect.cs.repository;

import cn.detect.cs.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * 搜索课程（支持按名称、教师、学分范围筛选）
     * @param name
     * @param teacher
     * @param minCredit
     * @param maxCredit
     * @param pageable
     * @return
     */
    @Query("SELECT c FROM Course c WHERE " +
            "(:name IS NULL OR c.name LIKE %:name%) AND " +
            "(:teacher IS NULL OR c.teacher LIKE %:teacher%) AND " +
            "(:minCredit IS NULL OR c.credit >= :minCredit) AND " +
            "(:maxCredit IS NULL OR c.credit <= :maxCredit)")
    Page<Course> searchCourses(@Param("name") String name,
                               @Param("teacher") String teacher,
                               @Param("minCredit") Integer minCredit,
                               @Param("maxCredit") Integer maxCredit,
                               Pageable pageable);

    /**
     * 分页查询课程（按名称模糊搜索）
     * @param name
     * @param pageable
     * @return
     */
    Page<Course> findByNameContaining(String name, Pageable pageable);

    /**
     * 增加课程的已选人数（选课时调用）
     * @param courseId
     * @return 影响行数
     */
    @Modifying
    @Transactional
    @Query("UPDATE Course c SET c.selectedCount = c.selectedCount + 1 WHERE c.id = :courseId AND c.selectedCount < c.capacity")
    int incrementSelectedCount(@Param("courseId") Long courseId);

    /**
     * 减少课程的已选人数（退课时调用）
     * @param courseId
     * @return 影响行数
     */
    @Modifying
    @Transactional
    @Query("UPDATE Course c SET c.selectedCount = c.selectedCount - 1 WHERE c.id = :courseId AND c.selectedCount > 0")
    int decrementSelectedCount(@Param("courseId") Long courseId);

    /**
     * 根据教师ID分页查询课程
     * @param teacherId
     * @param pageable
     * @return
     */
    Page<Course> findByTeacherEntityId(Long teacherId, Pageable pageable);

    /**
     * 根据课程ID和教师ID查询课程
     * @param id
     * @param teacherId
     * @return
     */
    Optional<Course> findByIdAndTeacherEntityId(Long id, Long teacherId);

    /**
     * 根据课程编号查询课程
     * @param courseCode
     * @return
     */
    Optional<Course> findByCourseCode(String courseCode);
}