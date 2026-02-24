package com.ecommerce.infrastructure.adapter.persistence.mysql;

import com.ecommerce.domain.model.User;
import com.ecommerce.domain.port.output.UserRepositoryPort;
import com.ecommerce.infrastructure.adapter.persistence.mysql.entity.UserJpaEntity;
import com.ecommerce.infrastructure.adapter.persistence.mysql.entity.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;

/**
 * ADAPTATEUR - Implémentation MySQL de UserRepositoryPort.
 *
 * Traduit entre les objets de domaine et les entités JPA.
 * Registré comme bean Spring via la configuration selon la valeur de auth.repository-type=mysql.
 */
@RequiredArgsConstructor
@Slf4j
public class MySQLUserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpaRepository;

    @Override
    public Optional<User> findByUsername(String username) {
        log.debug("[MySQL] Recherche utilisateur par username: {}", username);
        return jpaRepository.findByUsername(username).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public Optional<User> findById(String id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(UUID.randomUUID().toString());
        }
        UserJpaEntity saved = jpaRepository.save(toEntity(user));
        return toDomain(saved);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    // --- Mappers domain <-> JPA ---

    private User toDomain(UserJpaEntity entity) {
        return User.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .roles(entity.getRoles())
                .active(entity.isActive())
                .build();
    }

    private UserJpaEntity toEntity(User user) {
        return UserJpaEntity.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRoles())
                .active(user.isActive())
                .build();
    }
}
