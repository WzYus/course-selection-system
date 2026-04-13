package cn.detect.cs.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class StudentGradeDTO {
    private Long enrollmentId;
    private Long studentId;
    private String studentName;
    private String studentNumber;
    private BigDecimal regularGrade;
    private BigDecimal examGrade;
    private BigDecimal grade;
    private Integer year;
}