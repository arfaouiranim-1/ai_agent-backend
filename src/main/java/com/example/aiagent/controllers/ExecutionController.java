package com.example.aiagent.controllers;

import com.example.aiagent.dto.ApiResponse;
import com.example.aiagent.dto.ExecutionSessionDto;
import com.example.aiagent.services.ExecutionEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/execution")
public class ExecutionController {

    @Autowired private ExecutionEngineService executionEngineService;

    @PostMapping("/start/{graphId}")
    public ResponseEntity<ApiResponse<ExecutionSessionDto>> start(
            @PathVariable String graphId) {
        return ResponseEntity.ok(ApiResponse.ok("Démarré", executionEngineService.start(graphId)));
    }

    @PostMapping("/continue/{sessionId}")
    public ResponseEntity<ApiResponse<ExecutionSessionDto>> continueExec(
            @PathVariable String sessionId,
            @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(ApiResponse.ok("Continué",
                executionEngineService.continueExecution(sessionId, body.get("answer"))));
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<ApiResponse<ExecutionSessionDto>> getSession(
            @PathVariable String sessionId) {
        return ResponseEntity.ok(ApiResponse.ok(executionEngineService.getSession(sessionId)));
    }

    @GetMapping("/graph/{graphId}")
    public ResponseEntity<ApiResponse<List<ExecutionSessionDto>>> getByGraph(
            @PathVariable String graphId) {
        return ResponseEntity.ok(ApiResponse.ok(executionEngineService.getByGraph(graphId)));
    }
}