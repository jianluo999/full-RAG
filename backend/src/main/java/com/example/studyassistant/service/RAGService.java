package com.example.studyassistant.service;

import com.example.studyassistant.repository.DocumentRepository;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RAGService {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private DocumentRepository documentRepository;

    private final String systemPromptTemplate = """
            You are a helpful study assistant. Your task is to answer questions based on the provided context.
            The context contains text from documents uploaded by the user.
            Base your answer only on the provided context and do not use any other information.
            If the context does not contain enough information, say that you cannot answer the question based on the provided documents.

            Context:
            ---
            {context}
            ---
            """;

    public String getAnswer(String query) {
        // 1. Retrieval
        List<com.example.studyassistant.entity.Document> relevantDocs = documentRepository.findByKeyword(query);

        if (relevantDocs.isEmpty()) {
            return "I'm sorry, I couldn't find any relevant information in the uploaded documents to answer your question.";
        }
        
        // 2. Augmentation
        String context = relevantDocs.stream()
                .map(doc -> "Document: " + doc.getFileName() + "\nContent:\n" + doc.getContent())
                .collect(Collectors.joining("\n\n---\n\n"));

        // 3. Generation
      
    SystemPromptTemplate systemPromptTpl = new SystemPromptTemplate(systemPromptTemplate);
    String renderedSystemText = systemPromptTpl.render(Map.of("context", context));
    SystemMessage systemMessage = new SystemMessage(renderedSystemText);
        
        UserMessage userMessage = new UserMessage("Based on the context, please answer the following question: " + query);

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        
        return chatClient.call(prompt).getResult().getOutput().getContent();
    }
} 