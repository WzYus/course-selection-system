package cn.detect.cs.repository;

import cn.detect.cs.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    /**
     * 根据用户ID查找学生
     * @param userId
     * @return
     */
    Optional<Student> findByUserId(Long userId);

    /**
     * 根据学号查找学生
     * @param studentNumber
     * @return
     */
    Optional<Student> findByStudentNumber(String studentNumber);

    /**
     *
     * @param email
     * @return
     */
    boolean existsByEmail(String email);
}