package com.example.aiagent.services;

import com.example.aiagent.Engine.GraphExecutionEngine;
import com.example.aiagent.dao.ExecutionSessionRepository;
import com.example.aiagent.dto.ExecutionSessionDto;
import com.example.aiagent.entities.ExecutionSession;
import com.example.aiagent.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExecutionEngineService {

    @Autowired private GraphExecutionEngine       engine;
    @Autowired private ExecutionSessionRepository sessionRepository;

    public ExecutionSessionDto start(String graphId) {
        return toDto(engine.start(graphId));
    }

    public ExecutionSessionDto continueExecution(String sessionId, Object answer) {
        return toDto(engine.continueExecution(sessionId, answer));
    }

    @Transactional(readOnly = true)
    public ExecutionSessionDto getSession(String sessionId) {
        Long id = Long.parseLong(sessionId);
        return sessionRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Session introuvable : " + sessionId));
    }

    @Transactional(readOnly = true)
    public List<ExecutionSessionDto> getByGraph(String graphId) {
        return sessionRepository.findByGraphId(graphId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private ExecutionSessionDto toDto(ExecutionSession s) {
        ExecutionSessionDto d = new ExecutionSessionDto();
        d.setId(s.getId());
        d.setGraphId(s.getGraphId());
        d.setCurrentNodeId(s.getCurrentNodeId());
        d.setLastOutput(s.getLastOutput());
        d.setPendingMessage(s.getPendingMessage());
        d.setStatus(s.getStatus());
        d.setCreatedAt(s.getCreatedAt());
        d.setUpdatedAt(s.getUpdatedAt());
        return d;
    }
}