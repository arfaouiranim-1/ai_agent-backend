package com.example.aiagent.Engine;

import com.example.aiagent.enums.ConditionType;

public class NodeResult {

    public enum Status { WAITING_FOR_INPUT, DONE, COMPLETED }

    private final Status        status;
    private final ConditionType condition;
    private final Object        output;
    private final String        message;

    private NodeResult(Status status, ConditionType condition, Object output, String message) {
        this.status    = status;
        this.condition = condition;
        this.output    = output;
        this.message   = message;
    }

    public static NodeResult waitingForInput(String question) {
        return new NodeResult(Status.WAITING_FOR_INPUT, null, null, question);
    }

    public static NodeResult done(Object output) {
        return new NodeResult(Status.DONE, ConditionType.DEFAULT, output, null);
    }

    public static NodeResult done(ConditionType condition, Object output) {
        return new NodeResult(Status.DONE, condition, output, null);
    }

    public static NodeResult completed(String message) {
        return new NodeResult(Status.COMPLETED, null, null, message);
    }

    public boolean isWaiting()   { return status == Status.WAITING_FOR_INPUT; }
    public boolean isCompleted() { return status == Status.COMPLETED; }

    public Status        getStatus()    { return status; }
    public ConditionType getCondition() { return condition; }
    public Object        getOutput()    { return output; }
    public String        getMessage()   { return message; }
}