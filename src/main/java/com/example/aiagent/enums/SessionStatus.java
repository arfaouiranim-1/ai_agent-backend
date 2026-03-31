package com.example.aiagent.enums;

public enum SessionStatus {
    STARTED,    // session démarrée
    WAITING,    // en attente de réponse utilisateur
    PROCESSING, // LLM ou FETCH en cours
    COMPLETED,  // graphe terminé
    ERROR       // erreur
}