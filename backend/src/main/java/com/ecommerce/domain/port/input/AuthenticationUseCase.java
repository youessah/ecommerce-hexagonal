package com.ecommerce.domain.port.input;

import com.ecommerce.domain.model.User;

/**
 * PORT D'ENTRÉE - Cas d'usage d'authentification.
 *
 * Définit les actions disponibles pour l'authentification.
 * Indépendant de toute implémentation technique.
 * Pattern : Use Case / Command
 */
public interface AuthenticationUseCase {

    /**
     * Authentifie un utilisateur avec son identifiant et mot de passe.
     * La source des données (MySQL, MongoDB, JSON) est transparente pour ce port.
     *
     * @param username nom d'utilisateur
     * @param password mot de passe en clair
     * @return l'utilisateur authentifié avec son token JWT
     * @throws com.ecommerce.domain.exception.AuthenticationException si les credentials sont invalides
     */
    AuthResult login(String username, String password);

    /**
     * Enregistre un nouvel utilisateur.
     *
     * @param user l'utilisateur à créer
     * @return l'utilisateur créé
     */
    User register(User user);

    /**
     * Résultat de l'authentification
     */
    record AuthResult(User user, String token, long expiresIn) {}
}
