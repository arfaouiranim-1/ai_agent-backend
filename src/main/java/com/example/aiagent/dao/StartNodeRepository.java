package com.example.aiagent.dao;

import com.example.aiagent.entities.StartNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StartNodeRepository extends JpaRepository<StartNode, Long> {
    Optional<StartNode> findByGraph_Id(Long graphId);
}