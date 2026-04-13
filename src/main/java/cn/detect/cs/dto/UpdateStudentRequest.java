package cn.detect.cs.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateStudentRequest {
    private String name;
    private Integer age;
    private String major;
    private String grade;
    private String email;
}