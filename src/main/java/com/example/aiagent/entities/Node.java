package com.example.aiagent.entities;

import com.example.aiagent.Engine.NodeResult;
import com.example.aiagent.enums.NodeType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "nodes")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
public abstract class Node {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private NodeType type;

    @ManyToOne
    @JoinColumn(name = "graph_id")
    private Graph graph;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "node_id")
    private List<Edge> nextEdges;

    public abstract NodeResult execute(Object answer);
}
