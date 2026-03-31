package com.example.aiagent.entities;

import com.example.aiagent.enums.ConditionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "edges")
public class Edge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fromNodeId;
    private String toNodeId;

    @Enumerated(EnumType.STRING)
    private ConditionType condition;

    @ManyToOne
    @JoinColumn(name = "graph_id")
    private Graph graph;
}