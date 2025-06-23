package com.example.studyassistant.service;

import com.example.studyassistant.dto.ChatMessage;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SummaryService {

    private final ChatClient chatClient;
    private final String summaryPromptTemplate = """
            Your task is to create a concise summary of the following conversation.
            Focus on the key topics and the user's main intent.
            The summary will be used as context for a follow-up conversation.
            Respond ONLY with the summary, without any extra commentary.
            Conversation to summarize:
            ---
            %s
            ---
            """;

    public SummaryService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String summarize(List<ChatMessage> history) {
        if (history == null || history.isEmpty()) {
            return "";
        }

        String conversationText = history.stream()
                .map(msg -> msg.getRole() + ": " + msg.getContent())
                .collect(Collectors.joining("\n"));

        String promptString = String.format(summaryPromptTemplate, conversationText);

        Prompt prompt = new Prompt(new UserMessage(promptString));

        return chatClient.call(prompt).getResult().getOutput().getContent();
    }
} 