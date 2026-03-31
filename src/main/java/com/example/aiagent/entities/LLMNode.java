package com.example.aiagent.entities;
import com.example.aiagent.Engine.NodeResult;
import com.example.aiagent.entities.Node;
import com.example.aiagent.enums.NodeType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * LLMNode — nœud d'appel au modèle Claude (Anthropic).
 */
@Entity
@DiscriminatorValue("LLM")
@Getter
@Setter
@NoArgsConstructor                          // FIX 1 : requis par JPA
@ToString(callSuper = true)
public class LLMNode extends Node {         // FIX 2 : @AllArgsConstructor supprimé (incompatible avec héritage + defaults Lombok)

    /**
     * Prompt envoyé à Claude.
     * Supporte le placeholder {{input}} remplacé par la sortie du nœud précédent.
     * Exemple : "Résume ce texte en 3 points : {{input}}"
     */
    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "Le prompt template est obligatoire")
    private String promptTemplate;

    /**
     * Identifiant du modèle Anthropic à utiliser.
     *   - "claude-sonnet-4-6"        → recommandé (équilibre qualité/vitesse/coût)
     *   - "claude-opus-4-6"          → le plus puissant
     *   - "claude-haiku-4-5-20251001"→ rapide et économique
     */
    @Column(nullable = false)
    private String modelName = "claude-sonnet-4-6";  // FIX 3 : default garanti car @AllArgsConstructor supprimé

    /**
     * Instruction système optionnelle envoyée à Claude avant le prompt utilisateur.
     * Exemple : "Tu es un assistant expert en droit français."
     */
    @Column(columnDefinition = "TEXT")
    private String systemPrompt;

    /**
     * Nombre maximum de tokens générés en réponse.
     * Défaut : 1024. Plage conseillée : 256 – 4096.
     */
    @Column(nullable = false)
    @Min(value = 1, message = "maxTokens doit être supérieur à 0")  // FIX 4 : @DecimalMin/@DecimalMax remplacés par @Min (adapté à int)
    private int maxTokens = 1024;

    // ─────────────────────────────────────────────────────────
    // FIX 5 : constructeur explicite à la place de @AllArgsConstructor
    //         → compatible avec l'héritage (appel super())
    //         → respecte les valeurs par défaut
    // ─────────────────────────────────────────────────────────
    public LLMNode(String name, NodeType type,
                   String promptTemplate, String modelName,
                   String systemPrompt, int maxTokens) {
        super();
        setName(name);
        setType(type);
        this.promptTemplate = promptTemplate;
        this.modelName      = (modelName != null) ? modelName : "claude-sonnet-4-6";
        this.systemPrompt   = systemPrompt;
        this.maxTokens      = (maxTokens > 0)   ? maxTokens  : 1024;
    }

    @Override
    public NodeResult execute(Object answer) {
        // L'exécution réelle est déléguée à LLMNodeProcessor via GraphExecutionEngine
        return NodeResult.done("LLMNode: délégué au processor");
    }
}