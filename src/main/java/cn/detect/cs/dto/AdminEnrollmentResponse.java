package cn.detect.cs.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AdminEnrollmentResponse {
    private Long enrollmentId;
    private String studentName;
    private String studentNumber;
    private String courseName;
    private String courseCode;
    private Integer year;
    private LocalDateTime enrollmentTime;
    private String status;
    private BigDecimal grade;
    private BigDecimal regularGrade;
    private BigDecimal examGrade;
}