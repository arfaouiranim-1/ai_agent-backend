package com.example.aiagent.dao;


import com.example.aiagent.entities.Node;
import com.example.aiagent.enums.NodeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NodeRepository extends JpaRepository<Node, Long> {
    List<Node> findByGraph_Id(Long graphId);
    List<Node> findByType(NodeType type);
    @Query("SELECT n FROM Node n WHERE n.graph.id = :graphId AND n.type = :type")
    List<Node> findByGraphIdAndType(@Param("graphId") Long graphId,
                                    @Param("type") NodeType type);
}