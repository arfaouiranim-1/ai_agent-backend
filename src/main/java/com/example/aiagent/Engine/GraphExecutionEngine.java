package com.example.aiagent.Engine;

import com.example.aiagent.dao.EdgeRepository;
import com.example.aiagent.dao.ExecutionSessionRepository;
import com.example.aiagent.dao.GraphRepository;
import com.example.aiagent.dao.NodeRepository;
import com.example.aiagent.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class GraphExecutionEngine {

    @Autowired private GraphRepository            graphRepository;
    @Autowired private NodeRepository             nodeRepository;
    @Autowired private EdgeRepository             edgeRepository;
    @Autowired private ExecutionSessionRepository sessionRepository;
    @Autowired private ConditionResolver          conditionResolver;

    @Autowired(required = false)
    private List<NodeProcessorStrategy> processors;

    // Timeout : sessions WAITING depuis plus de X minutes → ERROR
    @Value("${execution.session.timeout-minutes:60}")
    private int sessionTimeoutMinutes;

    // ── Démarrer une exécution ─────────────────────────────
    @Transactional
    public ExecutionSession start(String graphId) {
        Long gId = Long.parseLong(graphId);
        Graph graph = graphRepository.findById(gId)
                .orElseThrow(() -> new RuntimeException("Graph introuvable : " + graphId));

        if (graph.getStartNodeId() == null)
            throw new RuntimeException("Ce graph n'a pas de StartNode défini");

        ExecutionSession session = new ExecutionSession();
        session.setGraphId(graphId);
        session = sessionRepository.save(session);
        return traverse(session, graph.getStartNodeId(), null);
    }

    // ── Continuer une exécution ────────────────────────────
    @Transactional
    public ExecutionSession continueExecution(String sessionId, Object answer) {
        Long sId = Long.parseLong(sessionId);
        ExecutionSession session = sessionRepository.findById(sId)
                .orElseThrow(() -> new RuntimeException("Session introuvable : " + sessionId));

        if (session.getStatus() == ExecutionSession.Status.COMPLETED)
            throw new RuntimeException("Session déjà terminée.");
        if (session.getStatus() == ExecutionSession.Status.ERROR)
            throw new RuntimeException("Session en erreur — impossible de continuer.");

        return traverse(session, session.getCurrentNodeId(), answer);
    }

    // ── Traversée du graph ─────────────────────────────────
    private ExecutionSession traverse(ExecutionSession session, String startNodeId, Object startInput) {
        String currentNodeId = startNodeId;
        Object currentInput  = startInput;

        while (currentNodeId != null) {
            final String nodeId = currentNodeId;

            try {
                Long nId = Long.parseLong(nodeId);
                Node node = nodeRepository.findById(nId)
                        .orElseThrow(() -> new RuntimeException("Node introuvable : " + nodeId));

                NodeResult result = executeNode(node, currentInput);

                if (result.isWaiting()) {
                    session.setCurrentNodeId(nodeId);
                    session.setPendingMessage(result.getMessage());
                    session.setStatus(ExecutionSession.Status.WAITING);
                    return sessionRepository.save(session);
                }

                if (result.isCompleted()) {
                    session.setCurrentNodeId(nodeId);
                    session.setPendingMessage(result.getMessage());
                    session.setLastOutput(result.getMessage());
                    session.setStatus(ExecutionSession.Status.COMPLETED);
                    return sessionRepository.save(session);
                }

                session.setLastOutput(result.getOutput() != null ? result.getOutput().toString() : "");
                List<Edge> edges = edgeRepository.findByFromNodeId(nodeId);
                currentNodeId = conditionResolver.resolve(edges, result.getCondition());
                currentInput  = result.getOutput();

            } catch (Exception e) {
                // ── 7. Gestion erreurs — mettre la session en ERROR ──
                session.setStatus(ExecutionSession.Status.ERROR);
                session.setPendingMessage("Erreur sur le nœud " + nodeId + " : " + e.getMessage());
                sessionRepository.save(session);
                throw new RuntimeException("Erreur d'exécution sur le nœud " + nodeId + " : " + e.getMessage(), e);
            }
        }

        session.setStatus(ExecutionSession.Status.COMPLETED);
        return sessionRepository.save(session);
    }

    private NodeResult executeNode(Node node, Object input) {
        if (processors != null)
            for (NodeProcessorStrategy p : processors)
                if (p.supports(node)) return p.process(node, input);
        return node.execute(input);
    }

    // ── 8. Timeout — job planifié toutes les 15 minutes ───
    @Scheduled(fixedDelay = 900000) // toutes les 15 minutes
    @Transactional
    public void expireTimedOutSessions() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(sessionTimeoutMinutes);
        List<ExecutionSession> stuck = sessionRepository
                .findByStatusAndUpdatedAtBefore(ExecutionSession.Status.WAITING, cutoff);

        for (ExecutionSession session : stuck) {
            session.setStatus(ExecutionSession.Status.ERROR);
            session.setPendingMessage("Session expirée après " + sessionTimeoutMinutes + " minutes d'inactivité");
            sessionRepository.save(session);
        }

        if (!stuck.isEmpty())
            System.out.println("[Timeout] " + stuck.size() + " session(s) expirée(s)");
    }
}