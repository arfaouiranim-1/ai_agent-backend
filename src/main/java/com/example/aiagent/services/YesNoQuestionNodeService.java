package com.example.aiagent.services;

import com.example.aiagent.dao.GraphRepository;
import com.example.aiagent.dao.YesNoQuestionNodeRepository;
import com.example.aiagent.dto.QuestionNodeDto;
import com.example.aiagent.entities.Graph;
import com.example.aiagent.entities.YesNoQuestionNode;
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
public class YesNoQuestionNodeService {
    @Autowired private YesNoQuestionNodeRepository repo;
    @Autowired private GraphRepository graphRepository;

    public QuestionNodeDto create(Long graphId, QuestionNodeDto dto) {
        Graph g = graphRepository.findById(graphId).orElseThrow(() -> new ResourceNotFoundException("Graph introuvable : " + graphId));
        YesNoQuestionNode n = new YesNoQuestionNode();
        n.setName(dto.getName()); n.setType(NodeType.QUESTION); n.setQuestionText(dto.getQuestionText()); n.setGraph(g);
        return toDto(repo.save(n));
    }

    @Transactional(readOnly = true)
    public QuestionNodeDto getById(Long id) {
        return toDto(repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("YesNoNode introuvable : " + id)));
    }

    @Transactional(readOnly = true)
    public List<QuestionNodeDto> getByGraph(Long graphId) {
        return repo.findByGraph_Id(graphId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public QuestionNodeDto update(Long id, QuestionNodeDto dto) {
        YesNoQuestionNode n = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("YesNoNode introuvable : " + id));
        n.setName(dto.getName()); n.setQuestionText(dto.getQuestionText());
        return toDto(repo.save(n));
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("YesNoNode introuvable : " + id);
        repo.deleteById(id);
    }

    private QuestionNodeDto toDto(YesNoQuestionNode n) {
        QuestionNodeDto d = new QuestionNodeDto();
        d.setId(n.getId()); d.setName(n.getName()); d.setType(n.getType());
        d.setQuestionText(n.getQuestionText()); d.setQuestionType(QuestionType.YES_NO); return d;
    }
}