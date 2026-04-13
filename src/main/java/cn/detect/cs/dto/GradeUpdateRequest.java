package cn.detect.cs.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class GradeUpdateRequest {
    private BigDecimal regularGrade;
    private BigDecimal examGrade;
    private BigDecimal grade; // 可选，若传入则直接使用
}