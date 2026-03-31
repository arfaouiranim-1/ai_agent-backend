package com.example.aiagent.entities;

import com.example.aiagent.Engine.NodeResult;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("START")
public class StartNode extends Node {

    @Override
    public NodeResult execute(Object answer) {
        return NodeResult.done("Graph démarré");
    }
}