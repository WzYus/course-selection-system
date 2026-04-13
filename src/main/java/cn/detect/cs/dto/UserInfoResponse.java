package cn.detect.cs.dto;

import lombok.Data;

@Data
public class UserInfoResponse {
    private Long id;
    private String username;
    private String role;
    private Boolean enabled;
    // 通用业务字段
    private String name;
    private String studentNumber;    // 学生专用
    private String teacherNumber;    // 教师专用
    private String phone;            // 管理员专用
}