package com.ecommerce.infrastructure.adapter.persistence.json;

import com.ecommerce.domain.model.User;
import com.ecommerce.domain.port.output.UserRepositoryPort;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ADAPTATEUR - Implémentation JSON (fichier local) de UserRepositoryPort.
 *
 * Lit les utilisateurs depuis un fichier JSON local (./data/users.json).
 * Activé quand auth.repository-type=json dans application.yml.
 *
 * Démontre que le cœur métier est totalement agnostique de la source de données.
 */
@Slf4j
public class JsonUserRepositoryAdapter implements UserRepositoryPort {

    private final String filePath;
    private final ObjectMapper objectMapper;

    public JsonUserRepositoryAdapter(String filePath) {
        this.filePath = filePath;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        log.debug("[JSON] Recherche utilisateur par username: {}", username);
        return loadUsers().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return loadUsers().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public Optional<User> findById(String id) {
        return loadUsers().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
    }

    @Override
    public User save(User user) {
        List<User> users = loadUsers();
        if (user.getId() == null) {
            user.setId(UUID.randomUUID().toString());
            users.add(user);
        } else {
            users = users.stream()
                    .map(u -> u.getId().equals(user.getId()) ? user : u)
                    .collect(Collectors.toList());
            if (users.stream().noneMatch(u -> u.getId().equals(user.getId()))) {
                users.add(user);
            }
        }
        saveUsers(users);
        return user;
    }

    @Override
    public boolean existsByUsername(String username) {
        return loadUsers().stream().anyMatch(u -> u.getUsername().equals(username));
    }

    @Override
    public boolean existsByEmail(String email) {
        return loadUsers().stream().anyMatch(u -> u.getEmail().equals(email));
    }

    private List<User> loadUsers() {
        File file = new File(filePath);
        if (!file.exists()) {
            log.info("[JSON] Fichier {} inexistant, création d'une liste vide", filePath);
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(file, new TypeReference<List<User>>() {});
        } catch (IOException e) {
            log.error("[JSON] Erreur lors de la lecture du fichier: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private void saveUsers(List<User> users) {
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, users);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde des utilisateurs en JSON", e);
        }
    }
}
