package com.example.aiagent.mapper;

import com.example.aiagent.dto.EdgeDto;
import com.example.aiagent.dto.GraphDto;
import com.example.aiagent.entities.Edge;
import com.example.aiagent.entities.Graph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GraphMapper {

    @Autowired private NodeMapper nodeMapper;

    public GraphDto toDto(Graph g) {
        GraphDto d = new GraphDto();
        d.setId(g.getId());
        d.setName(g.getName());
        d.setDescription(g.getDescription());
        d.setStartNodeId(g.getStartNodeId());
        d.setCreatedAt(g.getCreatedAt());
        d.setUpdatedAt(g.getUpdatedAt());
        if (g.getNodes() != null)
            d.setNodes(nodeMapper.toDtoList(g.getNodes()));
        if (g.getEdges() != null)
            d.setEdges(g.getEdges().stream().map(this::edgeToDto).collect(Collectors.toList()));
        return d;
    }

    public Graph toEntity(GraphDto d) {
        Graph g = new Graph();
        g.setName(d.getName());
        g.setDescription(d.getDescription());
        g.setStartNodeId(d.getStartNodeId());
        return g;
    }

    private EdgeDto edgeToDto(Edge e) {
        EdgeDto d = new EdgeDto();
        d.setId(e.getId());
        d.setFromNodeId(e.getFromNodeId());
        d.setToNodeId(e.getToNodeId());
        d.setCondition(e.getCondition());
        return d;
    }

    public List<GraphDto> toDtoList(List<Graph> list) {
        return list == null ? List.of() : list.stream().map(this::toDto).collect(Collectors.toList());
    }
}