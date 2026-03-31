package com.example.aiagent.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class GraphDto {
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String name;

    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String description;

    private String startNodeId;
    private List<BaseNodeDto> nodes;
    private List<EdgeDto> edges;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}