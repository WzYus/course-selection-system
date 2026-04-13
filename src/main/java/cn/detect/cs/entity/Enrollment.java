package cn.detect.cs.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollment")
@Data
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 关联学生（多对一）
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // 关联课程（多对一）
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // 选课时间，由数据库自动填充
    @CreationTimestamp
    @Column(name = "enrollment_time", nullable = false, updatable = false)
    private LocalDateTime enrollmentTime;

    // 状态：SELECTED（已选）、DROPPED（已退）
    @Column(nullable = false, length = 20)
    private String status = "SELECTED";

    // 最终成绩（可选）
    @Column(precision = 5, scale = 2)
    private BigDecimal grade;

    // 平时成绩（可选）
    @Column(name = "regular_grade", precision = 5, scale = 2)
    private BigDecimal regularGrade;

    // 期末成绩（可选）
    @Column(name = "exam_grade", precision = 5, scale = 2)
    private BigDecimal examGrade;

    @Column(nullable = false)
    private Integer year;
}