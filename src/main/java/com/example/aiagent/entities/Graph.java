package com.example.aiagent.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


@Getter
@Setter
@Entity
@Table(name = "graphs")
public class Graph {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String startNodeId;

    @OneToMany(mappedBy = "graph", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Node> nodes = new ArrayList<>();

    @OneToMany(mappedBy = "graph", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Edge> edges = new LinkedHashSet<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }

    @PreUpdate
    public void onUpdate() { updatedAt = LocalDateTime.now(); }
}