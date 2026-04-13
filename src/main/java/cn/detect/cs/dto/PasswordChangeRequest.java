package cn.detect.cs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import static cn.detect.cs.common.MessageConstants.*;

// 修改密码请求 DTO
@Data
public class PasswordChangeRequest {
    @NotBlank(message = OLD_PASSWORD_NOT_BLANK)
    private String oldPassword;

    @NotBlank(message = NEW_PASSWORD_NOT_BLANK)
    @Size(min = 6, message = NEW_PASSWORD_SIZE)
    private String newPassword;
}
