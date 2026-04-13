package cn.detect.cs.dto;

import lombok.Data;

@Data
public class TeacherProfileResponse {
    private Long id;
    private String teacherNumber;
    private String name;
    private String title;
    private String department;
    private String username;
}