package com.ecommerce.infrastructure.adapter;

import com.ecommerce.domain.model.User;
import com.ecommerce.domain.port.output.TokenGeneratorPort;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * ADAPTATEUR - Génération JWT avec la bibliothèque JJWT.
 *
 * Implémente TokenGeneratorPort pour que le domaine ne dépende pas de JJWT.
 */
@Component
@Slf4j
public class JwtTokenAdapter implements TokenGeneratorPort {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationMs;

    @Override
    public String generateToken(User user) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", user.getRoles())
                .claim("userId", user.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    @Override
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Token JWT invalide: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public long getExpirationTime() {
        return expirationMs;
    }

    private Claims parseClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}
