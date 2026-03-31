package com.example.aiagent.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationNodeDto extends BaseNodeDto {
    private String channel;
    private String template;
    private String recipient;
}