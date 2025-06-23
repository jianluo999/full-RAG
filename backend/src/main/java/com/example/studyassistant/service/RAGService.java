package com.example.studyassistant.service;

import com.example.studyassistant.dto.ChatMessage;
import com.example.studyassistant.dto.RagRequest;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RAGService {

    private final ChatClient chatClient;

    @Value("${system.prompt.template}")
    private String systemPromptTemplate;

    public RAGService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String getAnswer(RagRequest request) {
        String query = request.getQuery();
        List<ChatMessage> history = request.getHistory();
        List<String> contexts = request.getContexts();

        if (contexts == null || contexts.isEmpty()) {
            return "I can't answer without any context. Please search for some documents above first.";
        }

        String context = String.join("\n\n---\n\n", contexts);

        SystemPromptTemplate systemPromptTpl = new SystemPromptTemplate(systemPromptTemplate);
        String renderedSystemText = systemPromptTpl.render(Map.of("context", context, "query", query));
        SystemMessage systemMessage = new SystemMessage(renderedSystemText);

        List<Message> conversation = new ArrayList<>();
        conversation.add(systemMessage);

        if (history != null) {
            history.forEach(msg -> {
                if ("user".equalsIgnoreCase(msg.getRole())) {
                    conversation.add(new UserMessage(msg.getContent()));
                } else if ("assistant".equalsIgnoreCase(msg.getRole())) {
                    conversation.add(new AssistantMessage(msg.getContent()));
                }
            });
        }

        conversation.add(new UserMessage(query));

        Prompt prompt = new Prompt(conversation);

        return chatClient.call(prompt).getResult().getOutput().getContent();
    }
} 