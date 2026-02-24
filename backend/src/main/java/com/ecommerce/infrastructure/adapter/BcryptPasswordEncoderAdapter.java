package com.ecommerce.infrastructure.adapter;

import com.ecommerce.domain.port.output.PasswordEncoderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * ADAPTATEUR - Encodage BCrypt via Spring Security.
 *
 * Implémente PasswordEncoderPort pour que le domaine ne dépende pas de Spring Security.
 */
@Component
@RequiredArgsConstructor
public class BcryptPasswordEncoderAdapter implements PasswordEncoderPort {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
