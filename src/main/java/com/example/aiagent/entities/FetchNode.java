package com.example.aiagent.entities;

import com.example.aiagent.Engine.NodeResult;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

@Getter
@Setter
@Entity
@DiscriminatorValue("FETCH")
public class FetchNode extends Node {

    private String url;
    private String method;
    private String headersJson;

    @Override
    public NodeResult execute(Object answer) {
        String resolvedUrl = url != null && answer != null
                ? url.replace("{input}", answer.toString()) : url;

        if (resolvedUrl == null) return NodeResult.done("URL non configurée");

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Parser les headers JSON si présents
            if (headersJson != null && !headersJson.isBlank()) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, String> headerMap = mapper.readValue(headersJson, Map.class);
                headerMap.forEach(headers::add);
            }

            HttpMethod httpMethod = HttpMethod.valueOf(
                    method != null ? method.toUpperCase() : "GET"
            );

            ResponseEntity<String> response = restTemplate.exchange(
                    resolvedUrl, httpMethod,
                    new HttpEntity<>(headers), String.class
            );

            return NodeResult.done(response.getBody());

        } catch (Exception e) {
            return NodeResult.done("[FetchNode Error: " + e.getMessage() + "]");
        }
    }
}