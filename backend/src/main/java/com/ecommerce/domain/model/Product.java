package com.ecommerce.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * CŒUR MÉTIER - Entité Product.
 * Représente un produit d'un fournisseur dans un magasin.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private int stock;
    private String supplierId;   // Identifiant du fournisseur (f1, f2, ...)
    private String storeId;      // Identifiant du magasin
    private String category;
    private boolean available;

    /**
     * Règle métier : vérifier si on peut vendre la quantité demandée
     */
    public boolean canSell(int quantity) {
        return available && stock >= quantity;
    }

    /**
     * Règle métier : réduire le stock après une vente
     */
    public void decreaseStock(int quantity) {
        if (!canSell(quantity)) {
            throw new IllegalStateException(
                "Stock insuffisant pour le produit: " + name + " (disponible: " + stock + ", demandé: " + quantity + ")"
            );
        }
        this.stock -= quantity;
        if (this.stock == 0) {
            this.available = false;
        }
    }
}
