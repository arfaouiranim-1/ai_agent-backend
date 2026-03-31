package com.example.aiagent.dao;

import com.example.aiagent.entities.NotificationNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationNodeRepository extends JpaRepository<NotificationNode, Long> {
    List<NotificationNode> findByGraph_Id(Long graphId);
}