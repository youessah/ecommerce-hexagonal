package com.ecommerce.domain.service;

import com.ecommerce.domain.exception.AuthenticationException;
import com.ecommerce.domain.model.User;
import com.ecommerce.domain.port.input.AuthenticationUseCase;
import com.ecommerce.domain.port.output.PasswordEncoderPort;
import com.ecommerce.domain.port.output.TokenGeneratorPort;
import com.ecommerce.domain.port.output.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * SERVICE MÉTIER - Logique d'authentification.
 *
 * Implémente le port d'entrée AuthenticationUseCase.
 * Dépend uniquement de ports (interfaces), jamais d'implémentations concrètes.
 *
 * Design Patterns utilisés :
 * - Strategy : UserRepositoryPort peut être MySQL, MongoDB ou JSON selon config
 * - Facade : expose une interface simple (login/register) qui orchestre plusieurs ports
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService implements AuthenticationUseCase {

    // Injecté dynamiquement selon la configuration (Strategy Pattern)
    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenGeneratorPort tokenGenerator;

    @Override
    public AuthResult login(String username, String password) {
        log.info("Tentative de connexion pour l'utilisateur: {}", username);

        // 1. Rechercher l'utilisateur (quelle que soit la source de données)
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("Identifiants invalides"));

        // 2. Vérifier le mot de passe
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Mot de passe incorrect pour: {}", username);
            throw new AuthenticationException("Identifiants invalides");
        }

        // 3. Vérifier que le compte est actif
        if (!user.isActive()) {
            throw new AuthenticationException("Compte désactivé. Contactez l'administrateur.");
        }

        // 4. Générer le token JWT
        String token = tokenGenerator.generateToken(user);
        log.info("Connexion réussie pour: {}", username);

        return new AuthResult(user, token, tokenGenerator.getExpirationTime());
    }

    @Override
    public User register(User user) {
        log.info("Enregistrement d'un nouvel utilisateur: {}", user.getUsername());

        // Vérifications métier
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new AuthenticationException("Ce nom d'utilisateur est déjà pris");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new AuthenticationException("Cet email est déjà utilisé");
        }

        // Hashage du mot de passe
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Rôle par défaut
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Set.of("USER"));
        }
        user.setActive(true);

        User saved = userRepository.save(user);
        log.info("Utilisateur enregistré avec succès: {}", saved.getId());
        return saved;
    }
}
