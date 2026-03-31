package com.example.aiagent.dao;

import com.example.aiagent.entities.SingleChoiceQuestionNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SingleChoiceQuestionNodeRepository extends JpaRepository<SingleChoiceQuestionNode, Long> {
    List<SingleChoiceQuestionNode> findByGraph_Id(Long graphId);
}