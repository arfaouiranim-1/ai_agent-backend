package com.example.aiagent.Engine;


import com.example.aiagent.entities.Node;

public interface NodeProcessorStrategy {
    boolean supports(Node node);
    NodeResult process(Node node, Object input);
}