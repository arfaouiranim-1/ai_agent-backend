package com.example.aiagent.Engine;

import com.example.aiagent.entities.*;
import com.example.aiagent.enums.NodeType;
import org.springframework.stereotype.Component;

@Component
public class NodeFactory {

    public Node create(NodeType type) {
        return switch (type) {
            case START        -> new StartNode();
            case END          -> new EndNode();
            case LLM          -> new LLMNode();
            case FETCH        -> new FetchNode();
            case ANSWER       -> new AnswerNode();
            case NOTIFICATION -> new NotificationNode();
            case RESUME       -> new ResumeNode();
            case QUESTION     -> new YesNoQuestionNode();
        };
    }
}