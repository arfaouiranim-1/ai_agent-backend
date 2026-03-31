package com.example.aiagent.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "execution_sessions")
public class ExecutionSession {

    public enum Status { RUNNING, WAITING, COMPLETED, ERROR }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String graphId;
    private String currentNodeId;

    @Column(length = 4096)
    private String lastOutput;

    @Column(length = 2048)
    private String pendingMessage;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
        status = Status.RUNNING;
    }

    @PreUpdate
    public void onUpdate() { updatedAt = LocalDateTime.now(); }
}
