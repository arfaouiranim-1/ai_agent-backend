package com.example.aiagent.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.example.aiagent.enums.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Le username est obligatoire")
    @Size(min = 3, max = 50, message = "Le username doit contenir entre 3 et 50 caractères")
    private String username;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Email invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String password;

    private Role role = Role.USER;
}