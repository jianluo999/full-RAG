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
    @Query(value = "SELECT * FROM document WHERE LOWER(content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(file_name) LIKE LOWER(CONCAT('%', :keyword, '%'))",
           nativeQuery = true)
    List<Document> findByKeyword(@Param("keyword") String keyword);
}