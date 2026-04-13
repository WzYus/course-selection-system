package cn.detect.cs.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DropNotificationMessage implements Serializable {
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private Long courseId;
    private String courseName;
}