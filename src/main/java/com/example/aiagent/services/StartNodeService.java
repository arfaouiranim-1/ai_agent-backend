package com.example.aiagent.services;

import com.example.aiagent.dao.GraphRepository;
import com.example.aiagent.dao.StartNodeRepository;
import com.example.aiagent.dto.StartNodeDto;
import com.example.aiagent.entities.Graph;
import com.example.aiagent.entities.StartNode;
import com.example.aiagent.enums.NodeType;
import com.example.aiagent.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class StartNodeService {

    @Autowired private StartNodeRepository repo;
    @Autowired private GraphRepository     graphRepository;

    public StartNodeDto create(Long graphId, StartNodeDto dto) {
        Graph g = graphRepository.findById(graphId)
                .orElseThrow(() -> new ResourceNotFoundException("Graph introuvable : " + graphId));

        // Un graph ne peut avoir qu'un seul StartNode
        if (repo.findByGraph_Id(graphId).isPresent())
            throw new IllegalStateException("Ce graph a déjà un StartNode.");

        StartNode n = new StartNode();
        n.setName(dto.getName() != null ? dto.getName() : "Start");
        n.setType(NodeType.START);
        n.setGraph(g);
        StartNode saved = repo.save(n);

        // Mettre à jour automatiquement le startNodeId du graph
        g.setStartNodeId(String.valueOf(saved.getId()));
        graphRepository.save(g);

        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public StartNodeDto getById(Long id) {
        return toDto(repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StartNode introuvable : " + id)));
    }

    @Transactional(readOnly = true)
    public StartNodeDto getByGraph(Long graphId) {
        return toDto(repo.findByGraph_Id(graphId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun StartNode pour le graph : " + graphId)));
    }

    public StartNodeDto update(Long id, StartNodeDto dto) {
        StartNode n = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StartNode introuvable : " + id));
        n.setName(dto.getName());
        return toDto(repo.save(n));
    }

    public void delete(Long id) {
        if (!repo.existsById(id))
            throw new ResourceNotFoundException("StartNode introuvable : " + id);
        repo.deleteById(id);
    }

    private StartNodeDto toDto(StartNode n) {
        StartNodeDto d = new StartNodeDto();
        d.setId(n.getId());
        d.setName(n.getName());
        d.setType(n.getType());
        return d;
    }
}