package com.example.studyassistant.controller;

import com.example.studyassistant.dto.RagRequest;
import com.example.studyassistant.service.RAGService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/rag")
public class RAGController {

    private final RAGService ragService;

    public RAGController(RAGService ragService) {
        this.ragService = ragService;
    }

    @PostMapping("/query")
    public ResponseEntity<String> ragQuery(@RequestBody RagRequest request) {
        String query = request.getQuery();
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Query cannot be empty.");
        }
        try {
            String answer = ragService.getAnswer(request);
            return ResponseEntity.ok(answer);
        } catch (Exception e) {
            // Log the exception in a real application
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error processing RAG query: " + e.getMessage());
        }
    }
} 