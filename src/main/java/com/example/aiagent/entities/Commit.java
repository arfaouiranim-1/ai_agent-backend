package com.example.aiagent.entities;

import com.example.aiagent.enums.CommitStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter

@Entity
@Table(name = "commits")
public class Commit {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String graphId;
    private String message;

    @Column(columnDefinition = "TEXT")
    private String snapshot;

    @Enumerated(EnumType.STRING)
    private CommitStatus status;

    private LocalDateTime createdAt;

    @PrePersist public void onCreate() { createdAt = LocalDateTime.now(); status = CommitStatus.DRAFT; }
}

