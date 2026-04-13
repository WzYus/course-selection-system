package cn.detect.cs.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateAdminRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String name;
    private String phone;
}