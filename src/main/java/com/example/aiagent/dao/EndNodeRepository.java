package com.example.aiagent.dao;


import com.example.aiagent.entities.EndNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EndNodeRepository extends JpaRepository<EndNode, Long> {
    List<EndNode> findByGraph_Id(Long graphId);
}
