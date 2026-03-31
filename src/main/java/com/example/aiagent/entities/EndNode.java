package com.example.aiagent.entities;


import com.example.aiagent.Engine.NodeResult;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("END")
public class EndNode extends Node {

    private String resultMessage;

    @Override
    public NodeResult execute(Object answer) {
        return NodeResult.completed(resultMessage != null ? resultMessage : "Exécution terminée.");
    }
}