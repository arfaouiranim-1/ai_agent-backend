package com.example.aiagent.controllers;

import com.example.aiagent.dto.ApiResponse;
import com.example.aiagent.services.NotificationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationNodeController {

    private final NotificationService notificationService;

    // ── Envoyer un SMS ─────────────────────────────────────
    @PostMapping("/sms")
    public ResponseEntity<ApiResponse<Void>> sendSms(
            @Valid @RequestBody SmsRequest request) {
        notificationService.sendSms(request.getTo(), request.getMessage());
        return ResponseEntity.ok(ApiResponse.ok("SMS envoyé à " + request.getTo(), null));
    }

    // ── Envoyer un Email ───────────────────────────────────
    @PostMapping("/email")
    public ResponseEntity<ApiResponse<Void>> sendEmail(
            @Valid @RequestBody EmailRequest request) {
        notificationService.sendEmail(
                request.getTo(),
                request.getSubject() != null ? request.getSubject() : "Notification",
                request.getMessage()
        );
        return ResponseEntity.ok(ApiResponse.ok("Email envoyé à " + request.getTo(), null));
    }

    // ── DTOs internes ──────────────────────────────────────

    @Data
    public static class SmsRequest {
        @NotBlank(message = "Le numéro destinataire est obligatoire")
        private String to;

        @NotBlank(message = "Le message est obligatoire")
        private String message;
    }

    @Data
    public static class EmailRequest {
        @NotBlank(message = "L'email destinataire est obligatoire")
        @Email(message = "Email invalide")
        private String to;

        private String subject;

        @NotBlank(message = "Le message est obligatoire")
        private String message;
    }
}