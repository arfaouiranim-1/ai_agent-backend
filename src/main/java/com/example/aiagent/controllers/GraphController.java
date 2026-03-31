package com.example.aiagent.controllers;

import com.example.aiagent.services.GraphExportService;
import com.example.aiagent.services.GraphService;
import com.example.aiagent.dto.ApiResponse;
import com.example.aiagent.dto.GraphDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/graphs")
@RequiredArgsConstructor
@Tag(name = "graph-controller", description = "CRUD + Export des graphes")
public class GraphController {

    private final GraphService       graphService;
    private final GraphExportService graphExportService;

    @PostMapping
    @Operation(summary = "Créer un graphe")
    public ResponseEntity<ApiResponse<GraphDto>> create(
            @Valid @RequestBody GraphDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Graph créé", graphService.create(dto)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un graphe par ID")
    public ResponseEntity<ApiResponse<GraphDto>> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(graphService.getById(id)));
    }

    @GetMapping
    @Operation(summary = "Lister tous les graphes")
    public ResponseEntity<ApiResponse<Page<GraphDto>>> getAll(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                search != null
                        ? graphService.search(search, pageable)
                        : graphService.getAll(pageable)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un graphe")
    public ResponseEntity<ApiResponse<GraphDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody GraphDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Mis à jour", graphService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un graphe")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id) {
        graphService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Supprimé", null));
    }

    @GetMapping("/export")
    @Operation(summary = "Export graph as DOT file")
    public ResponseEntity<byte[]> exportDot(
            @Parameter(required = true) @RequestParam Long id,
            @Parameter(required = true) @RequestParam Long commitId,
            @RequestParam(defaultValue = "false") boolean flatten) {
        byte[] bytes = graphExportService
                .exportToDot(id, commitId, flatten)
                .getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"graph-" + id + ".dot\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(bytes);
    }

    @GetMapping("/export-pdf")
    @Operation(summary = "Export graph as PDF file")
    public ResponseEntity<byte[]> exportPdf(
            @Parameter(required = true) @RequestParam Long id,
            @Parameter(required = true) @RequestParam Long commitId,
            @RequestParam(defaultValue = "false") boolean flatten) {
        byte[] pdf = graphExportService.exportToPdf(id, commitId, flatten);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"graph-" + id + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/export-json")
    @Operation(summary = "Export graph as JSON file")
    public ResponseEntity<byte[]> exportJson(
            @Parameter(required = true) @RequestParam Long id,
            @Parameter(required = true) @RequestParam Long commitId,
            @RequestParam(defaultValue = "false") boolean flatten) {
        byte[] bytes = graphExportService
                .exportToJson(id, commitId, flatten)
                .getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"graph-" + id + ".json\"")
                .contentType(MediaType.APPLICATION_JSON)
                .body(bytes);
    }
}