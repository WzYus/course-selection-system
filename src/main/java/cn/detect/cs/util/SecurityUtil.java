package cn.detect.cs.util;

import cn.detect.cs.entity.AppUser;
import cn.detect.cs.common.BusinessException;
import cn.detect.cs.entity.Teacher;
import cn.detect.cs.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("未认证");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof AppUser) {
            return ((AppUser) principal).getId();
        }
        throw new BusinessException("无法获取当前用户信息");
    }

    @Autowired
    private TeacherRepository teacherRepository;

    public Long getCurrentTeacherId() {
        Long userId = getCurrentUserId();
        return teacherRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("当前用户不是教师"))
                .getId();
    }
}