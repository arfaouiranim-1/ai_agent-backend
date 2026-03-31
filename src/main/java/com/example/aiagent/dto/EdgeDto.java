package com.example.aiagent.dto;

import com.example.aiagent.enums.ConditionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EdgeDto {
    private Long id;

    @NotBlank(message = "fromNodeId est obligatoire")
    private String fromNodeId;

    @NotBlank(message = "toNodeId est obligatoire")
    private String toNodeId;

    @NotNull(message = "La condition est obligatoire")
    private ConditionType condition;
}