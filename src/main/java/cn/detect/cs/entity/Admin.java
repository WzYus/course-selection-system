package cn.detect.cs.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;           // 姓名

    private String phone;          // 联系电话（可选）

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private AppUser user;          // 关联的认证用户
}