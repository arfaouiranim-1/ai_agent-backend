package com.example.aiagent.controllers;

import com.example.aiagent.dto.AnswerNodeDto;
import com.example.aiagent.dto.ApiResponse;
import com.example.aiagent.services.AnswerNodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/graphs/{graphId}/nodes/answer")
@RequiredArgsConstructor
public class AnswerNodeController {

    private final AnswerNodeService answerNodeService;

    @PostMapping
    public ResponseEntity<ApiResponse<AnswerNodeDto>> create(
            @PathVariable Long graphId, @RequestBody AnswerNodeDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("AnswerNode créé", answerNodeService.create(graphId, dto)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AnswerNodeDto>>> getByGraph(@PathVariable Long graphId) {
        return ResponseEntity.ok(ApiResponse.ok(answerNodeService.getByGraph(graphId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AnswerNodeDto>> getById(
            @PathVariable Long graphId, @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(answerNodeService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AnswerNodeDto>> update(
            @PathVariable Long graphId, @PathVariable Long id,
            @RequestBody AnswerNodeDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Mis à jour", answerNodeService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long graphId, @PathVariable Long id) {
        answerNodeService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Supprimé", null));
    }
}