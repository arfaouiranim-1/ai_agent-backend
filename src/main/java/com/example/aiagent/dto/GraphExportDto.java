package com.example.aiagent.dto;

import com.example.aiagent.enums.CommitStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GraphExportDto {

    private Long   id;
    private String name;
    private String description;
    private String startNodeId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    private CommitInfoDto       commit;
    private List<NodeExportDto> nodes;
    private List<EdgeExportDto> edges;
    private String              exportedAt;
    @Builder.Default
    private String version = "1.0";
    private boolean             flattened;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CommitInfoDto {
        private Long         id;
        private String       message;
        private CommitStatus status;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime createdAt;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class NodeExportDto {
        private Long   id;
        private String name;
        private String type;
        private String color;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class EdgeExportDto {
        private Long   id;
        private String fromNodeId;
        private String toNodeId;
        private String condition;
    }
}