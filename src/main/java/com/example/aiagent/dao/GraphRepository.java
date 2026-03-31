package com.example.aiagent.dao;

import com.example.aiagent.entities.Graph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface GraphRepository extends JpaRepository<Graph, Long> {

    Optional<Graph> findByName(String name);

    Page<Graph> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    @Query("SELECT DISTINCT g FROM Graph g LEFT JOIN FETCH g.nodes WHERE g.id = :id")
    Optional<Graph> findByIdWithNodes(@Param("id") Long id);

    @Query("SELECT DISTINCT g FROM Graph g LEFT JOIN FETCH g.edges WHERE g.id = :id")
    Optional<Graph> findByIdWithEdges(@Param("id") Long id);
}