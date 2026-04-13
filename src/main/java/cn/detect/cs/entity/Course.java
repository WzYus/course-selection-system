package cn.detect.cs.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "course")
@Data
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_code", unique = true, nullable = false)
    private String courseCode;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer credit;

    private String teacher;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacherEntity;  // 新添加的关联字段

    private String schedule;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "selected_count")
    private Integer selectedCount = 0;

    @Lob
    private String description;
}