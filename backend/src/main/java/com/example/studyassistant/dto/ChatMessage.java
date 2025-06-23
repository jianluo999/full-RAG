package com.example.studyassistant.dto;

public class ChatMessage {
    private String role; // "user" or "assistant"
    private String content;

    // Getters and Setters
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
} 