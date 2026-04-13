package cn.detect.cs.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollNotificationMessage implements Serializable {
    private Long studentId;
    private String studentName;
    private String studentEmail;   // 模拟邮箱
    private Long courseId;
    private String courseName;
    private String message;         // 自定义内容
}