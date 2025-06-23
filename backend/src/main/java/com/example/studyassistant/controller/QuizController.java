package com.example.studyassistant.controller;

import com.example.studyassistant.dto.QuizQuestion;
import com.example.studyassistant.dto.QuizRequest;
import com.example.studyassistant.service.QuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping("/generate")
    public ResponseEntity<List<QuizQuestion>> generateQuiz(@RequestBody QuizRequest request) {
        if (request.getContexts() == null || request.getContexts().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        int numberOfQuestions = request.getNumberOfQuestions() > 0 ? request.getNumberOfQuestions() : 3; // Default to 3 questions

        try {
            List<QuizQuestion> questions = quizService.generateQuiz(request.getContexts(), numberOfQuestions);
            if (questions.isEmpty()) {
                return ResponseEntity.internalServerError().build();
            }
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
} 