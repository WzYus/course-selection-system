package cn.detect.cs.dto;

import lombok.Data;

@Data
public class TeacherEnrollRequest {
    private String studentNumber;   // 学号（而非学生ID）
    private String courseCode;      // 课程编号
}