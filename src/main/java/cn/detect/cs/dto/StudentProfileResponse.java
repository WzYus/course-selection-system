package cn.detect.cs.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

// 响应 DTO：返回给前端的学生个人信息
@Data
public class StudentProfileResponse {
    private Long id;
    private String name;
    private Integer age;
    private String studentNumber;
    private String major;
    private String grade;
    private String username;          // 来自关联的 AppUser
    // 可根据需要添加电话、邮箱等字段
}

