package com.example.aiagent.entities;


import com.example.aiagent.Engine.NodeResult;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("NOTIFICATION")
public class NotificationNode extends Node {

    private String channel;
    private String template;
    private String recipient;

    @Override
    public NodeResult execute(Object answer) {
        String message = template != null && answer != null
                ? template.replace("{input}", answer.toString()) : template;
        return NodeResult.done(message);
    }
}