package cn.detect.cs.repository;

import cn.detect.cs.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    /**
     * 根据关联的用户ID查询教师信息
     * @param userId AppUser 的 ID
     * @return 教师信息（可能为空）
     */
    Optional<Teacher> findByUserId(Long userId);

    /**
     * 根据工号查询教师（唯一性校验）
     * @param teacherNumber 工号
     * @return 教师信息（可能为空）
     */
    Optional<Teacher> findByTeacherNumber(String teacherNumber);
}