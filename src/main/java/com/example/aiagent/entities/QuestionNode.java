package com.example.aiagent.entities;

import com.example.aiagent.enums.NodeType;
import com.example.aiagent.enums.QuestionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter

@Entity
@DiscriminatorValue("QUESTION")
public abstract class QuestionNode extends Node {

    private String questionText;

    @Enumerated(EnumType.STRING)
    private QuestionType questionType;

    public abstract boolean validateAnswer(Object answer);
}
