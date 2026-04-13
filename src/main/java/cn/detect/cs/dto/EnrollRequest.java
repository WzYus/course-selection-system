package cn.detect.cs.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnrollRequest {
    @NotNull(message = "课程ID不能为空")
    private Long courseId;
}