package com.example.aiagent.controllers;

import com.example.aiagent.dto.ApiResponse;
import com.example.aiagent.entities.Attachment;
import com.example.aiagent.services.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/nodes/{nodeId}/attachments")
public class AttachmentController {
    @Autowired private AttachmentService attachmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<Attachment>> upload(@PathVariable String nodeId, @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(ApiResponse.ok("Uploadé", attachmentService.upload(nodeId, file)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Attachment>>> getByNode(@PathVariable String nodeId) {
        return ResponseEntity.ok(ApiResponse.ok(attachmentService.getByNode(nodeId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String nodeId, @PathVariable Long id) {
        attachmentService.delete(id); return ResponseEntity.ok(ApiResponse.ok("Supprimé", null));
    }
}