package com.example.aiagent.dto;

import com.example.aiagent.enums.CommitStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommitDto {
    private Long id;
    private String graphId;
    private String message;
    private CommitStatus status;
    private LocalDateTime createdAt;
}