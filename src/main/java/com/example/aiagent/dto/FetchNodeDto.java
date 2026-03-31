package com.example.aiagent.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FetchNodeDto extends BaseNodeDto {
    private String url;
    private String method;
    private String headersJson;
}