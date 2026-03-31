package com.example.aiagent.mapper;

import com.example.aiagent.dto.*;
import com.example.aiagent.entities.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NodeMapper {

    // ─────────────────────────────────────────────────────────
    // Entity → DTO
    // ─────────────────────────────────────────────────────────
    public BaseNodeDto toDto(Node node) {
        return switch (node.getType()) {

            case START -> {
                StartNodeDto dto = new StartNodeDto();
                dto.setId(node.getId());
                dto.setName(node.getName());
                dto.setType(node.getType());
                yield dto;
            }

            case END -> {
                EndNode n = (EndNode) node;
                EndNodeDto dto = new EndNodeDto();
                dto.setId(n.getId());
                dto.setName(n.getName());
                dto.setType(n.getType());
                dto.setResultMessage(n.getResultMessage());
                yield dto;
            }

            case LLM -> {
                LLMNode n = (LLMNode) node;
                LLMNodeDto dto = new LLMNodeDto();
                dto.setId(n.getId());
                dto.setName(n.getName());
                dto.setType(n.getType());
                dto.setPromptTemplate(n.getPromptTemplate());
                dto.setModelName(n.getModelName());
                dto.setSystemPrompt(n.getSystemPrompt());
                dto.setMaxTokens(n.getMaxTokens());
                yield dto;
            }

            case FETCH -> {
                FetchNode n = (FetchNode) node;
                FetchNodeDto dto = new FetchNodeDto();
                dto.setId(n.getId());
                dto.setName(n.getName());
                dto.setType(n.getType());
                dto.setUrl(n.getUrl());
                dto.setMethod(n.getMethod());
                dto.setHeadersJson(n.getHeadersJson());
                yield dto;
            }

            case ANSWER -> {
                AnswerNode n = (AnswerNode) node;
                AnswerNodeDto dto = new AnswerNodeDto();
                dto.setId(n.getId());
                dto.setName(n.getName());
                dto.setType(n.getType());
                dto.setResponseKey(n.getResponseKey());
                yield dto;
            }

            case NOTIFICATION -> {
                NotificationNode n = (NotificationNode) node;
                NotificationNodeDto dto = new NotificationNodeDto();
                dto.setId(n.getId());
                dto.setName(n.getName());
                dto.setType(n.getType());
                dto.setChannel(n.getChannel());
                dto.setTemplate(n.getTemplate());
                dto.setRecipient(n.getRecipient());
                yield dto;
            }

            case RESUME -> {
                ResumeNode n = (ResumeNode) node;
                ResumeNodeDto dto = new ResumeNodeDto();
                dto.setId(n.getId());
                dto.setName(n.getName());
                dto.setType(n.getType());
                dto.setResumeTemplate(n.getResumeTemplate());
                yield dto;
            }

            case QUESTION -> {
                QuestionNode qn = (QuestionNode) node;
                QuestionNodeDto dto = new QuestionNodeDto();
                dto.setId(qn.getId());
                dto.setName(qn.getName());
                dto.setType(qn.getType());
                dto.setQuestionText(qn.getQuestionText());
                dto.setQuestionType(qn.getQuestionType());
                // options uniquement pour les sous-types qui en ont
                if (qn instanceof SingleChoiceQuestionNode n) {
                    dto.setOptions(n.getOptions());
                } else if (qn instanceof MultipleChoiceQuestionNode n) {
                    dto.setOptions(n.getOptions());
                }
                yield dto;
            }
        };
    }

    // ─────────────────────────────────────────────────────────
    // Entity list → DTO list
    // ─────────────────────────────────────────────────────────
    public List<BaseNodeDto> toDtoList(List<Node> nodes) {
        return nodes.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────
    // DTO → Entity
    // ─────────────────────────────────────────────────────────
    public Node toEntity(BaseNodeDto dto) {
        return switch (dto.getType()) {

            case START -> {
                StartNode n = new StartNode();
                n.setName(dto.getName());
                n.setType(dto.getType());
                yield n;
            }

            case END -> {
                EndNodeDto d = (EndNodeDto) dto;
                EndNode n = new EndNode();
                n.setName(d.getName());
                n.setType(d.getType());
                n.setResultMessage(d.getResultMessage());
                yield n;
            }

            case LLM -> {
                LLMNodeDto d = (LLMNodeDto) dto;
                LLMNode n = new LLMNode();
                n.setName(d.getName());
                n.setType(d.getType());
                n.setPromptTemplate(d.getPromptTemplate());
                n.setModelName(d.getModelName() != null
                        ? d.getModelName() : "claude-sonnet-4-6");
                n.setSystemPrompt(d.getSystemPrompt());
                n.setMaxTokens(d.getMaxTokens() > 0 ? d.getMaxTokens() : 1024);
                yield n;
            }

            case FETCH -> {
                FetchNodeDto d = (FetchNodeDto) dto;
                FetchNode n = new FetchNode();
                n.setName(d.getName());
                n.setType(d.getType());
                n.setUrl(d.getUrl());
                n.setMethod(d.getMethod());
                n.setHeadersJson(d.getHeadersJson());
                yield n;
            }

            case ANSWER -> {
                AnswerNodeDto d = (AnswerNodeDto) dto;
                AnswerNode n = new AnswerNode();
                n.setName(d.getName());
                n.setType(d.getType());
                n.setResponseKey(d.getResponseKey());
                yield n;
            }

            case NOTIFICATION -> {
                NotificationNodeDto d = (NotificationNodeDto) dto;
                NotificationNode n = new NotificationNode();
                n.setName(d.getName());
                n.setType(d.getType());
                n.setChannel(d.getChannel());
                n.setTemplate(d.getTemplate());
                n.setRecipient(d.getRecipient());
                yield n;
            }

            case RESUME -> {
                ResumeNodeDto d = (ResumeNodeDto) dto;
                ResumeNode n = new ResumeNode();
                n.setName(d.getName());
                n.setType(d.getType());
                n.setResumeTemplate(d.getResumeTemplate());
                yield n;
            }

            case QUESTION -> {
                QuestionNodeDto d = (QuestionNodeDto) dto;
                QuestionNode n = switch (d.getQuestionType()) {
                    case YES_NO   -> new YesNoQuestionNode();
                    case FREE_FORM -> new FreeFormNode();
                    case SINGLE_CHOICE -> {
                        SingleChoiceQuestionNode s = new SingleChoiceQuestionNode();
                        s.setOptions(d.getOptions());
                        yield s;
                    }
                    case MULTIPLE_CHOICE -> {
                        MultipleChoiceQuestionNode m = new MultipleChoiceQuestionNode();
                        m.setOptions(d.getOptions());
                        yield m;
                    }
                };
                n.setName(d.getName());
                n.setType(d.getType());
                n.setQuestionText(d.getQuestionText());
                n.setQuestionType(d.getQuestionType());
                yield n;
            }
        };
    }
}