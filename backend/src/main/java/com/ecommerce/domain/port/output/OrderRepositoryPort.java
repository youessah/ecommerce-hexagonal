package com.ecommerce.domain.port.output;

import com.ecommerce.domain.model.Order;

import java.util.List;
import java.util.Optional;

/**
 * PORT DE SORTIE - Interface d'accès aux données commande.
 * Similaire à OrderRepositoryPort mentionné dans le PDF.
 */
public interface OrderRepositoryPort {

    Optional<Order> findById(String orderId);

    List<Order> findByCustomerId(String customerId);

    List<Order> findAll();

    Order save(Order order);

    void deleteById(String orderId);
}
