package com.example.aiagent.dto;


import com.example.aiagent.enums.NodeType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor

public class LLMNodeDto extends BaseNodeDto {

    @NotBlank(message = "promptTemplate est obligatoire")
    private String promptTemplate;

    private String modelName = "claude-sonnet-4-6";


    private String systemPrompt;

    @Min(value = 1, message = "maxTokens doit être >= 1")
    private int maxTokens = 1024;

    public LLMNodeDto(Long id, String name, NodeType type,
                      String promptTemplate, String modelName,
                      String systemPrompt, int maxTokens) {
        super();
        setType(type);
        setName(name);

        this.promptTemplate = promptTemplate;
        this.modelName      = modelName;
        this.systemPrompt   = systemPrompt;
        this.maxTokens      = maxTokens;
    }
}