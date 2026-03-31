package com.example.aiagent.services;

import com.example.aiagent.exception.ResourceNotFoundException;
import com.example.aiagent.dao.GraphRepository;
import com.example.aiagent.dao.NodeRepository;
import com.example.aiagent.dto.BaseNodeDto;
import com.example.aiagent.entities.Graph;
import com.example.aiagent.entities.Node;
import com.example.aiagent.mapper.NodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NodeService {

    @Autowired private NodeRepository  nodeRepository;
    @Autowired private GraphRepository graphRepository;
    @Autowired private NodeMapper      nodeMapper;

    public BaseNodeDto create(Long graphId, BaseNodeDto dto) {
        Graph graph = graphRepository.findById(graphId)
                .orElseThrow(() -> new ResourceNotFoundException("Graph introuvable : " + graphId));
        Node node = nodeMapper.toEntity(dto);
        node.setGraph(graph);
        return nodeMapper.toDto(nodeRepository.save(node));
    }

    @Transactional(readOnly = true)
    public BaseNodeDto getById(Long id) {
        return nodeRepository.findById(id)
                .map(nodeMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Node introuvable : " + id));
    }

    @Transactional(readOnly = true)
    public List<BaseNodeDto> getByGraph(Long graphId) {
        return nodeMapper.toDtoList(nodeRepository.findByGraph_Id(graphId));
    }

    public BaseNodeDto update(Long id, BaseNodeDto dto) {
        if (!nodeRepository.existsById(id))
            throw new ResourceNotFoundException("Node introuvable : " + id);
        Node node = nodeMapper.toEntity(dto);
        node.setId(id);
        return nodeMapper.toDto(nodeRepository.save(node));
    }

    public void delete(Long id) {
        if (!nodeRepository.existsById(id))
            throw new ResourceNotFoundException("Node introuvable : " + id);
        nodeRepository.deleteById(id);
    }
}