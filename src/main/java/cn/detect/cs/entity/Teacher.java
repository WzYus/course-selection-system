package cn.detect.cs.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String teacherNumber;  // 工号

    @NotBlank
    private String name;           // 姓名

    private String title;          // 职称（教授、副教授等）
    private String department;     // 所属院系

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private AppUser user;          // 关联的认证用户
}