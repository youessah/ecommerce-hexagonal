package com.ecommerce.infrastructure.adapter.persistence.mysql.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, String> {
    List<ProductJpaEntity> findBySupplierId(String supplierId);
    List<ProductJpaEntity> findByCategory(String category);

    @Transactional
    @Modifying
    @Query("UPDATE ProductJpaEntity p SET p.stock = :stock WHERE p.id = :productId")
    void updateStock(String productId, int stock);
}
