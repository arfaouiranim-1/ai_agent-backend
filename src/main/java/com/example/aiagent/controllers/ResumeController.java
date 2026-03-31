package com.example.aiagent.controllers;

import com.example.aiagent.dto.ApiResponse;
import com.example.aiagent.dto.ResumeNodeDto;
import com.example.aiagent.services.ResumeNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/graphs/{graphId}/nodes/resume")
public class ResumeController {

    @Autowired private ResumeNodeService service;

    @PostMapping
    public ResponseEntity<ApiResponse<ResumeNodeDto>> create(
            @PathVariable Long graphId,
            @RequestBody ResumeNodeDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("ResumeNode créé", service.create(graphId, dto)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ResumeNodeDto>>> getByGraph(
            @PathVariable Long graphId) {
        return ResponseEntity.ok(ApiResponse.ok(service.getByGraph(graphId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ResumeNodeDto>> getById(
            @PathVariable Long graphId,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ResumeNodeDto>> update(
            @PathVariable Long graphId,
            @PathVariable Long id,
            @RequestBody ResumeNodeDto dto) {
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