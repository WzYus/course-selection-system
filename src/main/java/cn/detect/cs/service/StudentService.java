package cn.detect.cs.service;

import cn.detect.cs.common.BusinessException;
import cn.detect.cs.dto.PasswordChangeRequest;
import cn.detect.cs.dto.StudentProfileResponse;
import cn.detect.cs.dto.StudentProfileUpdateRequest;
import cn.detect.cs.entity.AppUser;
import cn.detect.cs.entity.Student;
import cn.detect.cs.repository.AppUserRepository;
import cn.detect.cs.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 获取个人信息
     * @param userId
     * @return
     */
    public StudentProfileResponse getProfile(Long userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("学生信息不存在，请先完善资料"));
        return mapToResponse(student);
    }

    /**
     * 更新个人信息
     * @param userId
     * @param request
     * @return
     */
    public StudentProfileResponse updateProfile(Long userId, StudentProfileUpdateRequest request) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("学生信息不存在，请先完善资料"));

        student.setName(request.getName());
        student.setAge(request.getAge());
        student.setMajor(request.getMajor());
        student.setGrade(request.getGrade());

        if (request.getEmail() != null) {
            // 可选：增加唯一性校验
            if (!request.getEmail().equals(student.getEmail()) &&
                    studentRepository.existsByEmail(request.getEmail())) {
                throw new BusinessException("邮箱已被使用");
            }
            student.setEmail(request.getEmail());
        }

        studentRepository.save(student);
        return mapToResponse(student);
    }

    /**
     * 修改密码
     * @param userId
     * @param request
     */
    public void changePassword(Long userId, PasswordChangeRequest request) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("旧密码错误");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * 实体转DTO
     * @param student
     * @return
     */
    private StudentProfileResponse mapToResponse(Student student) {
        StudentProfileResponse response = new StudentProfileResponse();
        response.setId(student.getId());
        response.setName(student.getName());
        response.setAge(student.getAge());
        response.setStudentNumber(student.getStudentNumber());
        response.setMajor(student.getMajor());
        response.setGrade(student.getGrade());
        response.setUsername(student.getUser().getUsername());
        return response;
    }
}