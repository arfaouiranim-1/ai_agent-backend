package com.example.aiagent.Engine;

import com.example.aiagent.entities.Edge;
import com.example.aiagent.enums.ConditionType;
import org.springframework.stereotype.Component;

import java.util.List;

import java.util.Optional;



@Component
public class ConditionResolver {

    public String resolve(List<Edge> edges, ConditionType condition) {
        ConditionType cond = condition != null ? condition : ConditionType.DEFAULT;

        Optional<Edge> match = edges.stream()
                .filter(e -> e.getCondition() == cond)
                .findFirst();

        if (match.isPresent()) return match.get().getToNodeId();

        return edges.stream()
                .filter(e -> e.getCondition() == ConditionType.DEFAULT)
                .findFirst()
                .map(Edge::getToNodeId)
                .orElse(null);
    }

    public ConditionType fromAnswer(Object answer) {
        if (answer instanceof Boolean b) return b ? ConditionType.YES : ConditionType.NO;
        if (answer instanceof String s) return switch (s.toLowerCase()) {
            case "yes", "oui" -> ConditionType.YES;
            case "no",  "non" -> ConditionType.NO;
            default            -> ConditionType.OPTION;
        };
        return ConditionType.DEFAULT;
    }
}
