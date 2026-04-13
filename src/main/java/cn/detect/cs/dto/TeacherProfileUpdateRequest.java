package cn.detect.cs.dto;

import lombok.Data;

@Data
public class TeacherProfileUpdateRequest {
    private String name;
    private String title;
    private String department;
}