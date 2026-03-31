package com.example.aiagent.controllers;

import com.example.aiagent.dto.ApiResponse;
import com.example.aiagent.dto.QuestionNodeDto;
import com.example.aiagent.services.YesNoQuestionNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/graphs/{graphId}/nodes/yesno")
public class YesNoQuestionNodeController {

    @Autowired private YesNoQuestionNodeService service;

    @PostMapping
    public ResponseEntity<ApiResponse<QuestionNodeDto>> create(@PathVariable Long graphId, @RequestBody QuestionNodeDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Créé", service.create(graphId, dto)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<QuestionNodeDto>>> getByGraph(@PathVariable Long graphId) {
        return ResponseEntity.ok(ApiResponse.ok(service.getByGraph(graphId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestionNodeDto>> getById(@PathVariable Long graphId, @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestionNodeDto>> update(@PathVariable Long graphId, @PathVariable Long id, @RequestBody QuestionNodeDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Mis à jour", service.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long graphId, @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Supprimé", null));
    }
}