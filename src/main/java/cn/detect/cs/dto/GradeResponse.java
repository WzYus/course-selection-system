package cn.detect.cs.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class GradeResponse {
    private Long enrollmentId;      // 选课记录ID（可选）
    private String courseName;
    private String courseCode;
    private Integer credit;
    private BigDecimal grade;        // 最终成绩
    private BigDecimal regularGrade; // 平时成绩
    private BigDecimal examGrade;    // 期末成绩
    //private String semester;         // 学期（可选）
}