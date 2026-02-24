package com.ecommerce.domain.port.output;

import com.ecommerce.domain.model.User;

/**
 * PORT DE SORTIE - Génération et validation de tokens JWT.
 * Abstraction pour ne pas coupler le domaine à une bibliothèque JWT spécifique.
 */
public interface TokenGeneratorPort {
    String generateToken(User user);
    String extractUsername(String token);
    boolean validateToken(String token);
    long getExpirationTime();
}
