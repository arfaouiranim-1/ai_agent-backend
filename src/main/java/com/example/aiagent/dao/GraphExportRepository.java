package com.example.aiagent.dao;


import com.example.aiagent.entities.Graph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GraphExportRepository extends JpaRepository<Graph, Long> {

    @Query("SELECT g FROM Graph g " +
            "LEFT JOIN FETCH g.nodes " +
            "LEFT JOIN FETCH g.edges " +
            "WHERE g.id = :id")
    Optional<Graph> findByIdWithNodesAndEdges(@Param("id") Long id);
}