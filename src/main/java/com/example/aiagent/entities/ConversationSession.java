package com.example.aiagent.entities;

import com.example.aiagent.enums.SessionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conversation_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Graphe en cours d'exécution
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graph_id", nullable = false)
    private Graph graph;

    // Utilisateur qui exécute
    private Long userId;

    // Nœud courant
    private Long currentNodeId;

    // Statut de la session
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status;

    // Données collectées (JSON)
    @Column(columnDefinition = "TEXT")
    private String collectedData;

    // Résultat final
    @Column(columnDefinition = "TEXT")
    private String finalResult;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Messages échangés
    @OneToMany(mappedBy = "session",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @Builder.Default
    private List<ConversationMessage> messages = new ArrayList<>();
}
