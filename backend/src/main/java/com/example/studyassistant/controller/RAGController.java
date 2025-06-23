package com.example.studyassistant.controller;

import com.example.studyassistant.service.RAGService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/rag")
public class RAGController {

    @Autowired
    private RAGService ragService;

    @PostMapping("/query")
    public ResponseEntity<String> ragQuery(@RequestBody Map<String, String> payload) {
        String query = payload.get("query");
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Query cannot be empty.");
        }
        try {
            String answer = ragService.getAnswer(query);
            return ResponseEntity.ok(answer);
        } catch (Exception e) {
            // Log the exception in a real application
            e.printStackTrace();
            return ResponseEntity.status(500).body("An error occurred while processing your request.");
        }
    }
} 