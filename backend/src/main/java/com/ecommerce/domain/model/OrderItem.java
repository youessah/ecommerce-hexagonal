package com.ecommerce.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * CŒUR MÉTIER - Ligne de commande.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private String productId;
    private String productName;
    private String supplierId;  // Permet de router vers la bonne base de données du fournisseur
    private int quantity;
    private BigDecimal unitPrice;
}
