package com.example.aiagent.dao;
import com.example.aiagent.entities.YesNoQuestionNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface YesNoQuestionNodeRepository extends JpaRepository<YesNoQuestionNode, Long> {
    List<YesNoQuestionNode> findByGraph_Id(Long graphId);
}