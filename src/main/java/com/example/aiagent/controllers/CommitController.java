package com.example.aiagent.controllers;

import com.example.aiagent.dto.ApiResponse;
import com.example.aiagent.dto.CommitDto;
import com.example.aiagent.services.CommitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/graphs/{graphId}/commits")
public class CommitController {
    @Autowired private CommitService commitService;

    @PostMapping
    public ResponseEntity<ApiResponse<CommitDto>> commit(@PathVariable Long graphId, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(ApiResponse.ok("Commit créé", commitService.commit(graphId, body.get("message"))));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CommitDto>>> getByGraph(@PathVariable Long graphId) {
        return ResponseEntity.ok(ApiResponse.ok(commitService.getByGraph(graphId)));
    }

    @PatchMapping("/{commitId}/publish")
    public ResponseEntity<ApiResponse<CommitDto>> publish(@PathVariable Long graphId, @PathVariable Long commitId) {
        return ResponseEntity.ok(ApiResponse.ok("Publié", commitService.publish(commitId)));
    }
}