package cn.detect.cs.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EnrolledCourseResponse {
    private Long enrollmentId;
    private Long courseId;
    private String courseName;
    private String courseCode;
    private Integer credit;
    private String teacher;
    private String schedule;
    private LocalDateTime enrollmentTime;
    private String status;
}