package com.example.aiagent.services;

import com.example.aiagent.dto.LLMNodeDto;
import com.example.aiagent.entities.Graph;
import com.example.aiagent.entities.LLMNode;
import com.example.aiagent.enums.NodeType;
import com.example.aiagent.exception.ResourceNotFoundException;
import com.example.aiagent.dao.GraphRepository;
import com.example.aiagent.dao.LLMNodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LLMNodeService {

    private final LLMNodeRepository llmNodeRepository;
    private final GraphRepository   graphRepository;

    @Transactional
    public LLMNodeDto create(Long graphId, LLMNodeDto dto) {
        Graph graph = findGraphOrThrow(graphId);

        LLMNode node = new LLMNode();
        node.setGraph(graph);
        node.setName(dto.getName());
        node.setType(NodeType.LLM);
        node.setPromptTemplate(dto.getPromptTemplate());
        node.setModelName(resolveModel(dto.getModelName()));
        node.setSystemPrompt(dto.getSystemPrompt());
        node.setMaxTokens(resolveMaxTokens(dto.getMaxTokens()));

        return toDto(llmNodeRepository.save(node));
    }

    @Transactional(readOnly = true)
    public List<LLMNodeDto> getByGraph(Long graphId) {
        findGraphOrThrow(graphId);
        return llmNodeRepository.findByGraphId(graphId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LLMNodeDto getById(Long graphId, Long id) {
        LLMNode node = llmNodeRepository.findByIdAndGraphId(id, graphId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "LLMNode introuvable : id=" + id + " pour graphe=" + graphId));
        return toDto(node);
    }

    @Transactional
    public LLMNodeDto update(Long graphId, Long id, LLMNodeDto dto) {
        LLMNode node = llmNodeRepository.findByIdAndGraphId(id, graphId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "LLMNode introuvable : id=" + id + " pour graphe=" + graphId));

        if (dto.getName() != null)           node.setName(dto.getName());
        if (dto.getPromptTemplate() != null) node.setPromptTemplate(dto.getPromptTemplate());
        if (dto.getModelName() != null)      node.setModelName(resolveModel(dto.getModelName()));
        if (dto.getSystemPrompt() != null)   node.setSystemPrompt(dto.getSystemPrompt());
        if (dto.getMaxTokens() > 0)          node.setMaxTokens(resolveMaxTokens(dto.getMaxTokens()));

        return toDto(llmNodeRepository.save(node));
    }

    @Transactional
    public void delete(Long graphId, Long id) {
        LLMNode node = llmNodeRepository.findByIdAndGraphId(id, graphId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "LLMNode introuvable : id=" + id + " pour graphe=" + graphId));
        llmNodeRepository.delete(node);
    }

    private String resolveModel(String modelName) {
        return (modelName != null && !modelName.isBlank()) ? modelName : "claude-sonnet-4-6";
    }

    private int resolveMaxTokens(int maxTokens) {
        return (maxTokens > 0) ? maxTokens : 1024;
    }

    private Graph findGraphOrThrow(Long graphId) {
        return graphRepository.findById(graphId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Graphe introuvable : id=" + graphId));
    }

    private LLMNodeDto toDto(LLMNode node) {
        LLMNodeDto dto = new LLMNodeDto();
        dto.setId(node.getId());
        dto.setName(node.getName());
        dto.setType(node.getType());
        dto.setPromptTemplate(node.getPromptTemplate());
        dto.setModelName(node.getModelName());
        dto.setSystemPrompt(node.getSystemPrompt());
        dto.setMaxTokens(node.getMaxTokens());
        return dto;
    }
}