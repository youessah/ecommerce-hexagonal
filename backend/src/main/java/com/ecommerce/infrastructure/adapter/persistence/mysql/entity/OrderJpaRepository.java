package com.ecommerce.infrastructure.adapter.persistence.mysql.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, String> {
    List<OrderJpaEntity> findByCustomerId(String customerId);
}
