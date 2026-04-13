package cn.detect.cs.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCourseRequest {
    @NotBlank
    private String courseCode;
    @NotBlank
    private String name;
    @NotNull
    @Min(1)
    private Integer credit;
    private Long teacherId;          // 可选，不指定则无教师
    private String schedule;
    @NotNull
    @Min(1)
    private Integer capacity;
    private String description;
}