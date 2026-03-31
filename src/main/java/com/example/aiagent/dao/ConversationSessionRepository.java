package com.example.aiagent.dao;


import com.example.aiagent.entities.ConversationSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationSessionRepository
        extends JpaRepository<ConversationSession, Long> {

    Optional<ConversationSession> findByIdAndUserId(Long id, Long userId);

    List<ConversationSession> findByGraphIdOrderByCreatedAtDesc(Long graphId);
}