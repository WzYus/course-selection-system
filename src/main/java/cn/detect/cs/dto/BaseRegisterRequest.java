package cn.detect.cs.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BaseRegisterRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "角色不能为空")
    private String role;   // 取值为 "ROLE_STUDENT" 或 "ROLE_TEACHER"
}