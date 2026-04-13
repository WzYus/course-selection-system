package cn.detect.cs.repository.elastic;

import cn.detect.cs.document.CourseDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseDocumentRepository extends ElasticsearchRepository<CourseDocument, Long> {
    // 可以自定义搜索方法，例如根据名称或教师模糊查询
    // 但我们将使用 QueryBuilder 实现更灵活的全文搜索
}