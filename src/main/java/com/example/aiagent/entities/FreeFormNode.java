package com.example.aiagent.entities;

import com.example.aiagent.Engine.NodeResult;
import com.example.aiagent.enums.QuestionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("FREE_FORM")
public class FreeFormNode extends QuestionNode {

    public FreeFormNode() {
        setQuestionType(QuestionType.FREE_FORM);
    }

    @Override
    public boolean validateAnswer(Object answer) {
        return answer != null && !answer.toString().trim().isEmpty();
    }

    @Override
    public NodeResult execute(Object answer) {
        if (answer == null || answer.toString().trim().isEmpty())
            return NodeResult.waitingForInput(getQuestionText());
        return NodeResult.done(answer);
    }
}