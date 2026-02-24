package com.ecommerce.infrastructure.adapter.persistence.mysql.entity;

import com.ecommerce.domain.model.Order;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderJpaEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "customer_id", nullable = false, length = 36)
    private String customerId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private List<OrderItemJpaEntity> items;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Order.OrderStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "shipping_address", length = 500)
    private String shippingAddress;
}
