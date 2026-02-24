package com.ecommerce.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * CŒUR MÉTIER - Entité Order (Commande).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private String id;
    private String customerId;
    private List<OrderItem> items;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String shippingAddress;

    public enum OrderStatus {
        PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    }

    /**
     * Règle métier : calculer le total de la commande
     */
    public BigDecimal calculateTotal() {
        if (items == null || items.isEmpty()) return BigDecimal.ZERO;
        return items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Règle métier : confirmer une commande
     */
    public void confirm() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("Seules les commandes en attente peuvent être confirmées");
        }
        this.status = OrderStatus.CONFIRMED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Règle métier : annuler une commande
     */
    public void cancel() {
        if (this.status == OrderStatus.SHIPPED || this.status == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Impossible d'annuler une commande déjà expédiée ou livrée");
        }
        this.status = OrderStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }
}
