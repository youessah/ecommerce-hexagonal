package com.ecommerce.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * CŒUR MÉTIER - Entité User.
 * Aucune dépendance technique (pas d'annotations JPA/Mongo ici).
 * C'est un objet métier pur.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id;
    private String username;
    private String email;
    private String password; // hashé
    private Set<String> roles;
    private boolean active;

    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    public boolean isAdmin() {
        return hasRole("ADMIN");
    }
}
