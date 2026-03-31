package com.example.aiagent.dao;

import com.example.aiagent.entities.MultipleChoiceQuestionNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MultipleChoiceQuestionNodeRepository extends JpaRepository<MultipleChoiceQuestionNode, Long> {
    List<MultipleChoiceQuestionNode> findByGraph_Id(Long graphId);
}
