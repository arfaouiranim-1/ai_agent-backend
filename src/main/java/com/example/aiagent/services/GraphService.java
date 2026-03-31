package com.example.aiagent.services;

import com.example.aiagent.dao.GraphRepository;
import com.example.aiagent.dto.GraphDto;
import com.example.aiagent.entities.Graph;
import com.example.aiagent.exception.ResourceNotFoundException;
import com.example.aiagent.mapper.GraphMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GraphService {

    @Autowired private GraphRepository graphRepository;
    @Autowired private GraphMapper     graphMapper;

    public GraphDto create(GraphDto dto) {
        return graphMapper.toDto(graphRepository.save(graphMapper.toEntity(dto)));
    }

    @Transactional(readOnly = true)
    public GraphDto getById(Long id) {
        Graph withNodes = graphRepository.findByIdWithNodes(id)
                .orElseThrow(() -> new ResourceNotFoundException("Graph introuvable : " + id));
        Graph withEdges = graphRepository.findByIdWithEdges(id)
                .orElseThrow(() -> new ResourceNotFoundException("Graph introuvable : " + id));
        withNodes.setEdges(withEdges.getEdges());
        return graphMapper.toDto(withNodes);
    }

    // ── Pagination ─────────────────────────────────────────
    @Transactional(readOnly = true)
    public Page<GraphDto> getAll(Pageable pageable) {
        return graphRepository.findAll(pageable).map(graphMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<GraphDto> search(String keyword, Pageable pageable) {
        return graphRepository.findByNameContainingIgnoreCase(keyword, pageable)
                .map(graphMapper::toDto);
    }

    public GraphDto update(Long id, GraphDto dto) {
        Graph g = graphRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Graph introuvable : " + id));
        g.setName(dto.getName());
        g.setDescription(dto.getDescription());
        g.setStartNodeId(dto.getStartNodeId());
        return graphMapper.toDto(graphRepository.save(g));
    }

    public void delete(Long id) {
        if (!graphRepository.existsById(id))
            throw new ResourceNotFoundException("Graph introuvable : " + id);
        graphRepository.deleteById(id);
    }
}