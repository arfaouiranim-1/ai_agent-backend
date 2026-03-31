package com.example.aiagent.dao;

import com.example.aiagent.entities.FreeFormNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FreeFormNodeRepository extends JpaRepository<FreeFormNode, Long> {
    List<FreeFormNode> findByGraph_Id(Long graphId);
}