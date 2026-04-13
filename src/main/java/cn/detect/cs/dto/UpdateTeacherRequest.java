package cn.detect.cs.dto;

import lombok.Data;

@Data
public class UpdateTeacherRequest {
    private String name;
    private String title;
    private String department;
}