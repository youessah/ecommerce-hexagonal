package com.ecommerce.infrastructure.adapter.persistence.mysql.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductJpaEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "stock", nullable = false)
    private int stock;

    @Column(name = "supplier_id", nullable = false, length = 50)
    private String supplierId;

    @Column(name = "store_id", length = 50)
    private String storeId;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "available", nullable = false)
    private boolean available;
}
