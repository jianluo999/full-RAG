package com.example.studyassistant.dto;

import java.util.List;

public class QuizQuestion {
    private String question;
    private List<String> options; // For multiple choice questions
    private String answer;
    private String type; // e.g., "multiple-choice", "short-answer"

    // Getters and Setters
    public String getQuestion() {
        return question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }
    public List<String> getOptions() {
        return options;
    }
    public void setOptions(List<String> options) {
        this.options = options;
    }
    public String getAnswer() {
        return answer;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
} 