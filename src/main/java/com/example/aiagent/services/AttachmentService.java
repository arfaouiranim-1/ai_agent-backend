package com.example.aiagent.services;

import com.example.aiagent.dao.AttachmentRepository;
import com.example.aiagent.entities.Attachment;
import com.example.aiagent.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Service
@Transactional
public class AttachmentService {
    private static final String UPLOAD_DIR = "uploads/";
    @Autowired private AttachmentRepository attachmentRepository;

    public Attachment upload(String nodeId, MultipartFile file) throws IOException {
        Path dir = Paths.get(UPLOAD_DIR); Files.createDirectories(dir);
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = dir.resolve(fileName);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        Attachment a = new Attachment();
        a.setNodeId(nodeId); a.setFileName(file.getOriginalFilename());
        a.setFileType(file.getContentType()); a.setFilePath(path.toString());
        return attachmentRepository.save(a);
    }

    @Transactional(readOnly = true)
    public List<Attachment> getByNode(String nodeId) { return attachmentRepository.findByNodeId(nodeId); }

    public void delete(Long id) {
        Attachment a = attachmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment introuvable : " + id));
        try { Files.deleteIfExists(Paths.get(a.getFilePath())); } catch (IOException ignored) {}
        attachmentRepository.deleteById(id);
    }
}