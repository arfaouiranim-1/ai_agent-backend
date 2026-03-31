package com.example.aiagent.dao;


import com.example.aiagent.entities.LLMNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LLMNodeRepository extends JpaRepository<LLMNode, Long> {

    @Query("SELECT n FROM LLMNode n WHERE n.graph.id = :graphId")
    List<LLMNode> findByGraphId(@Param("graphId") Long graphId);

    @Query("SELECT n FROM LLMNode n WHERE n.id = :id AND n.graph.id = :graphId")
    Optional<LLMNode> findByIdAndGraphId(@Param("id") Long id, @Param("graphId") Long graphId);

    boolean existsByNameAndGraphId(String name, Long graphId);
}