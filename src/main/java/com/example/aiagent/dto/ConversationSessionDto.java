package com.example.aiagent.dto;

import com.example.aiagent.enums.SessionStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConversationSessionDto {

    private Long                id;
    private Long                graphId;
    private String              graphName;
    private Long                currentNodeId;
    private String              currentNodeType;   // ← alimenté dans toDto()
    private SessionStatus       status;
    private String              finalResult;
    private LocalDateTime       createdAt;
    private CurrentQuestionDto  currentQuestion;
    private List<MessageDto>    messages;
    private Map<String, Object> collectedData;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CurrentQuestionDto {
        private Long         nodeId;
        private String       questionText;
        private String       questionType;
        private List<String> options;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class MessageDto {
        private Long         nodeId;
        private String       role;
        private String       content;
        private List<String> options;
        private String       createdAt;
    }
}