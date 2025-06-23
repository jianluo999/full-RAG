package com.example.studyassistant.dto;
import java.util.List;

public class RagRequest {
    private String query;
    private List<ChatMessage> history;
    private List<String> contexts;

    // Getters and Setters
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    public List<ChatMessage> getHistory() { return history; }
    public void setHistory(List<ChatMessage> history) { this.history = history; }
    public List<String> getContexts() { return contexts; }
    public void setContexts(List<String> contexts) { this.contexts = contexts; }
} 