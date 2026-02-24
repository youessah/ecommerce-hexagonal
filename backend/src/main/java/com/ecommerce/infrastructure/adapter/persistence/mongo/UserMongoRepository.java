package com.ecommerce.infrastructure.adapter.persistence.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Spring Data MongoDB Repository - Infrastructure uniquement.
 */
public interface UserMongoRepository extends MongoRepository<UserMongoDocument, String> {
    Optional<UserMongoDocument> findByUsername(String username);
    Optional<UserMongoDocument> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
