package com.example.aiagent.dto;

import com.example.aiagent.enums.NodeType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseNodeDto {
    private Long id;
    private String name;
    private NodeType type;
}