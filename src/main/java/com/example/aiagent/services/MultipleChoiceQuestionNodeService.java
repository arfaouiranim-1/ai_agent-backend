package com.example.aiagent.services;

import com.example.aiagent.dao.GraphRepository;
import com.example.aiagent.dao.MultipleChoiceQuestionNodeRepository;
import com.example.aiagent.dto.QuestionNodeDto;
import com.example.aiagent.entities.Graph;
import com.example.aiagent.entities.MultipleChoiceQuestionNode;
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
public class MultipleChoiceQuestionNodeService {
    @Autowired private MultipleChoiceQuestionNodeRepository repo;
    @Autowired private GraphRepository graphRepository;

    public QuestionNodeDto create(Long graphId, QuestionNodeDto dto) {
        Graph g = graphRepository.findById(graphId).orElseThrow(() -> new ResourceNotFoundException("Graph introuvable : " + graphId));
        MultipleChoiceQuestionNode n = new MultipleChoiceQuestionNode();
        n.setName(dto.getName()); n.setType(NodeType.QUESTION); n.setQuestionText(dto.getQuestionText()); n.setOptions(dto.getOptions()); n.setGraph(g);
        return toDto(repo.save(n));
    }

    @Transactional(readOnly = true)
    public QuestionNodeDto getById(Long id) {
        return toDto(repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("MultipleChoiceNode introuvable : " + id)));
    }

    @Transactional(readOnly = true)
    public List<QuestionNodeDto> getByGraph(Long graphId) {
        return repo.findByGraph_Id(graphId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public QuestionNodeDto update(Long id, QuestionNodeDto dto) {
        MultipleChoiceQuestionNode n = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("MultipleChoiceNode introuvable : " + id));
        n.setName(dto.getName()); n.setQuestionText(dto.getQuestionText()); n.setOptions(dto.getOptions());
        return toDto(repo.save(n));
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("MultipleChoiceNode introuvable : " + id);
        repo.deleteById(id);
    }

    private QuestionNodeDto toDto(MultipleChoiceQuestionNode n) {
        QuestionNodeDto d = new QuestionNodeDto();
        d.setId(n.getId()); d.setName(n.getName()); d.setType(n.getType());
        d.setQuestionText(n.getQuestionText()); d.setQuestionType(QuestionType.MULTIPLE_CHOICE); d.setOptions(n.getOptions()); return d;
    }
}