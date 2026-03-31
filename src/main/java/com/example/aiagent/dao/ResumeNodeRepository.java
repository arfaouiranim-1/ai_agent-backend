package com.example.aiagent.dao;

import com.example.aiagent.entities.ResumeNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResumeNodeRepository extends JpaRepository<ResumeNode, Long> {
    List<ResumeNode> findByGraph_Id(Long graphId);
}