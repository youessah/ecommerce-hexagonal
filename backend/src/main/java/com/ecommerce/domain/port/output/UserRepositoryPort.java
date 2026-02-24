package com.ecommerce.domain.port.output;

import com.ecommerce.domain.model.User;

import java.util.Optional;

/**
 * PORT DE SORTIE - Interface d'accès aux données utilisateur.
 *
 * Ce port définit ce que le cœur métier attend d'un système de persistance.
 * Les adaptateurs (MySQL, MongoDB, JSON) implémentent ce port.
 *
 * Pattern Strategy + Repository Pattern.
 */
public interface UserRepositoryPort {

    /**
     * Recherche un utilisateur par son nom d'utilisateur.
     * Implémenté selon la source (MySQL, MongoDB, JSON).
     */
    Optional<User> findByUsername(String username);

    /**
     * Recherche un utilisateur par son email.
     */
    Optional<User> findByEmail(String email);

    /**
     * Recherche un utilisateur par son ID.
     */
    Optional<User> findById(String id);

    /**
     * Sauvegarde ou met à jour un utilisateur.
     */
    User save(User user);

    /**
     * Vérifie l'existence d'un username.
     */
    boolean existsByUsername(String username);

    /**
     * Vérifie l'existence d'un email.
     */
    boolean existsByEmail(String email);
}
