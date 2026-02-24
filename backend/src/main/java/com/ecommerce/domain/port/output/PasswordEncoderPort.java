package com.ecommerce.domain.port.output;

/**
 * PORT DE SORTIE - Abstraction pour l'encodage des mots de passe.
 * Permet de ne pas coupler le domaine à BCrypt ou autre implémentation.
 */
public interface PasswordEncoderPort {
    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
