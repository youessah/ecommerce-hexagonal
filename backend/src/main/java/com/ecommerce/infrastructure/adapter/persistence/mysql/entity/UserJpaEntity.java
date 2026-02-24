package com.ecommerce.infrastructure.adapter.persistence.mysql.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * ADAPTATEUR PERSISTENCE - Entité JPA (MySQL).
 *
 * Cette classe contient les annotations JPA spécifiques à MySQL.
 * Elle est convertie en/depuis le modèle domaine User grâce à un Mapper.
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserJpaEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "username", unique = true, nullable = false, length = 100)
    private String username;

    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles;

    @Column(name = "active", nullable = false)
    private boolean active;
}
