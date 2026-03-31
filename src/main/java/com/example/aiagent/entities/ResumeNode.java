package com.example.aiagent.entities;

import com.example.aiagent.Engine.NodeResult;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("RESUME")
public class ResumeNode extends Node {

    private String resumeTemplate; // ex: "Voici un résumé : {input}"

    @Override
    public NodeResult execute(Object answer) {
        String output = resumeTemplate != null && answer != null
                ? resumeTemplate.replace("{input}", answer.toString())
                : (answer != null ? answer.toString() : "");
        return NodeResult.done(output);
    }
}
