package com.ecommerce.infrastructure.adapter.persistence.mysql.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository Spring Data JPA - Infrastructure uniquement.
 * N'est jamais exposé au domaine directement.
 */
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, String> {
    Optional<UserJpaEntity> findByUsername(String username);
    Optional<UserJpaEntity> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
