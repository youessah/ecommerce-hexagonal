package com.ecommerce.domain.port.input;

import com.ecommerce.domain.model.Product;

import java.util.List;
import java.util.Optional;

/**
 * PORT D'ENTRÉE - Cas d'usage produit.
 *
 * Définit toutes les opérations métier sur les produits.
 */
public interface ProductUseCase {

    /**
     * Récupère tous les produits disponibles.
     */
    List<Product> getAllProducts();

    /**
     * Récupère un produit par son ID.
     */
    Optional<Product> getProductById(String productId);

    /**
     * Récupère les produits d'un fournisseur spécifique.
     * Chaque fournisseur a sa propre base de données.
     */
    List<Product> getProductsBySupplier(String supplierId);

    /**
     * Recherche des produits par catégorie.
     */
    List<Product> getProductsByCategory(String category);

    /**
     * Crée un nouveau produit (Admin seulement).
     */
    Product createProduct(Product product);

    /**
     * Met à jour un produit existant.
     */
    Product updateProduct(String productId, Product product);

    /**
     * Supprime un produit.
     */
    void deleteProduct(String productId);
}
