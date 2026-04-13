package cn.detect.cs.repository;

import cn.detect.cs.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    /**
     * 根据用户ID查找管理员
     * @param userId
     * @return
     */
    Optional<Admin> findByUserId(Long userId);
}