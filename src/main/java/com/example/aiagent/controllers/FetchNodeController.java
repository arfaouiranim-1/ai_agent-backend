package com.example.aiagent.controllers;

import com.example.aiagent.dto.FetchNodeDto;
import com.example.aiagent.dto.ApiResponse;
import com.example.aiagent.services.FetchNodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/graphs/{graphId}/nodes/fetch")
@RequiredArgsConstructor
public class FetchNodeController {

    private final FetchNodeService fetchNodeService;

    @PostMapping
    public ResponseEntity<ApiResponse<FetchNodeDto>> create(
            @PathVariable Long graphId, @RequestBody FetchNodeDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("FetchNode créé", fetchNodeService.create(graphId, dto)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FetchNodeDto>>> getByGraph(@PathVariable Long graphId) {
        return ResponseEntity.ok(ApiResponse.ok(fetchNodeService.getByGraph(graphId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FetchNodeDto>> getById(
            @PathVariable Long graphId, @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(fetchNodeService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FetchNodeDto>> update(
            @PathVariable Long graphId, @PathVariable Long id,
            @RequestBody FetchNodeDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Mis à jour", fetchNodeService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long graphId, @PathVariable Long id) {
        fetchNodeService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Supprimé", null));
    }
}