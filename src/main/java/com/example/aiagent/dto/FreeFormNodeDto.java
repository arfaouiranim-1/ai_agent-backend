package com.example.aiagent.dto;

import com.example.aiagent.enums.QuestionType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FreeFormNodeDto extends BaseNodeDto {
    private String questionText;
    private QuestionType questionType = QuestionType.FREE_FORM;
}