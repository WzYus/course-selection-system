package cn.detect.cs.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import static cn.detect.cs.common.MessageConstants.STUDENT_AGE_INVALID;
import static cn.detect.cs.common.MessageConstants.STUDENT_NAME_REQUIRED;

// 请求 DTO：更新个人信息时允许修改的字段
@Data
public class StudentProfileUpdateRequest {
    @NotBlank(message = STUDENT_NAME_REQUIRED)
    private String name;

    @Min(value = 1, message = STUDENT_AGE_INVALID)
    private Integer age;

    private String major;   // 专业
    private String grade;   // 年级
    // 如果需要修改电话、邮箱等，可在此添加

    @Email(message = "邮箱格式不正确")
    private String email;
}
