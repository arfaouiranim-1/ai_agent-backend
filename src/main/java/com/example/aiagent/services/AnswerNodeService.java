package com.example.aiagent.services;

import com.example.aiagent.dao.AnswerNodeRepository;
import com.example.aiagent.dao.GraphRepository;
import com.example.aiagent.dto.AnswerNodeDto;
import com.example.aiagent.entities.AnswerNode;
import com.example.aiagent.entities.Graph;
import com.example.aiagent.enums.NodeType;
import com.example.aiagent.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AnswerNodeService {

    private final AnswerNodeRepository answerNodeRepository;
    private final GraphRepository      graphRepository;

    public AnswerNodeDto create(Long graphId, AnswerNodeDto dto) {
        Graph graph = graphRepository.findById(graphId)
                .orElseThrow(() -> new ResourceNotFoundException("Graph introuvable : " + graphId));
        AnswerNode node = new AnswerNode();
        node.setName(dto.getName());
        node.setType(NodeType.ANSWER);
        node.setResponseKey(dto.getResponseKey());
        node.setGraph(graph);
        return toDto(answerNodeRepository.save(node));
    }

    @Transactional(readOnly = true)
    public List<AnswerNodeDto> getByGraph(Long graphId) {
        return answerNodeRepository.findByGraph_Id(graphId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AnswerNodeDto getById(Long id) {
        return toDto(answerNodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AnswerNode introuvable : " + id)));
    }

    public AnswerNodeDto update(Long id, AnswerNodeDto dto) {
        AnswerNode node = answerNodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AnswerNode introuvable : " + id));
        node.setName(dto.getName());
        node.setResponseKey(dto.getResponseKey());
        return toDto(answerNodeRepository.save(node));
    }

    public void delete(Long id) {
        if (!answerNodeRepository.existsById(id))
            throw new ResourceNotFoundException("AnswerNode introuvable : " + id);
        answerNodeRepository.deleteById(id);
    }

    private AnswerNodeDto toDto(AnswerNode n) {
        AnswerNodeDto d = new AnswerNodeDto();
        d.setId(n.getId());
        d.setName(n.getName());
        d.setType(n.getType());
        d.setResponseKey(n.getResponseKey());
        return d;
    }
}