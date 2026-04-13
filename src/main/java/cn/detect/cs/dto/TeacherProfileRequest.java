package cn.detect.cs.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TeacherProfileRequest {
    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotBlank(message = "工号不能为空")
    private String teacherNumber;

    private String title;       // 职称（可选）
    private String department;  // 院系（可选）
}