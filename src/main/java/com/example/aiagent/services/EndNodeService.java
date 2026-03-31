package com.example.aiagent.services;

import com.example.aiagent.dao.EndNodeRepository;
import com.example.aiagent.dao.GraphRepository;
import com.example.aiagent.dto.EndNodeDto;
import com.example.aiagent.entities.EndNode;
import com.example.aiagent.entities.Graph;
import com.example.aiagent.enums.NodeType;
import com.example.aiagent.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EndNodeService {

    @Autowired private EndNodeRepository repo;
    @Autowired private GraphRepository   graphRepository;

    public EndNodeDto create(Long graphId, EndNodeDto dto) {
        Graph g = graphRepository.findById(graphId)
                .orElseThrow(() -> new ResourceNotFoundException("Graph introuvable : " + graphId));

        EndNode n = new EndNode();
        n.setName(dto.getName() != null ? dto.getName() : "End");
        n.setType(NodeType.END);
        n.setResultMessage(dto.getResultMessage());
        n.setGraph(g);
        return toDto(repo.save(n));
    }

    @Transactional(readOnly = true)
    public EndNodeDto getById(Long id) {
        return toDto(repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EndNode introuvable : " + id)));
    }

    @Transactional(readOnly = true)
    public List<EndNodeDto> getByGraph(Long graphId) {
        return repo.findByGraph_Id(graphId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public EndNodeDto update(Long id, EndNodeDto dto) {
        EndNode n = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EndNode introuvable : " + id));
        n.setName(dto.getName());
        n.setResultMessage(dto.getResultMessage());
        return toDto(repo.save(n));
    }

    public void delete(Long id) {
        if (!repo.existsById(id))
            throw new ResourceNotFoundException("EndNode introuvable : " + id);
        repo.deleteById(id);
    }

    private EndNodeDto toDto(EndNode n) {
        EndNodeDto d = new EndNodeDto();
        d.setId(n.getId());
        d.setName(n.getName());
        d.setType(n.getType());
        d.setResultMessage(n.getResultMessage());
        return d;
    }
}