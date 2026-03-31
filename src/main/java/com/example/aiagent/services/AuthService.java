package com.example.aiagent.services;

import com.example.aiagent.dao.UserRepository;
import com.example.aiagent.dto.AuthResponse;
import com.example.aiagent.dto.LoginRequest;
import com.example.aiagent.dto.RegisterRequest;
import com.example.aiagent.entities.User;
import com.example.aiagent.enums.Role;
import com.example.aiagent.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository        userRepository;
    private final PasswordEncoder       passwordEncoder;
    private final JwtService            jwtService;
    private final AuthenticationManager authenticationManager;

    // ── Inscription ────────────────────────────────────────
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new RuntimeException("Username déjà utilisé : " + request.getUsername());
        if (userRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("Email déjà utilisé : " + request.getEmail());

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : Role.USER);

        userRepository.save(user);
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getUsername(), user.getEmail(), user.getRole().name());
    }

    // ── Connexion ──────────────────────────────────────────
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getUsername(), user.getEmail(), user.getRole().name());
    }
}