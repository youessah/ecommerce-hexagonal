package com.ecommerce.infrastructure.adapter.persistence;

import com.ecommerce.domain.port.output.ProductRepositoryPort;
import com.ecommerce.domain.port.output.SupplierRepositoryRouter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * ADAPTATEUR - Implémentation du routeur vers les bases de données fournisseurs.
 *
 * Pattern STRATEGY : sélectionne dynamiquement le bon ProductRepositoryPort
 * selon l'identifiant du fournisseur.
 *
 * Exemple :
 * - "f1" → base MySQL du fournisseur 1
 * - "f2" → base MongoDB du fournisseur 2
 * - "default" → base par défaut
 *
 * La configuration est injectée via la classe RepositoryConfig.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SupplierRepositoryRouterImpl implements SupplierRepositoryRouter {

    /**
     * Map des repositories par fournisseur.
     * Clé : supplierId (ex: "f1", "f2")
     * Valeur : implémentation du port ProductRepositoryPort
     *
     * Cette map est construite dans RepositoryConfig via Spring.
     */
    private final Map<String, ProductRepositoryPort> supplierRepositories;

    /**
     * Repository par défaut si le fournisseur n'est pas trouvé dans la map.
     */
    private final ProductRepositoryPort defaultProductRepository;

    @Override
    public ProductRepositoryPort getRepositoryForSupplier(String supplierId) {
        ProductRepositoryPort repo = supplierRepositories.get(supplierId);
        if (repo != null) {
            log.debug("Routing vers le repository du fournisseur: {}", supplierId);
            return repo;
        }
        log.warn("Fournisseur '{}' non trouvé, utilisation du repository par défaut", supplierId);
        return defaultProductRepository;
    }
}
