package com.example.aiagent.services;

import com.example.aiagent.dao.EdgeRepository;
import com.example.aiagent.dao.GraphRepository;
import com.example.aiagent.dto.EdgeDto;
import com.example.aiagent.entities.Edge;
import com.example.aiagent.entities.Graph;
import com.example.aiagent.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EdgeService {
    @Autowired private EdgeRepository  edgeRepository;
    @Autowired private GraphRepository graphRepository;

    public EdgeDto create(Long graphId, EdgeDto dto) {
        Graph graph = graphRepository.findById(graphId)
                .orElseThrow(() -> new ResourceNotFoundException("Graph introuvable : " + graphId));
        Edge edge = toEntity(dto);
        edge.setGraph(graph);
        return toDto(edgeRepository.save(edge));
    }

    @Transactional(readOnly = true)
    public List<EdgeDto> getByGraph(Long graphId) {
        return edgeRepository.findByGraph_Id(graphId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public EdgeDto update(Long id, EdgeDto dto) {
        Edge edge = edgeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Edge introuvable : " + id));
        edge.setFromNodeId(dto.getFromNodeId()); edge.setToNodeId(dto.getToNodeId()); edge.setCondition(dto.getCondition());
        return toDto(edgeRepository.save(edge));
    }

    public void delete(Long id) {
        if (!edgeRepository.existsById(id)) throw new ResourceNotFoundException("Edge introuvable : " + id);
        edgeRepository.deleteById(id);
    }

    private Edge toEntity(EdgeDto d) {
        Edge e = new Edge(); e.setFromNodeId(d.getFromNodeId()); e.setToNodeId(d.getToNodeId()); e.setCondition(d.getCondition()); return e;
    }
    private EdgeDto toDto(Edge e) {
        EdgeDto d = new EdgeDto(); d.setId(e.getId()); d.setFromNodeId(e.getFromNodeId()); d.setToNodeId(e.getToNodeId()); d.setCondition(e.getCondition()); return d;
    }
}