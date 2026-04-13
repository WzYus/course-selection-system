package cn.detect.cs.controller;

import cn.detect.cs.dto.BaseRegisterRequest;
import cn.detect.cs.dto.StudentProfileRequest;
import cn.detect.cs.dto.TeacherProfileRequest;
import cn.detect.cs.entity.AppUser;
import cn.detect.cs.entity.Student;
import cn.detect.cs.entity.Teacher;
import cn.detect.cs.common.BusinessException;
import cn.detect.cs.repository.AppUserRepository;
import cn.detect.cs.repository.StudentRepository;
import cn.detect.cs.repository.TeacherRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/register")
@RequiredArgsConstructor
public class RegisterController {

    private final AppUserRepository appUserRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 基础注册（创建账号）
     * @param request
     * @return
     */
    @PostMapping("/init")
    public ResponseEntity<?> initRegister(@Valid @RequestBody BaseRegisterRequest request) {
        if (appUserRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessException("用户名已存在");
        }

        AppUser appUser = new AppUser();
        appUser.setUsername(request.getUsername());
        appUser.setPassword(passwordEncoder.encode(request.getPassword()));
        appUser.setRole(request.getRole());
        appUser = appUserRepository.save(appUser);

        return ResponseEntity.ok(Map.of(
                "message", "基础账户创建成功，请完善个人信息",
                "userId", appUser.getId(),
                "role", appUser.getRole()
        ));
    }

    /**
     * 完善学生信息
     * @param userId
     * @param request
     * @return
     */
    @PostMapping("/student/{userId}")
    @Transactional
    public ResponseEntity<?> completeStudent(@PathVariable Long userId,
                                             @Valid @RequestBody StudentProfileRequest request) {
        AppUser appUser = appUserRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        if (!"ROLE_STUDENT".equals(appUser.getRole())) {
            throw new BusinessException("该用户角色不是学生，无法完善学生信息");
        }

        if (studentRepository.findByUserId(userId).isPresent()) {
            throw new BusinessException("学生信息已存在，请勿重复提交");
        }

        if (studentRepository.findByStudentNumber(request.getStudentNumber()).isPresent()) {
            throw new BusinessException("学号已存在");
        }

        Student student = new Student();
        student.setName(request.getName());
        student.setAge(request.getAge());
        student.setStudentNumber(request.getStudentNumber());
        student.setMajor(request.getMajor());
        student.setGrade(request.getGrade());
        student.setUser(appUser);
        student.setEmail(request.getEmail());
        studentRepository.save(student);

        return ResponseEntity.ok("学生信息完善成功");
    }

    /**
     * 完善教师信息
     * @param userId
     * @param request
     * @return
     */
    @PostMapping("/teacher/{userId}")
    @Transactional
    public ResponseEntity<?> completeTeacher(@PathVariable Long userId,
                                             @Valid @RequestBody TeacherProfileRequest request) {
        AppUser appUser = appUserRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        if (!"ROLE_TEACHER".equals(appUser.getRole())) {
            throw new BusinessException("该用户角色不是教师，无法完善教师信息");
        }

        if (teacherRepository.findByUserId(userId).isPresent()) {
            throw new BusinessException("教师信息已存在，请勿重复提交");
        }

        if (teacherRepository.findByTeacherNumber(request.getTeacherNumber()).isPresent()) {
            throw new BusinessException("工号已存在");
        }

        Teacher teacher = new Teacher();
        teacher.setName(request.getName());
        teacher.setTeacherNumber(request.getTeacherNumber());
        teacher.setTitle(request.getTitle());
        teacher.setDepartment(request.getDepartment());
        teacher.setUser(appUser);
        teacherRepository.save(teacher);

        return ResponseEntity.ok("教师信息完善成功");
    }
}