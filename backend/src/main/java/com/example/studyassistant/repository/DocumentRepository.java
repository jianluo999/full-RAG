package com.example.studyassistant.repository;

import com.example.studyassistant.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    
    // 综合搜索：文件名或内容包含关键词
    // 注意：由于 content 是 CLOB/TEXT 类型，直接对其使用 LOWER() 函数可能不被所有数据库支持（包括H2）。
    // 因此，我们只对 file_name 使用LOWER()，并依赖数据库的默认排序规则进行内容搜索。
    // 对于更高级的全文搜索，应考虑使用 Elasticsearch 或其他专门的搜索引擎。
    @Query(value = "SELECT * FROM document WHERE content LIKE CONCAT('%', :keyword, '%') OR LOWER(file_name) LIKE LOWER(CONCAT('%', :keyword, '%'))",
           nativeQuery = true)
    List<Document> findByKeyword(@Param("keyword") String keyword);
}