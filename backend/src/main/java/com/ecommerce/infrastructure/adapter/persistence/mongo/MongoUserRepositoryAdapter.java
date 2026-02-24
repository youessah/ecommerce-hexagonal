package com.ecommerce.infrastructure.adapter.persistence.mongo;

import com.ecommerce.domain.model.User;
import com.ecommerce.domain.port.output.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;

/**
 * ADAPTATEUR - Implémentation MongoDB de UserRepositoryPort.
 *
 * Activé quand auth.repository-type=mongo dans application.yml.
 * La même interface du domaine, une implémentation différente.
 */
@RequiredArgsConstructor
@Slf4j
public class MongoUserRepositoryAdapter implements UserRepositoryPort {

    private final UserMongoRepository mongoRepository;

    @Override
    public Optional<User> findByUsername(String username) {
        log.debug("[MongoDB] Recherche utilisateur par username: {}", username);
        return mongoRepository.findByUsername(username).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return mongoRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public Optional<User> findById(String id) {
        return mongoRepository.findById(id).map(this::toDomain);
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(UUID.randomUUID().toString());
        }
        UserMongoDocument saved = mongoRepository.save(toDocument(user));
        return toDomain(saved);
    }

    @Override
    public boolean existsByUsername(String username) {
        return mongoRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return mongoRepository.existsByEmail(email);
    }

    private User toDomain(UserMongoDocument doc) {
        return User.builder()
                .id(doc.getId())
                .username(doc.getUsername())
                .email(doc.getEmail())
                .password(doc.getPassword())
                .roles(doc.getRoles())
                .active(doc.isActive())
                .build();
    }

    private UserMongoDocument toDocument(User user) {
        return UserMongoDocument.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRoles())
                .active(user.isActive())
                .build();
    }
}
