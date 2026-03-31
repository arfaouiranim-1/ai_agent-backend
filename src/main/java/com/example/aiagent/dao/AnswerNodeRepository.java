package com.example.aiagent.dao;


import com.example.aiagent.entities.AnswerNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnswerNodeRepository extends JpaRepository<AnswerNode, Long> {
    List<AnswerNode> findByGraph_Id(Long graphId);
}