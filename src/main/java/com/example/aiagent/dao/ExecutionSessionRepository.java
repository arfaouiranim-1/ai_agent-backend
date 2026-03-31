package com.example.aiagent.dao;

import com.example.aiagent.entities.ExecutionSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExecutionSessionRepository extends JpaRepository<ExecutionSession, Long> {
    List<ExecutionSession> findByGraphId(String graphId);
    List<ExecutionSession> findByStatus(ExecutionSession.Status status);
    List<ExecutionSession> findByStatusAndUpdatedAtBefore(
            ExecutionSession.Status status, LocalDateTime cutoff);
}