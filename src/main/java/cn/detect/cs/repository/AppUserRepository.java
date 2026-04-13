package cn.detect.cs.repository;

import cn.detect.cs.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long>, JpaSpecificationExecutor<AppUser> {

    /**
     * 根据用户名查找用户
     * @param username
     * @return
     */
    Optional<AppUser> findByUsername(String username);
}