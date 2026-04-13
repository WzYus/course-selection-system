package cn.detect.cs.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StudentProfileRequest {
    @NotBlank(message = "姓名不能为空")
    private String name;

    @Min(value = 1, message = "年龄必须大于0")
    private Integer age;

    @NotBlank(message = "学号不能为空")
    private String studentNumber;

    private String major;   // 专业（可选）
    private String grade;   // 年级（可选）

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
}