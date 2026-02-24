package com.ecommerce.infrastructure.config;

import com.ecommerce.domain.port.output.ProductRepositoryPort;
import com.ecommerce.domain.port.output.UserRepositoryPort;
import com.ecommerce.infrastructure.adapter.persistence.json.JsonUserRepositoryAdapter;
import com.ecommerce.infrastructure.adapter.persistence.mongo.MongoUserRepositoryAdapter;
import com.ecommerce.infrastructure.adapter.persistence.mongo.UserMongoRepository;
import com.ecommerce.infrastructure.adapter.persistence.mysql.MySQLOrderRepositoryAdapter;
import com.ecommerce.infrastructure.adapter.persistence.mysql.MySQLProductRepositoryAdapter;
import com.ecommerce.infrastructure.adapter.persistence.mysql.MySQLUserRepositoryAdapter;
import com.ecommerce.infrastructure.adapter.persistence.mysql.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * CONFIGURATION - Sélection dynamique des adaptateurs.
 *
 * Pattern FACTORY + STRATEGY :
 * Selon la valeur de auth.repository-type (mysql | mongo | json),
 * un UserRepositoryPort différent est injecté dans le domaine.
 *
 * Cela démontre la puissance de l'architecture hexagonale :
 * le domaine ne change pas, seule la configuration change.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class RepositoryConfig {

    @Value("${auth.repository-type:mysql}")
    private String authRepositoryType;

    @Value("${spring.json-repository.file-path:./data/users.json}")
    private String jsonFilePath;

    // JPA Repositories (Spring Data)
    private final UserJpaRepository userJpaRepository;
    private final ProductJpaRepository productJpaRepository;
    private final OrderJpaRepository orderJpaRepository;
    private final UserMongoRepository userMongoRepository;

    /**
     * Sélectionne dynamiquement le UserRepositoryPort selon la configuration.
     * Pattern Strategy.
     */
    @Bean
    public UserRepositoryPort userRepositoryPort() {
        log.info("=== Sélection du UserRepositoryPort: {} ===", authRepositoryType);
        return switch (authRepositoryType.toLowerCase()) {
            case "mongo" -> {
                log.info("→ Adaptateur MongoDB activé pour l'authentification");
                yield new MongoUserRepositoryAdapter(userMongoRepository);
            }
            case "json" -> {
                log.info("→ Adaptateur JSON activé pour l'authentification (fichier: {})", jsonFilePath);
                yield new JsonUserRepositoryAdapter(jsonFilePath);
            }
            default -> {
                log.info("→ Adaptateur MySQL/H2 activé pour l'authentification");
                yield new MySQLUserRepositoryAdapter(userJpaRepository);
            }
        };
    }

    /**
     * Repository de produits par défaut.
     */
    @Bean
    public ProductRepositoryPort defaultProductRepository() {
        return new MySQLProductRepositoryAdapter(productJpaRepository, "default");
    }

    /**
     * Repositories par fournisseur (Strategy routing).
     *
     * Dans un vrai projet, chaque fournisseur aurait sa propre DataSource configurée.
     * Ici on simule avec la même base pour les fournisseurs f1 et f2.
     * En production: f1 → datasource1, f2 → datasource2, etc.
     */
    @Bean
    public Map<String, ProductRepositoryPort> supplierRepositories() {
        Map<String, ProductRepositoryPort> repos = new HashMap<>();

        // Fournisseur f1 : utilise MySQL (même DB pour la démo)
        repos.put("f1", new MySQLProductRepositoryAdapter(productJpaRepository, "f1"));

        // Fournisseur f2 : utilise MySQL (dans un vrai contexte, ce serait une autre datasource)
        repos.put("f2", new MySQLProductRepositoryAdapter(productJpaRepository, "f2"));

        log.info("=== SupplierRepositoryRouter configuré avec {} fournisseurs ===", repos.size());
        return repos;
    }

    /**
     * Repository de commandes.
     */
    @Bean
    public MySQLOrderRepositoryAdapter orderRepositoryAdapter() {
        return new MySQLOrderRepositoryAdapter(orderJpaRepository);
    }
}
