package com.ecommerce.infrastructure.adapter.persistence.mysql;

import com.ecommerce.domain.model.Order;
import com.ecommerce.domain.model.OrderItem;
import com.ecommerce.domain.port.output.OrderRepositoryPort;
import com.ecommerce.infrastructure.adapter.persistence.mysql.entity.*;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * ADAPTATEUR - Implémentation MySQL de OrderRepositoryPort.
 */
@RequiredArgsConstructor
public class MySQLOrderRepositoryAdapter implements OrderRepositoryPort {

    private final OrderJpaRepository jpaRepository;

    @Override
    public Optional<Order> findById(String orderId) {
        return jpaRepository.findById(orderId).map(this::toDomain);
    }

    @Override
    public List<Order> findByCustomerId(String customerId) {
        return jpaRepository.findByCustomerId(customerId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Order> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Order save(Order order) {
        if (order.getId() == null) order.setId(UUID.randomUUID().toString());
        return toDomain(jpaRepository.save(toEntity(order)));
    }

    @Override
    public void deleteById(String orderId) {
        jpaRepository.deleteById(orderId);
    }

    private Order toDomain(OrderJpaEntity e) {
        List<OrderItem> items = e.getItems() == null ? List.of() : e.getItems().stream()
                .map(i -> OrderItem.builder()
                        .productId(i.getProductId()).productName(i.getProductName())
                        .supplierId(i.getSupplierId()).quantity(i.getQuantity()).unitPrice(i.getUnitPrice())
                        .build())
                .collect(Collectors.toList());
        return Order.builder()
                .id(e.getId()).customerId(e.getCustomerId()).items(items)
                .status(e.getStatus()).createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt()).shippingAddress(e.getShippingAddress())
                .build();
    }

    private OrderJpaEntity toEntity(Order o) {
        List<OrderItemJpaEntity> items = o.getItems() == null ? List.of() : o.getItems().stream()
                .map(i -> OrderItemJpaEntity.builder()
                        .productId(i.getProductId()).productName(i.getProductName())
                        .supplierId(i.getSupplierId()).quantity(i.getQuantity()).unitPrice(i.getUnitPrice())
                        .build())
                .collect(Collectors.toList());
        return OrderJpaEntity.builder()
                .id(o.getId()).customerId(o.getCustomerId()).items(items)
                .status(o.getStatus()).createdAt(o.getCreatedAt())
                .updatedAt(o.getUpdatedAt()).shippingAddress(o.getShippingAddress())
                .build();
    }
}
