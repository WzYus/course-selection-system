package cn.detect.cs.service;

import cn.detect.cs.dto.TeacherProfileResponse;
import cn.detect.cs.dto.TeacherProfileUpdateRequest;
import cn.detect.cs.dto.PasswordChangeRequest;
import cn.detect.cs.entity.AppUser;
import cn.detect.cs.entity.Teacher;
import cn.detect.cs.common.BusinessException;
import cn.detect.cs.repository.AppUserRepository;
import cn.detect.cs.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 获取教师个人信息
     * @param teacherId
     * @return
     */
    public TeacherProfileResponse getProfile(Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new BusinessException("教师不存在"));
        return mapToResponse(teacher);
    }

    /**
     * 更新教师个人信息
     * @param teacherId
     * @param request
     * @return
     */
    @Transactional
    public TeacherProfileResponse updateProfile(Long teacherId, TeacherProfileUpdateRequest request) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new BusinessException("教师不存在"));
        if (request.getName() != null) {
            teacher.setName(request.getName());
        }
        if (request.getTitle() != null) {
            teacher.setTitle(request.getTitle());
        }
        if (request.getDepartment() != null) {
            teacher.setDepartment(request.getDepartment());
        }
        teacherRepository.save(teacher);
        return mapToResponse(teacher);
    }

    /**
     * 修改密码
     * @param userId
     * @param request
     * @return
     */
    @Transactional
    public void changePassword(Long userId, PasswordChangeRequest request) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("旧密码错误");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        appUserRepository.save(user);
    }

    private TeacherProfileResponse mapToResponse(Teacher teacher) {
        TeacherProfileResponse response = new TeacherProfileResponse();
        response.setId(teacher.getId());
        response.setTeacherNumber(teacher.getTeacherNumber());
        response.setName(teacher.getName());
        response.setTitle(teacher.getTitle());
        response.setDepartment(teacher.getDepartment());
        response.setUsername(teacher.getUser().getUsername());
        return response;
    }
}