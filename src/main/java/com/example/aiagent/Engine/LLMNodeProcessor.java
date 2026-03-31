package com.example.aiagent.Engine;
import com.example.aiagent.entities.LLMNode;
import com.example.aiagent.entities.Node;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class LLMNodeProcessor implements NodeProcessorStrategy {

    @Value("${anthropic.api.key}")
    private String apiKey;

    @Value("${anthropic.api.url:https://api.anthropic.com/v1/messages}")
    private String apiUrl;

    @Value("${anthropic.api.version:2023-06-01}")
    private String apiVersion;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean supports(Node node) {
        return node instanceof LLMNode;
    }

    @Override
    public NodeResult process(Node node, Object input) {
        LLMNode llmNode = (LLMNode) node;

        // Remplace {{input}} dans le prompt par la valeur réelle
        String userPrompt = buildPrompt(llmNode.getPromptTemplate(), input);

        try {
            String response = callClaude(
                    userPrompt,
                    llmNode.getSystemPrompt(),
                    llmNode.getModelName(),
                    llmNode.getMaxTokens()
            );
            log.info("[LLMNode:{}] Claude response ok ({} chars)", llmNode.getId(), response.length());
            return NodeResult.done(response);

        } catch (Exception e) {
            log.error("[LLMNode:{}] Erreur appel Claude: {}", llmNode.getId(), e.getMessage());
            return NodeResult.done("Erreur Claude: " + e.getMessage());
        }
    }

    private String callClaude(String userPrompt, String systemPrompt,
                              String modelName, int maxTokens) throws Exception {

        // Corps de la requête
        Map<String, Object> body = new java.util.LinkedHashMap<>();
        body.put("model", modelName);
        body.put("max_tokens", maxTokens);

        // System prompt (optionnel)
        if (systemPrompt != null && !systemPrompt.isBlank()) {
            body.put("system", systemPrompt);
        }

        // Message utilisateur
        body.put("messages", List.of(
                Map.of("role", "user", "content", userPrompt)
        ));

        String jsonBody = objectMapper.writeValueAsString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type",    "application/json")
                .header("x-api-key",       apiKey)
                .header("anthropic-version", apiVersion)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Claude API error " + response.statusCode() + ": " + response.body());
        }

        // Extraire le texte de la réponse
        JsonNode root = objectMapper.readTree(response.body());
        return root.path("content").get(0).path("text").asText();
    }

    private String buildPrompt(String template, Object input) {
        if (template == null) return "";
        String value = (input != null) ? input.toString() : "";
        return template.replace("{{input}}", value);
    }
}