package com.example.aiagent.dao;
import com.example.aiagent.entities.Edge;
import com.example.aiagent.enums.ConditionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EdgeRepository extends JpaRepository<Edge, Long> {
    List<Edge> findByFromNodeId(String fromNodeId);
    List<Edge> findByGraph_Id(Long graphId);
    List<Edge> findByFromNodeIdAndCondition(String fromNodeId, ConditionType condition);
    // Ajoutez dans EdgeRepository.java
    @Query("SELECT e FROM Edge e WHERE e.fromNodeId = :fromNodeId AND e.graph.id = :graphId")
    List<Edge> findByFromNodeIdAndGraphId(@Param("fromNodeId") String fromNodeId,
                                          @Param("graphId") Long graphId);
}
