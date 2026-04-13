package cn.detect.cs.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateStudentRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String name;
    @Min(1)
    private Integer age;
    @NotBlank
    private String studentNumber;
    private String major;
    private String grade;
}