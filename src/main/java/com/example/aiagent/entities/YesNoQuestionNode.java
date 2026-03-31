package com.example.aiagent.entities;


import com.example.aiagent.Engine.NodeResult;
import com.example.aiagent.enums.ConditionType;
import com.example.aiagent.enums.QuestionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("YES_NO")
public class YesNoQuestionNode extends QuestionNode {

    public YesNoQuestionNode() {
        setQuestionType(QuestionType.YES_NO);
    }

    @Override
    public boolean validateAnswer(Object answer) {
        if (answer instanceof Boolean) return true;
        if (answer instanceof String s) {
            String l = s.toLowerCase();
            return l.equals("yes") || l.equals("no") || l.equals("oui") || l.equals("non");
        }
        return false;
    }

    @Override
    public NodeResult execute(Object answer) {
        if (answer == null)
            return NodeResult.waitingForInput(getQuestionText());
        if (!validateAnswer(answer))
            return NodeResult.waitingForInput("Réponse invalide. " + getQuestionText());
        ConditionType cond = isYes(answer) ? ConditionType.YES : ConditionType.NO;
        return NodeResult.done(cond, answer);
    }

    private boolean isYes(Object a) {
        if (a instanceof Boolean b) return b;
        String s = a.toString().toLowerCase();
        return s.equals("yes") || s.equals("oui");
    }
}
