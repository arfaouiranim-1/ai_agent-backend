package com.example.aiagent.controllers;

import com.example.aiagent.dto.ApiResponse;
import com.example.aiagent.dto.StartNodeDto;
import com.example.aiagent.services.StartNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/graphs/{graphId}/nodes/start")
public class StartNodeController {

    @Autowired private StartNodeService service;

    @PostMapping
    public ResponseEntity<ApiResponse<StartNodeDto>> create(
            @PathVariable Long graphId,
            @RequestBody StartNodeDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("StartNode créé", service.create(graphId, dto)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<StartNodeDto>> getByGraph(
            @PathVariable Long graphId) {
        return ResponseEntity.ok(ApiResponse.ok(service.getByGraph(graphId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StartNodeDto>> getById(
            @PathVariable Long graphId,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StartNodeDto>> update(
            @PathVariable Long graphId,
            @PathVariable Long id,
            @RequestBody StartNodeDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Mis à jour", service.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long graphId,
            @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Supprimé", null));
    }
}