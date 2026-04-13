package cn.detect.cs.repository;

import cn.detect.cs.entity.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    /**
     * 查询选课记录（支持按课程ID、课程名称、学年筛选）
     * @param courseId
     * @param courseName
     * @param year
     * @param pageable
     * @return
     */
    @Query("SELECT e FROM Enrollment e JOIN FETCH e.student s JOIN FETCH e.course c WHERE " +
            "(:courseId IS NULL OR c.id = :courseId) AND " +
            "(:courseName IS NULL OR c.name LIKE %:courseName%) AND " +
            "(:year IS NULL OR e.year = :year)")
    Page<Enrollment> findEnrollmentsWithFilters(@Param("courseId") Long courseId,
                                                @Param("courseName") String courseName,
                                                @Param("year") Integer year,
                                                Pageable pageable);

    /**
     * 分页查询某课程已选课的学生（支持按学年、姓名筛选）
     * @param courseId
     * @param year
     * @param name
     * @param pageable
     * @return
     */
    @Query("SELECT e FROM Enrollment e JOIN FETCH e.student s " +
            "WHERE e.course.id = :courseId AND e.status = 'SELECTED' " +
            "AND (:year IS NULL OR e.year = :year) " +
            "AND (:name IS NULL OR s.name LIKE %:name%)")
    Page<Enrollment> findSelectedByCourseIdWithFilters(@Param("courseId") Long courseId,
                                                       @Param("year") Integer year,
                                                       @Param("name") String name,
                                                       Pageable pageable);

    /**
     * 查询某课程已选课的学生列表（支持按学年、姓名筛选，不分页）
     * @param courseId
     * @param year
     * @param name
     * @return
     */
    @Query("SELECT e FROM Enrollment e JOIN FETCH e.student s " +
            "WHERE e.course.id = :courseId AND e.status = 'SELECTED' " +
            "AND (:year IS NULL OR e.year = :year) " +
            "AND (:name IS NULL OR s.name LIKE %:name%)")
    List<Enrollment> findSelectedByCourseIdWithFilters(@Param("courseId") Long courseId,
                                                       @Param("year") Integer year,
                                                       @Param("name") String name);

    /**
     * 检查学生是否已选某课程（状态为 SELECTED）
     * @param studentId
     * @param courseId
     * @return
     */
    @Query("SELECT COUNT(e) > 0 FROM Enrollment e WHERE e.student.id = :studentId AND e.course.id = :courseId AND e.status = 'SELECTED'")
    boolean existsByStudentIdAndCourseIdAndStatusSelected(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    /**
     * 根据学生ID和课程ID查询选课记录（不限状态）
     * @param studentId
     * @param courseId
     * @return
     */
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);

    /**
     * 查询学生所有已选课程（状态为 SELECTED），并预加载课程信息
     * @param studentId
     * @return
     */
    @Query("SELECT e FROM Enrollment e JOIN FETCH e.course WHERE e.student.id = :studentId AND e.status = 'SELECTED'")
    List<Enrollment> findSelectedByStudentId(@Param("studentId") Long studentId);

    /**
     * 查询某课程所有选课学生（状态为 SELECTED），并预加载学生信息
     * @param courseId
     * @return
     */
    @Query("SELECT e FROM Enrollment e JOIN FETCH e.student WHERE e.course.id = :courseId AND e.status = 'SELECTED'")
    List<Enrollment> findSelectedByCourseId(@Param("courseId") Long courseId);

    /**
     * 查询学生某学年的已选课程（状态 SELECTED）
     * @param studentId
     * @param year
     * @return
     */
    @Query("SELECT e FROM Enrollment e JOIN FETCH e.course WHERE e.student.id = :studentId AND e.status = 'SELECTED' AND e.year = :year")
    List<Enrollment> findSelectedByStudentIdAndYear(@Param("studentId") Long studentId, @Param("year") Integer year);

    /**
     * 查询某课程某学年的选课学生（状态 SELECTED）
     * @param courseId
     * @param year
     * @return
     */
    @Query("SELECT e FROM Enrollment e JOIN FETCH e.student WHERE e.course.id = :courseId AND e.status = 'SELECTED' AND e.year = :year")
    List<Enrollment> findSelectedByCourseIdAndYear(@Param("courseId") Long courseId, @Param("year") Integer year);

    /**
     * 退课：更新状态为 DROPPED
     * @param enrollmentId
     * @return
     */
    @Transactional
    @Modifying
    @Query("UPDATE Enrollment e SET e.status = 'DROPPED' WHERE e.id = :enrollmentId AND e.status = 'SELECTED'")
    int dropEnrollment(@Param("enrollmentId") Long enrollmentId);

    /**
     * 查询学生所有选课记录（不限状态）
     * @param studentId
     * @return
     */
    @Query("SELECT e FROM Enrollment e JOIN FETCH e.course WHERE e.student.id = :studentId")
    List<Enrollment> findAllByStudentId(@Param("studentId") Long studentId);
}