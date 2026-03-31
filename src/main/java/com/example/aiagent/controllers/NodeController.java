package com.example.aiagent.controllers;

import com.example.aiagent.dto.ApiResponse;
import com.example.aiagent.dto.BaseNodeDto;
import com.example.aiagent.services.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/graphs/{graphId}/nodes")
public class NodeController {

    @Autowired private NodeService nodeService;

    @PostMapping
    public ResponseEntity<ApiResponse<BaseNodeDto>> create(
            @PathVariable Long graphId, @RequestBody BaseNodeDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Node créé", nodeService.create(graphId, dto)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BaseNodeDto>>> getByGraph(
            @PathVariable Long graphId) {
        return ResponseEntity.ok(ApiResponse.ok(nodeService.getByGraph(graphId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BaseNodeDto>> getById(
            @PathVariable Long graphId, @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(nodeService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BaseNodeDto>> update(
            @PathVariable Long graphId, @PathVariable Long id,
            @RequestBody BaseNodeDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Mis à jour", nodeService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long graphId, @PathVariable Long id) {
        nodeService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Supprimé", null));
    }
}