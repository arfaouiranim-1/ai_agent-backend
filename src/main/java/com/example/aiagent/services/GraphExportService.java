package com.example.aiagent.services;

import com.example.aiagent.dto.GraphExportDto;
import com.example.aiagent.entities.*;
import com.example.aiagent.exception.ResourceNotFoundException;
import com.example.aiagent.dao.CommitExportRepository;
import com.example.aiagent.dao.GraphExportRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GraphExportService {

    private final GraphExportRepository  graphExportRepository;
    private final CommitExportRepository commitExportRepository;
    private final ObjectMapper           objectMapper;

    // ═══════════════════════════════════════════════════════
    // EXPORT JSON
    // ═══════════════════════════════════════════════════════
    @Transactional(readOnly = true)
    public String exportToJson(Long graphId, Long commitId, boolean flatten) {
        GraphExportDto dto = buildExportDto(graphId, commitId, flatten);
        try {
            return objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(dto);
        } catch (Exception e) {
            throw new RuntimeException("Erreur génération JSON : " + e.getMessage(), e);
        }
    }

    // ═══════════════════════════════════════════════════════
    // EXPORT DOT
    // ═══════════════════════════════════════════════════════
    @Transactional(readOnly = true)
    public String exportToDot(Long graphId, Long commitId, boolean flatten) {
        GraphExportDto dto = buildExportDto(graphId, commitId, flatten);

        StringBuilder dot = new StringBuilder();
        dot.append("digraph \"").append(escape(dto.getName())).append("\" {\n");
        dot.append("    rankdir=TB;\n");
        dot.append("    node [shape=box, style=\"filled,rounded\", fontname=\"Helvetica\", fontsize=11];\n");
        dot.append("    graph [label=\"").append(escape(dto.getName()))
                .append("\\nCommit: ").append(escape(dto.getCommit().getMessage()))
                .append("\", fontsize=14];\n\n");

        for (GraphExportDto.NodeExportDto node : dto.getNodes()) {
            dot.append("    \"").append(node.getId()).append("\"")
                    .append(" [label=\"").append(escape(node.getName()))
                    .append("\\n[").append(node.getType()).append("]\"")
                    .append(", fillcolor=\"").append(node.getColor()).append("\"];\n");
        }
        dot.append("\n");
        for (GraphExportDto.EdgeExportDto edge : dto.getEdges()) {
            dot.append("    \"").append(edge.getFromNodeId()).append("\"")
                    .append(" -> \"").append(edge.getToNodeId()).append("\"");
            if (edge.getCondition() != null && !edge.getCondition().equals("DEFAULT")) {
                dot.append(" [label=\"").append(edge.getCondition()).append("\"]");
            }
            dot.append(";\n");
        }
        dot.append("}\n");
        return dot.toString();
    }

    // ═══════════════════════════════════════════════════════
    // EXPORT PDF
    // ═══════════════════════════════════════════════════════
    @Transactional(readOnly = true)
    public byte[] exportToPdf(Long graphId, Long commitId, boolean flatten) {
        GraphExportDto dto = buildExportDto(graphId, commitId, flatten);
        try {
            return generatePdf(dto);
        } catch (Exception e) {
            throw new RuntimeException("Erreur génération PDF : " + e.getMessage(), e);
        }
    }

    // ═══════════════════════════════════════════════════════
    // BUILD DTO
    // ═══════════════════════════════════════════════════════
    @Transactional(readOnly = true)
    public GraphExportDto buildExportDto(Long graphId, Long commitId, boolean flatten) {
        Graph graph = graphExportRepository.findByIdWithNodesAndEdges(graphId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Graphe introuvable : id=" + graphId));

        Commit commit = commitExportRepository
                .findByIdAndGraphId(commitId, String.valueOf(graphId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Commit introuvable : id=" + commitId
                                + " pour graphe=" + graphId));

        List<Node> nodes = flatten
                ? new ArrayList<>(graph.getNodes())
                : graph.getNodes();

        List<GraphExportDto.NodeExportDto> nodeDtos = nodes.stream()
                .map(n -> GraphExportDto.NodeExportDto.builder()
                        .id(n.getId())
                        .name(buildNodeLabel(n))
                        .type(n.getType() != null ? n.getType().name() : "UNKNOWN")
                        .color(nodeColor(n.getType() != null ? n.getType().name() : ""))
                        .build())
                .toList();

        List<GraphExportDto.EdgeExportDto> edgeDtos = graph.getEdges().stream()
                .map(e -> GraphExportDto.EdgeExportDto.builder()
                        .id(e.getId())
                        .fromNodeId(e.getFromNodeId())
                        .toNodeId(e.getToNodeId())
                        .condition(e.getCondition() != null
                                ? e.getCondition().name() : "DEFAULT")
                        .build())
                .toList();

        return GraphExportDto.builder()
                .id(graph.getId())
                .name(graph.getName() != null ? graph.getName() : "Graph")
                .description(graph.getDescription())
                .startNodeId(graph.getStartNodeId())
                .createdAt(graph.getCreatedAt())
                .updatedAt(graph.getUpdatedAt())
                .commit(GraphExportDto.CommitInfoDto.builder()
                        .id(commit.getId())
                        .message(commit.getMessage() != null ? commit.getMessage() : "")
                        .status(commit.getStatus())
                        .createdAt(commit.getCreatedAt())
                        .build())
                .nodes(nodeDtos)
                .edges(edgeDtos)
                .exportedAt(LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))
                .version("1.0")
                .flattened(flatten)
                .build();
    }

    // ═══════════════════════════════════════════════════════
    // PDF — 1 seule page, graphe direct  ✅ CORRIGÉ
    // ═══════════════════════════════════════════════════════
    private byte[] generatePdf(GraphExportDto dto) throws Exception {
        byte[] pngBytes = renderGraphAsPng(dto);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        com.lowagie.text.Document doc =
                new com.lowagie.text.Document(PageSize.A4.rotate());
        PdfWriter.getInstance(doc, baos);
        doc.setMargins(10, 10, 10, 10);
        doc.open();

        // ✅ Plus de header paragraphe — l'image occupe toute la page
        com.lowagie.text.Image img =
                com.lowagie.text.Image.getInstance(pngBytes);
        img.scaleToFit(
                doc.getPageSize().getWidth()  - 20,
                doc.getPageSize().getHeight() - 20);
        img.setAlignment(com.lowagie.text.Image.ALIGN_CENTER);
        doc.add(img);

        doc.close();
        return baos.toByteArray();
    }

    // ═══════════════════════════════════════════════════════
    // PNG — Java2D pur  ✅ Titre + commit + date dans l'image
    // ═══════════════════════════════════════════════════════
    private byte[] renderGraphAsPng(GraphExportDto dto) throws Exception {
        final int WIDTH  = 1200;
        final int HEIGHT = 900;
        final int NODE_W = 190;
        final int NODE_H = 64;
        final int MARGIN = 60;

        List<GraphExportDto.NodeExportDto> nodes = dto.getNodes();
        List<GraphExportDto.EdgeExportDto> edges = dto.getEdges();

        Map<String, int[]> positions =
                computePositions(nodes, edges, WIDTH, NODE_W, NODE_H, MARGIN);

        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Fond blanc
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        // ── Arêtes
        for (GraphExportDto.EdgeExportDto edge : edges) {
            if (edge.getFromNodeId() == null || edge.getToNodeId() == null) continue;
            int[] from = positions.get(edge.getFromNodeId());
            int[] to   = positions.get(edge.getToNodeId());
            if (from == null || to == null) continue;

            int x1 = from[0] + NODE_W / 2;
            int y1 = from[1] + NODE_H;
            int x2 = to[0]   + NODE_W / 2;
            int y2 = to[1];

            g2.setColor(new Color(80, 80, 80));
            g2.setStroke(new BasicStroke(2f));
            g2.drawLine(x1, y1, x2, y2);
            drawArrow(g2, x1, y1, x2, y2);

            String cond = edge.getCondition();
            if (cond != null && !cond.equals("DEFAULT") && !cond.isBlank()) {
                int mx = (x1 + x2) / 2;
                int my = (y1 + y2) / 2;
                g2.setColor(new Color(30, 30, 180));
                g2.setFont(new Font("Helvetica", Font.BOLD, 11));
                g2.drawString(cond, mx + 5, my - 4);
            }
        }

        // ── Nœuds
        for (GraphExportDto.NodeExportDto node : nodes) {
            if (node.getId() == null) continue;
            int[] pos = positions.get(String.valueOf(node.getId()));
            if (pos == null) continue;

            int x = pos[0];
            int y = pos[1];

            // Ombre
            g2.setColor(new Color(180, 180, 180, 120));
            g2.fillRoundRect(x + 4, y + 4, NODE_W, NODE_H, 14, 14);

            // Fond coloré
            String color = node.getColor() != null ? node.getColor() : "#ffffff";
            g2.setColor(hexToAwtColor(color));
            g2.fillRoundRect(x, y, NODE_W, NODE_H, 14, 14);

            // Bordure
            g2.setColor(new Color(100, 100, 100));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(x, y, NODE_W, NODE_H, 14, 14);

            String name = node.getName() != null ? node.getName() : "?";
            String type = node.getType() != null ? node.getType() : "UNKNOWN";
            String[] lines = name.split("\n");

            if (lines.length == 1) {
                g2.setColor(new Color(20, 20, 20));
                g2.setFont(new Font("Helvetica", Font.BOLD, 11));
                drawCenteredString(g2, lines[0], x, y + 24, NODE_W);
                g2.setColor(new Color(70, 70, 70));
                g2.setFont(new Font("Helvetica", Font.PLAIN, 9));
                drawCenteredString(g2, "[" + type + "]", x, y + 42, NODE_W);
            } else {
                g2.setColor(new Color(20, 20, 20));
                g2.setFont(new Font("Helvetica", Font.BOLD, 10));
                drawCenteredString(g2, lines[0], x, y + 18, NODE_W);
                g2.setColor(new Color(50, 50, 160));
                g2.setFont(new Font("Helvetica", Font.ITALIC, 9));
                drawCenteredString(g2, lines[1], x, y + 33, NODE_W);
                g2.setColor(new Color(70, 70, 70));
                g2.setFont(new Font("Helvetica", Font.PLAIN, 8));
                drawCenteredString(g2, "[" + type + "]", x, y + 50, NODE_W);
            }
        }

        // ✅ Titre dans l'image
        g2.setFont(new Font("Helvetica", Font.BOLD, 15));
        g2.setColor(new Color(26, 39, 68));
        g2.drawString(dto.getName() != null ? dto.getName() : "Graph", MARGIN, 35);

        // ✅ Commit + date dans l'image (remplace le header PDF supprimé)
        g2.setFont(new Font("Helvetica", Font.PLAIN, 9));
        g2.setColor(new Color(100, 100, 100));
        g2.drawString(
                "Commit: " + dto.getCommit().getMessage() + "   |   " + dto.getExportedAt(),
                MARGIN, 52);

        g2.dispose();

        ByteArrayOutputStream pngBaos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", pngBaos);
        return pngBaos.toByteArray();
    }

    // ═══════════════════════════════════════════════════════
    // LAYOUT BFS
    // ═══════════════════════════════════════════════════════
    private Map<String, int[]> computePositions(
            List<GraphExportDto.NodeExportDto> nodes,
            List<GraphExportDto.EdgeExportDto> edges,
            int width, int nodeW, int nodeH, int margin) {

        Map<String, int[]>        pos      = new LinkedHashMap<>();
        Map<String, List<String>> children = new LinkedHashMap<>();
        Map<String, Integer>      level    = new LinkedHashMap<>();

        for (GraphExportDto.NodeExportDto n : nodes) {
            children.put(String.valueOf(n.getId()), new ArrayList<>());
        }
        for (GraphExportDto.EdgeExportDto e : edges) {
            if (e.getFromNodeId() == null || e.getToNodeId() == null) continue;
            List<String> c = children.get(e.getFromNodeId());
            if (c != null) c.add(e.getToNodeId());
        }

        if (nodes.isEmpty()) return pos;

        Queue<String> queue = new LinkedList<>();
        String        start = String.valueOf(nodes.get(0).getId());
        queue.add(start);
        level.put(start, 0);

        while (!queue.isEmpty()) {
            String cur = queue.poll();
            int    lv  = level.get(cur);
            for (String child : children.getOrDefault(cur, List.of())) {
                if (!level.containsKey(child)) {
                    level.put(child, lv + 1);
                    queue.add(child);
                }
            }
        }

        int maxLv = level.values().stream().mapToInt(i -> i).max().orElse(0);
        for (GraphExportDto.NodeExportDto n : nodes) {
            level.putIfAbsent(String.valueOf(n.getId()), maxLv + 1);
        }

        Map<Integer, List<String>> byLevel = new TreeMap<>();
        for (Map.Entry<String, Integer> e : level.entrySet()) {
            byLevel.computeIfAbsent(e.getValue(), k -> new ArrayList<>()).add(e.getKey());
        }

        int levelHeight = nodeH + 80;
        for (Map.Entry<Integer, List<String>> e : byLevel.entrySet()) {
            int          lv    = e.getKey();
            List<String> ids   = e.getValue();
            int          count = ids.size();
            int          total = count * nodeW + (count - 1) * 40;
            int          sx    = (width - total) / 2;
            int          y     = margin + lv * levelHeight;
            for (int i = 0; i < ids.size(); i++) {
                pos.put(ids.get(i), new int[]{sx + i * (nodeW + 40), y});
            }
        }
        return pos;
    }

    // ═══════════════════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════════════════
    private String buildNodeLabel(Node n) {
        if (n == null) return "?";
        String name = n.getName() != null ? n.getName() : "?";

        return switch (n.getType()) {
            case QUESTION -> {
                if (n instanceof QuestionNode qn
                        && qn.getQuestionText() != null
                        && !qn.getQuestionText().isBlank())
                    yield name + "\n" + truncate(qn.getQuestionText(), 28);
                yield name;
            }
            case ANSWER -> {
                if (n instanceof AnswerNode an
                        && an.getResponseKey() != null
                        && !an.getResponseKey().isBlank())
                    yield name + "\n" + truncate(an.getResponseKey(), 28);
                yield name;
            }
            case LLM -> {
                if (n instanceof LLMNode llm && llm.getModelName() != null)
                    yield name + "\n" + llm.getModelName();
                yield name;
            }
            case END -> {
                if (n instanceof EndNode en
                        && en.getResultMessage() != null
                        && !en.getResultMessage().isBlank())
                    yield name + "\n" + truncate(en.getResultMessage(), 28);
                yield name;
            }
            default -> name;
        };
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max) + "…" : s;
    }

    private void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int    size  = 10;
        int[]  ax    = {x2,
                (int)(x2 - size * Math.cos(angle - 0.4)),
                (int)(x2 - size * Math.cos(angle + 0.4))};
        int[]  ay    = {y2,
                (int)(y2 - size * Math.sin(angle - 0.4)),
                (int)(y2 - size * Math.sin(angle + 0.4))};
        g2.setColor(new Color(80, 80, 80));
        g2.fillPolygon(ax, ay, 3);
    }

    private void drawCenteredString(Graphics2D g2, String text,
                                    int x, int y, int width) {
        if (text == null) text = "";
        FontMetrics fm = g2.getFontMetrics();
        String t = text;
        while (fm.stringWidth(t) > width - 12 && t.length() > 3) {
            t = t.substring(0, t.length() - 1);
        }
        if (!t.equals(text)) t += "…";
        g2.drawString(t, x + (width - fm.stringWidth(t)) / 2, y);
    }

    private Color hexToAwtColor(String hex) {
        hex = hex.replace("#", "");
        return new Color(
                Integer.parseInt(hex.substring(0, 2), 16),
                Integer.parseInt(hex.substring(2, 4), 16),
                Integer.parseInt(hex.substring(4, 6), 16));
    }

    private String nodeColor(String type) {
        return switch (type) {
            case "START"        -> "#c8e6c9";
            case "END"          -> "#e1bee7";
            case "LLM"          -> "#fff9c4";
            case "FETCH"        -> "#b3e5fc";
            case "ANSWER"       -> "#ffe0b2";
            case "NOTIFICATION" -> "#fce4ec";
            case "RESUME"       -> "#f3e5f5";
            case "QUESTION"     -> "#f5f5f5";
            default             -> "#ffffff";
        };
    }

    private String escape(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
    }
}
