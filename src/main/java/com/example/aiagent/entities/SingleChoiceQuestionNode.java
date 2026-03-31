package com.example.aiagent.entities;

import com.example.aiagent.Engine.NodeResult;
import com.example.aiagent.enums.ConditionType;
import com.example.aiagent.enums.QuestionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Entity
@DiscriminatorValue("SINGLE_CHOICE")
public class SingleChoiceQuestionNode extends QuestionNode {

    @ElementCollection
    @CollectionTable(name = "single_choice_options", joinColumns = @JoinColumn(name = "node_id"))
    @Column(name = "option_value")
    private List<String> options;

    public SingleChoiceQuestionNode() {
        setQuestionType(QuestionType.SINGLE_CHOICE);
    }

    @Override
    public boolean validateAnswer(Object answer) {
        return answer instanceof String s && options != null && options.contains(s);
    }

    @Override
    public NodeResult execute(Object answer) {
        if (answer == null)
            return NodeResult.waitingForInput(getQuestionText());
        if (!validateAnswer(answer))
            return NodeResult.waitingForInput("Option invalide. " + getQuestionText());
        return NodeResult.done(ConditionType.OPTION, answer);
    }
}