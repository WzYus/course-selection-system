package cn.detect.cs.dto;

import lombok.Data;

@Data
public class TimetableItem {
    private String day;       // "周一" 或 数字1-7
    private Integer startSection;
    private Integer endSection;
    private String courseName;
    private String teacher;
    private String location;
    private String courseCode;
    // 可选其他字段
}
