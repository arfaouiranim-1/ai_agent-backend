package com.example.aiagent.mapper;

import com.example.aiagent.dto.CommitDto;
import com.example.aiagent.entities.Commit;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommitMapper {
    public CommitDto toDto(Commit c) {
        CommitDto d = new CommitDto();
        d.setId(c.getId()); d.setGraphId(c.getGraphId()); d.setMessage(c.getMessage());
        d.setStatus(c.getStatus()); d.setCreatedAt(c.getCreatedAt()); return d;
    }
    public List<CommitDto> toDtoList(List<Commit> list) {
        return list == null ? List.of() : list.stream().map(this::toDto).collect(Collectors.toList());
    }
}