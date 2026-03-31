package com.example.aiagent.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "attachments")
public class Attachment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String fileType;
    private String filePath;
    private String nodeId;
    private LocalDateTime uploadedAt;

    @PrePersist
    public void onCreate() { uploadedAt = LocalDateTime.now(); }
}