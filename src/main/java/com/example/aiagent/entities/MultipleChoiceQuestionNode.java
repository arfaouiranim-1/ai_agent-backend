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
@DiscriminatorValue("MULTIPLE_CHOICE")
public class MultipleChoiceQuestionNode extends QuestionNode {

    @ElementCollection
    @CollectionTable(name = "multiple_choice_options", joinColumns = @JoinColumn(name = "node_id"))
    @Column(name = "option_value")
    private List<String> options;

    public MultipleChoiceQuestionNode() {
        setQuestionType(QuestionType.MULTIPLE_CHOICE);
    }

    @Override
    public boolean validateAnswer(Object answer) {
        return answer instanceof List<?> sel && options != null && options.containsAll((List<?>) sel);
    }

    @Override
    public NodeResult execute(Object answer) {
        if (answer == null)
            return NodeResult.waitingForInput(getQuestionText());
        if (!validateAnswer(answer))
            return NodeResult.waitingForInput("Sélection invalide. " + getQuestionText());
        return NodeResult.done(ConditionType.OPTION, answer);
    }
}
