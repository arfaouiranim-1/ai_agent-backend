package com.example.aiagent.services;

import com.example.aiagent.Engine.LLMNodeProcessor;
import com.example.aiagent.Engine.NodeResult;
import com.example.aiagent.dto.ConversationAnswerDto;
import com.example.aiagent.dto.ConversationSessionDto;
import com.example.aiagent.entities.*;
import com.example.aiagent.enums.*;
import com.example.aiagent.exception.ResourceNotFoundException;
import com.example.aiagent.dao.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationSessionRepository  sessionRepository;
    private final ConversationMessageRepository  messageRepository;
    private final GraphRepository                graphRepository;
    private final NodeRepository                 nodeRepository;
    private final EdgeRepository                 edgeRepository;
    private final LLMNodeProcessor               llmProcessor;
    private final ObjectMapper                   objectMapper;

    // ═══════════════════════════════════════════════════════
    // DÉMARRER une session
    // ═══════════════════════════════════════════════════════
    @Transactional
    public ConversationSessionDto start(Long graphId, Long userId) {
        Graph graph = graphRepository.findById(graphId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Graphe introuvable : id=" + graphId));

        Node startNode = nodeRepository
                .findByGraphIdAndType(graphId, NodeType.START)
                .stream().findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Nœud START introuvable dans le graphe"));

        ConversationSession session = ConversationSession.builder()
                .graph(graph)
                .userId(userId)
                .currentNodeId(startNode.getId())
                .status(SessionStatus.STARTED)
                .collectedData("{}")
                .build();
        session = sessionRepository.save(session);

        return advance(session, null);
    }

    // ═══════════════════════════════════════════════════════
    // RÉPONDRE à une question
    // ═══════════════════════════════════════════════════════
    @Transactional
    public ConversationSessionDto answer(Long sessionId,
                                         Long userId,
                                         ConversationAnswerDto answerDto) {
        ConversationSession session = sessionRepository
                .findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Session introuvable : id=" + sessionId));

        if (session.getStatus() == SessionStatus.COMPLETED) {
            throw new RuntimeException("Session déjà terminée");
        }

        String answerText = extractAnswerText(answerDto);
        saveMessage(session, session.getCurrentNodeId(),
                MessageRole.USER, answerText, null);

        updateCollectedData(session, answerText);

        return advance(session, answerDto);
    }

    // ═══════════════════════════════════════════════════════
    // RÉCUPÉRER une session
    // ═══════════════════════════════════════════════════════
    @Transactional(readOnly = true)
    public ConversationSessionDto getSession(Long sessionId) {
        ConversationSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Session introuvable : id=" + sessionId));
        return toDto(session);
    }

    // ═══════════════════════════════════════════════════════
    // LISTER les sessions d'un graphe
    // ═══════════════════════════════════════════════════════
    @Transactional(readOnly = true)
    public List<ConversationSessionDto> getByGraph(Long graphId) {
        return sessionRepository
                .findByGraphIdOrderByCreatedAtDesc(graphId)
                .stream().map(this::toDto)
                .collect(Collectors.toList());
    }

    // ═══════════════════════════════════════════════════════
    // AVANCER dans le graphe
    // ═══════════════════════════════════════════════════════
    private ConversationSessionDto advance(ConversationSession session,
                                           ConversationAnswerDto lastAnswer) {
        Long currentNodeId = session.getCurrentNodeId();

        String condition = resolveCondition(lastAnswer);
        Optional<Edge> nextEdge = edgeRepository
                .findByFromNodeIdAndGraphId(
                        String.valueOf(currentNodeId),
                        session.getGraph().getId())
                .stream()
                .filter(e -> {
                    if (e.getCondition() == null) return true;
                    String cond = e.getCondition().name();
                    return condition.equals(cond) || "DEFAULT".equals(cond);
                })
                .findFirst();

        if (nextEdge.isEmpty()) {
            session.setStatus(SessionStatus.COMPLETED);
            sessionRepository.save(session);
            return toDto(session);
        }

        Long nextNodeId = Long.parseLong(nextEdge.get().getToNodeId());
        Node nextNode   = nodeRepository.findById(nextNodeId)
                .orElseThrow(() -> new RuntimeException(
                        "Nœud suivant introuvable : id=" + nextNodeId));

        session.setCurrentNodeId(nextNodeId);

        switch (nextNode.getType()) {

            case START -> advance(session, null);

            case END -> {
                EndNode end = (EndNode) nextNode;
                session.setStatus(SessionStatus.COMPLETED);
                session.setFinalResult(end.getResultMessage());
                saveMessage(session, nextNodeId,
                        MessageRole.AGENT,
                        end.getResultMessage() != null
                                ? end.getResultMessage()
                                : "Traitement terminé.", null);
            }

            case QUESTION -> {
                QuestionNode qn = (QuestionNode) nextNode;
                List<String> options = resolveOptions(nextNode);
                saveMessage(session, nextNodeId,
                        MessageRole.AGENT,
                        qn.getQuestionText(), options);
                session.setStatus(SessionStatus.WAITING);
            }

            case ANSWER -> {
                AnswerNode an = (AnswerNode) nextNode;
                saveMessage(session, nextNodeId,
                        MessageRole.AGENT,
                        an.getResponseKey(), null);
                sessionRepository.save(session);
                return advance(session, null);
            }

            case LLM -> {
                session.setStatus(SessionStatus.PROCESSING);
                sessionRepository.save(session);
                try {
                    String input      = getCollectedDataAsText(session);
                    NodeResult result = llmProcessor.process((LLMNode) nextNode, input);
                    String response   = result != null && result.getOutput() != null
                            ? result.getOutput().toString()
                            : "Erreur LLM";
                    saveMessage(session, nextNodeId, MessageRole.AGENT, response, null);
                    session.setStatus(SessionStatus.WAITING);
                    sessionRepository.save(session);
                    return advance(session, null);
                } catch (Exception e) {
                    session.setStatus(SessionStatus.ERROR);
                    log.error("Erreur LLM : {}", e.getMessage());
                }
            }

            case NOTIFICATION -> {
                saveMessage(session, nextNodeId,
                        MessageRole.AGENT,
                        "Notification envoyée", null);
                sessionRepository.save(session);
                return advance(session, null);
            }

            default -> session.setStatus(SessionStatus.ERROR);
        }

        sessionRepository.save(session);
        return toDto(session);
    }

    // ═══════════════════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════════════════
    private String resolveCondition(ConversationAnswerDto answer) {
        if (answer == null) return "DEFAULT";
        if (answer.getYesNo() != null) {
            return answer.getYesNo() ? "YES" : "NO";
        }
        return "DEFAULT";
    }

    private List<String> resolveOptions(Node node) {
        if (node instanceof SingleChoiceQuestionNode n)   return n.getOptions();
        if (node instanceof MultipleChoiceQuestionNode n) return n.getOptions();
        return null;
    }

    private String extractAnswerText(ConversationAnswerDto dto) {
        if (dto.getAnswer()          != null) return dto.getAnswer();
        if (dto.getYesNo()           != null) return dto.getYesNo() ? "YES" : "NO";
        if (dto.getSelectedOption()  != null) return dto.getSelectedOption();
        if (dto.getSelectedOptions() != null) return String.join(", ", dto.getSelectedOptions());
        return "";
    }

    private void updateCollectedData(ConversationSession session, String value) {
        try {
            Map<String, Object> data = objectMapper.readValue(
                    session.getCollectedData() != null
                            ? session.getCollectedData() : "{}",
                    new TypeReference<>() {});
            data.put("answer_node_" + session.getCurrentNodeId(), value);
            session.setCollectedData(objectMapper.writeValueAsString(data));
        } catch (Exception e) {
            log.warn("Erreur update collected data : {}", e.getMessage());
        }
    }

    private String getCollectedDataAsText(ConversationSession session) {
        try {
            Map<String, Object> data = objectMapper.readValue(
                    session.getCollectedData() != null
                            ? session.getCollectedData() : "{}",
                    new TypeReference<>() {});
            return data.values().stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
        } catch (Exception e) {
            return "";
        }
    }

    private void saveMessage(ConversationSession session,
                             Long nodeId,
                             MessageRole role,
                             String content,
                             List<String> options) {
        try {
            String optionsJson = options != null
                    ? objectMapper.writeValueAsString(options) : null;
            ConversationMessage msg = ConversationMessage.builder()
                    .session(session)
                    .nodeId(nodeId)
                    .role(role)
                    .content(content)
                    .optionsJson(optionsJson)
                    .build();
            messageRepository.save(msg);
        } catch (Exception e) {
            log.error("Erreur save message : {}", e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════
    // toDto — avec currentNodeType
    // ═══════════════════════════════════════════════════════
    private ConversationSessionDto toDto(ConversationSession session) {
        List<ConversationMessage> messages = messageRepository
                .findBySessionIdOrderByCreatedAtAsc(session.getId());

        // ── currentNodeType ──────────────────────────────
        String currentNodeType = null;
        if (session.getCurrentNodeId() != null) {
            currentNodeType = nodeRepository
                    .findById(session.getCurrentNodeId())
                    .map(n -> n.getType().name())
                    .orElse(null);
        }

        // ── currentQuestion ──────────────────────────────
        ConversationSessionDto.CurrentQuestionDto currentQuestion = null;
        if (session.getStatus() == SessionStatus.WAITING
                && session.getCurrentNodeId() != null) {
            Node node = nodeRepository
                    .findById(session.getCurrentNodeId())
                    .orElse(null);
            if (node instanceof QuestionNode qn) {
                currentQuestion = ConversationSessionDto.CurrentQuestionDto.builder()
                        .nodeId(qn.getId())
                        .questionText(qn.getQuestionText())
                        .questionType(qn.getQuestionType().name())
                        .options(resolveOptions(node))
                        .build();
            }
        }

        // ── collectedData ────────────────────────────────
        Map<String, Object> collectedData = new LinkedHashMap<>();
        try {
            if (session.getCollectedData() != null) {
                collectedData = objectMapper.readValue(
                        session.getCollectedData(),
                        new TypeReference<>() {});
            }
        } catch (Exception ignored) {}

        // ── build DTO ────────────────────────────────────
        return ConversationSessionDto.builder()
                .id(session.getId())
                .graphId(session.getGraph().getId())
                .graphName(session.getGraph().getName())
                .currentNodeId(session.getCurrentNodeId())
                .currentNodeType(currentNodeType)           // ← fix
                .status(session.getStatus())
                .finalResult(session.getFinalResult())
                .createdAt(session.getCreatedAt())
                .currentQuestion(currentQuestion)
                .collectedData(collectedData)
                .messages(messages.stream().map(m -> {
                    List<String> opts = null;
                    try {
                        if (m.getOptionsJson() != null) {
                            opts = objectMapper.readValue(
                                    m.getOptionsJson(),
                                    new TypeReference<>() {});
                        }
                    } catch (Exception ignored) {}
                    return ConversationSessionDto.MessageDto.builder()
                            .nodeId(m.getNodeId())
                            .role(m.getRole().name())
                            .content(m.getContent())
                            .options(opts)
                            .createdAt(m.getCreatedAt().toString())
                            .build();
                }).collect(Collectors.toList()))
                .build();
    }
}