package com.example.aiagent.services;

import com.example.aiagent.dao.GraphRepository;
import com.example.aiagent.dao.ResumeNodeRepository;
import com.example.aiagent.dto.ResumeNodeDto;
import com.example.aiagent.entities.Graph;
import com.example.aiagent.entities.ResumeNode;
import com.example.aiagent.enums.NodeType;
import com.example.aiagent.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ResumeNodeService {

    @Autowired private ResumeNodeRepository repo;
    @Autowired private GraphRepository      graphRepository;

    public ResumeNodeDto create(Long graphId, ResumeNodeDto dto) {
        Graph g = graphRepository.findById(graphId)
                .orElseThrow(() -> new ResourceNotFoundException("Graph introuvable : " + graphId));
        ResumeNode n = new ResumeNode();
        n.setName(dto.getName());
        n.setType(NodeType.RESUME);
        n.setResumeTemplate(dto.getResumeTemplate());
        n.setGraph(g);
        return toDto(repo.save(n));
    }

    @Transactional(readOnly = true)
    public ResumeNodeDto getById(Long id) {
        return toDto(repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ResumeNode introuvable : " + id)));
    }

    @Transactional(readOnly = true)
    public List<ResumeNodeDto> getByGraph(Long graphId) {
        return repo.findByGraph_Id(graphId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public ResumeNodeDto update(Long id, ResumeNodeDto dto) {
        ResumeNode n = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ResumeNode introuvable : " + id));
        n.setName(dto.getName());
        n.setResumeTemplate(dto.getResumeTemplate());
        return toDto(repo.save(n));
    }

    public void delete(Long id) {
        if (!repo.existsById(id))
            throw new ResourceNotFoundException("ResumeNode introuvable : " + id);
        repo.deleteById(id);
    }

    private ResumeNodeDto toDto(ResumeNode n) {
        ResumeNodeDto d = new ResumeNodeDto();
        d.setId(n.getId());
        d.setName(n.getName());
        d.setType(n.getType());
        d.setResumeTemplate(n.getResumeTemplate());
        return d;
    }
}