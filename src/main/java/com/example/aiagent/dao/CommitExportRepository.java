package com.example.aiagent.dao;


import com.example.aiagent.entities.Commit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommitExportRepository extends JpaRepository<Commit, Long> {

    // Trouve un commit par id et graphId
    Optional<Commit> findByIdAndGraphId(Long id, String graphId);

    // Liste tous les commits d'un graphe
    List<Commit> findByGraphIdOrderByCreatedAtDesc(String graphId);
}