package cn.detect.cs.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTeacherRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String name;
    @NotBlank
    private String teacherNumber;
    private String title;
    private String department;
}