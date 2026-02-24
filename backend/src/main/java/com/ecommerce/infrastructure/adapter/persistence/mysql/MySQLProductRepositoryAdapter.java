package com.ecommerce.infrastructure.adapter.persistence.mysql;

import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.port.output.ProductRepositoryPort;
import com.ecommerce.infrastructure.adapter.persistence.mysql.entity.ProductJpaEntity;
import com.ecommerce.infrastructure.adapter.persistence.mysql.entity.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * ADAPTATEUR - Implémentation MySQL de ProductRepositoryPort.
 * Plusieurs instances peuvent coexister (une par fournisseur avec des datasources différentes).
 */
@RequiredArgsConstructor
@Slf4j
public class MySQLProductRepositoryAdapter implements ProductRepositoryPort {

    private final ProductJpaRepository jpaRepository;
    private final String supplierId; // Identifiant du fournisseur associé à cette base

    @Override
    public Optional<Product> findById(String productId) {
        return jpaRepository.findById(productId).map(this::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Product> findBySupplierId(String supplierId) {
        return jpaRepository.findBySupplierId(supplierId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Product> findByCategory(String category) {
        return jpaRepository.findByCategory(category).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(UUID.randomUUID().toString());
        }
        return toDomain(jpaRepository.save(toEntity(product)));
    }

    @Override
    public void deleteById(String productId) {
        jpaRepository.deleteById(productId);
    }

    @Override
    public boolean existsById(String productId) {
        return jpaRepository.existsById(productId);
    }

    @Override
    public void updateStock(String productId, int newStock) {
        log.info("[MySQL-{}] Mise à jour du stock: produit={}, newStock={}", supplierId, productId, newStock);
        jpaRepository.updateStock(productId, newStock);
    }

    private Product toDomain(ProductJpaEntity e) {
        return Product.builder()
                .id(e.getId()).name(e.getName()).description(e.getDescription())
                .price(e.getPrice()).stock(e.getStock()).supplierId(e.getSupplierId())
                .storeId(e.getStoreId()).category(e.getCategory()).available(e.isAvailable())
                .build();
    }

    private ProductJpaEntity toEntity(Product p) {
        return ProductJpaEntity.builder()
                .id(p.getId()).name(p.getName()).description(p.getDescription())
                .price(p.getPrice()).stock(p.getStock()).supplierId(p.getSupplierId())
                .storeId(p.getStoreId()).category(p.getCategory()).available(p.isAvailable())
                .build();
    }
}
