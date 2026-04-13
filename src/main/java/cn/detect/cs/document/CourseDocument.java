package cn.detect.cs.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "courses")
public class CourseDocument {
    @Id
    private Long id;

    @Field(type = FieldType.Keyword)
    private String courseCode;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String name;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String teacher;

    private Integer credit;
    private String schedule;
    private Integer capacity;
    private Integer selectedCount;
    private String description;
}