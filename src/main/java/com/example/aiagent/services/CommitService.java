package com.example.aiagent.services;

import com.example.aiagent.dao.CommitRepository;
import com.example.aiagent.dao.GraphRepository;
import com.example.aiagent.dto.CommitDto;
import com.example.aiagent.entities.Commit;
import com.example.aiagent.entities.Graph;
import com.example.aiagent.enums.CommitStatus;
import com.example.aiagent.exception.ResourceNotFoundException;
import com.example.aiagent.mapper.CommitMapper;
import com.example.aiagent.mapper.GraphMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class CommitService {

    @Autowired private CommitRepository commitRepository;
    @Autowired private GraphRepository  graphRepository;
    @Autowired private CommitMapper     commitMapper;
    @Autowired private GraphMapper      graphMapper;
    @Autowired private ObjectMapper     objectMapper;

    public CommitDto commit(Long graphId, String message) {
        Graph withNodes = graphRepository.findByIdWithNodes(graphId)
                .orElseThrow(() -> new ResourceNotFoundException("Graph introuvable : " + graphId));
        Graph withEdges = graphRepository.findByIdWithEdges(graphId)
                .orElseThrow(() -> new ResourceNotFoundException("Graph introuvable : " + graphId));
        withNodes.setEdges(withEdges.getEdges());

        try {
            Commit commit = new Commit();
            commit.setGraphId(String.valueOf(graphId));
            commit.setMessage(message);
            commit.setSnapshot(objectMapper.writeValueAsString(graphMapper.toDto(withNodes)));
            return commitMapper.toDto(commitRepository.save(commit));
        } catch (Exception e) {
            throw new RuntimeException("Erreur commit : " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<CommitDto> getByGraph(Long graphId) {
        return commitMapper.toDtoList(commitRepository.findByGraphId(String.valueOf(graphId)));
    }

    public CommitDto publish(Long commitId) {
        Commit c = commitRepository.findById(commitId)
                .orElseThrow(() -> new ResourceNotFoundException("Commit introuvable : " + commitId));
        c.setStatus(CommitStatus.PUBLISHED);
        return commitMapper.toDto(commitRepository.save(c));
    }
}