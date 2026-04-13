package cn.detect.cs.entity;

import cn.detect.cs.common.MessageConstants;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = MessageConstants.STUDENT_NAME_REQUIRED)
    private String name;

    @Min(value = 1, message = MessageConstants.STUDENT_AGE_INVALID)
    private Integer age;

    @Column(unique = true)
    private String studentNumber;  // 学号

    private String major;          // 专业
    private String grade;          // 年级

    @Column(nullable = false, unique = true)
    private String email;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private AppUser user;          // 关联的认证用户
}