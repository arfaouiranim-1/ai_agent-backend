package com.example.aiagent.services;

import com.example.aiagent.dao.FreeFormNodeRepository;
import com.example.aiagent.dao.GraphRepository;
import com.example.aiagent.dto.FreeFormNodeDto;
import com.example.aiagent.entities.FreeFormNode;
import com.example.aiagent.entities.Graph;
import com.example.aiagent.enums.NodeType;
import com.example.aiagent.enums.QuestionType;
import com.example.aiagent.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FreeFormNodeService {

    @Autowired private FreeFormNodeRepository repo;
    @Autowired private GraphRepository        graphRepository;

    public FreeFormNodeDto create(Long graphId, FreeFormNodeDto dto) {
        Graph g = graphRepository.findById(graphId)
                .orElseThrow(() -> new ResourceNotFoundException("Graph introuvable : " + graphId));
        FreeFormNode n = new FreeFormNode();
        n.setName(dto.getName());
        n.setType(NodeType.QUESTION);
        n.setQuestionText(dto.getQuestionText());
        n.setQuestionType(QuestionType.FREE_FORM);
        n.setGraph(g);
        return toDto(repo.save(n));
    }

    @Transactional(readOnly = true)
    public FreeFormNodeDto getById(Long id) {
        return toDto(repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FreeFormNode introuvable : " + id)));
    }

    @Transactional(readOnly = true)
    public List<FreeFormNodeDto> getByGraph(Long graphId) {
        return repo.findByGraph_Id(graphId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public FreeFormNodeDto update(Long id, FreeFormNodeDto dto) {
        FreeFormNode n = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FreeFormNode introuvable : " + id));
        n.setName(dto.getName());
        n.setQuestionText(dto.getQuestionText());
        return toDto(repo.save(n));
    }

    public void delete(Long id) {
        if (!repo.existsById(id))
            throw new ResourceNotFoundException("FreeFormNode introuvable : " + id);
        repo.deleteById(id);
    }

    private FreeFormNodeDto toDto(FreeFormNode n) {
        FreeFormNodeDto d = new FreeFormNodeDto();
        d.setId(n.getId());
        d.setName(n.getName());
        d.setType(n.getType());
        d.setQuestionText(n.getQuestionText());
        d.setQuestionType(QuestionType.FREE_FORM);
        return d;
    }
}