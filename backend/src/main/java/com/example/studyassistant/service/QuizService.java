package com.example.studyassistant.service;

import com.example.studyassistant.dto.QuizQuestion;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class QuizService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;
    private final String quizPromptTemplate = """
            你是一个很棒的学习助手，擅长出题。你的所有输出，包括问题、选项和答案，都必须使用简体中文。
            请根据下面提供的上下文，生成 {numberOfQuestions} 个题目来检验用户的学习效果。
            请混合使用以下题型：选择题 (multiple-choice), 简答题 (short-answer), 和名词解释 (term-explanation)。

            你必须返回一个严格的、纯净的JSON数组格式，不要包含任何Markdown标记或多余的文字说明。
            数组中的每一个对象都应遵循以下结构:
            - "question": 问题文本。
            - "options": 针对于选择题的选项数组；对于其他题型，请使用空数组 []。
            - "answer": 正确答案。对于选择题，答案必须是选项之一。
            - "type": 题目类型，必须是 "multiple-choice", "short-answer", 或 "term-explanation" 中的一个。

            出题所依据的上下文:
            ---
            {context}
            ---
            """;

    public QuizService(ChatClient chatClient, ObjectMapper objectMapper) {
        this.chatClient = chatClient;
        this.objectMapper = objectMapper;
    }

    public List<QuizQuestion> generateQuiz(List<String> contexts, int numberOfQuestions) {
        String context = String.join("\n\n---\n\n", contexts);
        SystemPromptTemplate promptTemplate = new SystemPromptTemplate(quizPromptTemplate);
        Prompt prompt = promptTemplate.create(Map.of(
                "context", context,
                "numberOfQuestions", numberOfQuestions
        ));

        String rawResponse = chatClient.call(prompt).getResult().getOutput().getContent();

        try {
            // More robust JSON extraction to handle conversational parts from the AI
            int startIndex = rawResponse.indexOf('[');
            int endIndex = rawResponse.lastIndexOf(']');

            if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                String jsonResponse = rawResponse.substring(startIndex, endIndex + 1);
                return objectMapper.readValue(jsonResponse, new TypeReference<List<QuizQuestion>>() {});
            } else {
                 System.err.println("Could not find a valid JSON array in the AI response. Raw response: " + rawResponse);
                 return Collections.emptyList();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to parse JSON from AI response. Raw response: " + rawResponse);
            return Collections.emptyList();
        }
    }
} 