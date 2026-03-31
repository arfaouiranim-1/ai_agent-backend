package com.example.aiagent.dao;

import com.example.aiagent.entities.Commit;
import com.example.aiagent.enums.CommitStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommitRepository extends JpaRepository<Commit, Long> {
    List<Commit> findByGraphId(String graphId);
    List<Commit> findByStatus(CommitStatus status);
}
