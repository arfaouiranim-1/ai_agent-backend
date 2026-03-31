package com.example.aiagent.controllers;


import com.example.aiagent.dto.ApiResponse;
import com.example.aiagent.dto.LLMNodeDto;
import com.example.aiagent.services.LLMNodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/graphs/{graphId}/nodes/llm")
@RequiredArgsConstructor
public class LLMController {

    private final LLMNodeService service;

    @PostMapping
    public ResponseEntity<ApiResponse<LLMNodeDto>> create(
            @PathVariable Long graphId,
            @Valid @RequestBody LLMNodeDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("LLMNode créé", service.create(graphId, dto)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LLMNodeDto>>> getByGraph(
            @PathVariable Long graphId) {
        return ResponseEntity.ok(ApiResponse.ok(service.getByGraph(graphId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LLMNodeDto>> getById(
            @PathVariable Long graphId,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getById(graphId, id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LLMNodeDto>> update(
            @PathVariable Long graphId,
            @PathVariable Long id,
            @Valid @RequestBody LLMNodeDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Mis à jour", service.update(graphId, id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long graphId,
            @PathVariable Long id) {
        service.delete(graphId, id);
        return ResponseEntity.ok(ApiResponse.ok("Supprimé", null));
    }
}