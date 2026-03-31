package com.example.aiagent.dao;


import com.example.aiagent.entities.ConversationMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationMessageRepository
        extends JpaRepository<ConversationMessage, Long> {

    List<ConversationMessage> findBySessionIdOrderByCreatedAtAsc(Long sessionId);
}