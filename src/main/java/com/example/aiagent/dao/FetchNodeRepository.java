package com.example.aiagent.dao;

import com.example.aiagent.entities.FetchNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FetchNodeRepository extends JpaRepository<FetchNode, Long> {
    List<FetchNode> findByGraph_Id(Long graphId);
}