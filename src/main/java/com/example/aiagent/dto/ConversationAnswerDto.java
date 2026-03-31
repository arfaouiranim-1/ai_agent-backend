package com.example.aiagent.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ConversationAnswerDto {

    // Réponse texte libre (FREE_FORM)
    private String answer;

    // Réponse YES/NO
    private Boolean yesNo;

    // Réponse choix unique (SINGLE_CHOICE)
    private String selectedOption;

    // Réponses choix multiples (MULTIPLE_CHOICE)
    private List<String> selectedOptions;
}