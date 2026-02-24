package com.ecommerce.domain.port.output;

import com.ecommerce.domain.model.Product;

import java.util.List;
import java.util.Optional;

/**
 * PORT DE SORTIE - Interface d'accès aux données produit.
 *
 * Le routing vers la bonne base de données fournisseur se fait
 * via le SupplierRepositoryRouter (pattern Strategy).
 */
public interface ProductRepositoryPort {

    Optional<Product> findById(String productId);

    List<Product> findAll();

    List<Product> findBySupplierId(String supplierId);

    List<Product> findByCategory(String category);

    Product save(Product product);

    void deleteById(String productId);

    boolean existsById(String productId);

    /**
     * Met à jour uniquement le stock d'un produit.
     * Appelé après une vente, sur la base du fournisseur correspondant.
     */
    void updateStock(String productId, int newStock);
}
