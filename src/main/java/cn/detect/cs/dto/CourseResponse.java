package cn.detect.cs.dto;

import lombok.Data;

@Data
public class CourseResponse {
    private Long id;
    private String courseCode;
    private String name;
    private Integer credit;
    private String teacher;
    private String schedule;
    private Integer capacity;
    private Integer selectedCount;  // 已选人数
    private String description;
}