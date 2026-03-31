package com.example.aiagent.dto;

import com.example.aiagent.enums.QuestionType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionNodeDto extends BaseNodeDto {
    private String questionText;
    private QuestionType questionType;
    private List<String> options;
}