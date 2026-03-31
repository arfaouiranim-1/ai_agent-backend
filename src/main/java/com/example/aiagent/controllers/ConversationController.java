package com.example.aiagent.controllers;

import com.example.aiagent.dto.ApiResponse;
import com.example.aiagent.dto.ConversationAnswerDto;
import com.example.aiagent.dto.ConversationSessionDto;
import com.example.aiagent.services.ConversationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
@Tag(name = "conversation-controller",
        description = "Exécution interactive des agents — questions/réponses")
public class ConversationController {

    private final ConversationService conversationService;

    // ─────────────────────────────────────────────────────────
    // POST /api/conversations/start/{graphId}
    // Démarrer une session pour un utilisateur
    // ─────────────────────────────────────────────────────────
    @PostMapping("/start/{graphId}")
    @Operation(summary = "Démarrer une session de conversation")
    public ResponseEntity<ApiResponse<ConversationSessionDto>> start(
            @PathVariable Long graphId,
            @RequestParam(defaultValue = "1") Long userId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Session démarrée",
                        conversationService.start(graphId, userId)));
    }

    // ─────────────────────────────────────────────────────────
    // POST /api/conversations/{sessionId}/answer
    // Envoyer une réponse → avancer au nœud suivant
    // ─────────────────────────────────────────────────────────
    @PostMapping("/{sessionId}/answer")
    @Operation(summary = "Répondre à la question courante")
    public ResponseEntity<ApiResponse<ConversationSessionDto>> answer(
            @PathVariable Long sessionId,
            @RequestParam(defaultValue = "1") Long userId,
            @RequestBody ConversationAnswerDto answerDto) {
        return ResponseEntity.ok(ApiResponse.ok("Réponse enregistrée",
                conversationService.answer(sessionId, userId, answerDto)));
    }

    // ─────────────────────────────────────────────────────────
    // GET /api/conversations/{sessionId}
    // Récupérer l'état courant d'une session
    // ─────────────────────────────────────────────────────────
    @GetMapping("/{sessionId}")
    @Operation(summary = "Récupérer l'état d'une session")
    public ResponseEntity<ApiResponse<ConversationSessionDto>> getSession(
            @PathVariable Long sessionId) {
        return ResponseEntity.ok(
                ApiResponse.ok(conversationService.getSession(sessionId)));
    }

    // ─────────────────────────────────────────────────────────
    // GET /api/conversations/graph/{graphId}
    // Lister toutes les sessions d'un graphe
    // ─────────────────────────────────────────────────────────
    @GetMapping("/graph/{graphId}")
    @Operation(summary = "Lister les sessions d'un graphe")
    public ResponseEntity<ApiResponse<List<ConversationSessionDto>>> getByGraph(
            @PathVariable Long graphId) {
        return ResponseEntity.ok(
                ApiResponse.ok(conversationService.getByGraph(graphId)));
    }
}