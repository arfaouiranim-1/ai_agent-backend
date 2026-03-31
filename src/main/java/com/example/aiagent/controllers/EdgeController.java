package com.example.aiagent.controllers;

import com.example.aiagent.dto.ApiResponse;
import com.example.aiagent.dto.EdgeDto;
import com.example.aiagent.services.EdgeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/graphs/{graphId}/edges")
public class EdgeController {

    @Autowired private EdgeService edgeService;

    @PostMapping
    public ResponseEntity<ApiResponse<EdgeDto>> create(
            @PathVariable Long graphId, @Valid @RequestBody EdgeDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Edge créé", edgeService.create(graphId, dto)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EdgeDto>>> getByGraph(@PathVariable Long graphId) {
        return ResponseEntity.ok(ApiResponse.ok(edgeService.getByGraph(graphId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EdgeDto>> update(
            @PathVariable Long graphId, @PathVariable Long id,
            @Valid @RequestBody EdgeDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Mis à jour", edgeService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long graphId, @PathVariable Long id) {
        edgeService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Supprimé", null));
    }
}