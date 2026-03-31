package com.example.aiagent.entities;
import com.example.aiagent.Engine.NodeResult;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("ANSWER")
public class AnswerNode extends Node {

    private String responseKey;

    @Override
    public NodeResult execute(Object answer) {
        return NodeResult.done(responseKey);
    }
}