package com.example.aiagent.services;

import com.example.aiagent.dao.FetchNodeRepository;
import com.example.aiagent.dao.GraphRepository;
import com.example.aiagent.dto.FetchNodeDto;
import com.example.aiagent.entities.FetchNode;
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
public class FetchNodeService {

    private final FetchNodeRepository fetchNodeRepository;
    private final GraphRepository     graphRepository;

    public FetchNodeDto create(Long graphId, FetchNodeDto dto) {
        Graph graph = graphRepository.findById(graphId)
                .orElseThrow(() -> new ResourceNotFoundException("Graph introuvable : " + graphId));
        FetchNode node = new FetchNode();
        node.setName(dto.getName());
        node.setType(NodeType.FETCH);
        node.setUrl(dto.getUrl());
        node.setMethod(dto.getMethod() != null ? dto.getMethod() : "GET");
        node.setHeadersJson(dto.getHeadersJson());
        node.setGraph(graph);
        return toDto(fetchNodeRepository.save(node));
    }

    @Transactional(readOnly = true)
    public List<FetchNodeDto> getByGraph(Long graphId) {
        return fetchNodeRepository.findByGraph_Id(graphId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FetchNodeDto getById(Long id) {
        return toDto(fetchNodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FetchNode introuvable : " + id)));
    }

    public FetchNodeDto update(Long id, FetchNodeDto dto) {
        FetchNode node = fetchNodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FetchNode introuvable : " + id));
        node.setName(dto.getName());
        node.setUrl(dto.getUrl());
        node.setMethod(dto.getMethod());
        node.setHeadersJson(dto.getHeadersJson());
        return toDto(fetchNodeRepository.save(node));
    }

    public void delete(Long id) {
        if (!fetchNodeRepository.existsById(id))
            throw new ResourceNotFoundException("FetchNode introuvable : " + id);
        fetchNodeRepository.deleteById(id);
    }

    private FetchNodeDto toDto(FetchNode n) {
        FetchNodeDto d = new FetchNodeDto();
        d.setId(n.getId());
        d.setName(n.getName());
        d.setType(n.getType());
        d.setUrl(n.getUrl());
        d.setMethod(n.getMethod());
        d.setHeadersJson(n.getHeadersJson());
        return d;
    }
}