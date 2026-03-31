package com.example.aiagent.dto;

import com.example.aiagent.entities.ExecutionSession;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ExecutionSessionDto {
    private Long id;
    private String graphId;
    private String currentNodeId;
    private String lastOutput;
    private String pendingMessage;
    private ExecutionSession.Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}