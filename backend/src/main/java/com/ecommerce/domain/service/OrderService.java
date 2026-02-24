package com.ecommerce.domain.service;

import com.ecommerce.domain.exception.OrderNotFoundException;
import com.ecommerce.domain.exception.ProductNotFoundException;
import com.ecommerce.domain.model.Order;
import com.ecommerce.domain.model.OrderItem;
import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.port.input.OrderUseCase;
import com.ecommerce.domain.port.output.OrderRepositoryPort;
import com.ecommerce.domain.port.output.ProductRepositoryPort;
import com.ecommerce.domain.port.output.SupplierRepositoryRouter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * SERVICE MÉTIER - Logique de commande et vente.
 *
 * Cas particulier clé : le routage vers la bonne base de données fournisseur.
 * Quand on vend un produit du fournisseur f1, c'est la base f1 qui est mise à jour.
 *
 * Pattern STRATEGY via SupplierRepositoryRouter.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService implements OrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final ProductRepositoryPort defaultProductRepository;  // Pour consultation globale
    private final SupplierRepositoryRouter supplierRouter;         // Pour mise à jour ciblée par fournisseur

    @Override
    public Order placeOrder(String customerId, List<OrderItem> items, String shippingAddress) {
        log.info("Passage de commande pour le client: {} avec {} articles", customerId, items.size());

        // 1. Vérifier le stock pour chaque article et enrichir les données
        for (OrderItem item : items) {
            // Récupérer la base du fournisseur via le routeur (Strategy Pattern)
            ProductRepositoryPort supplierRepo = supplierRouter.getRepositoryForSupplier(item.getSupplierId());

            Product product = supplierRepo.findById(item.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(item.getProductId()));

            if (!product.canSell(item.getQuantity())) {
                throw new IllegalStateException(
                    "Stock insuffisant pour le produit " + product.getName()
                    + " (fournisseur: " + item.getSupplierId() + ")"
                );
            }

            // Enrichir l'item avec le prix actuel
            item.setUnitPrice(product.getPrice());
            item.setProductName(product.getName());
        }

        // 2. Créer la commande dans l'état PENDING
        Order order = Order.builder()
                .id(UUID.randomUUID().toString())
                .customerId(customerId)
                .items(items)
                .status(Order.OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .shippingAddress(shippingAddress)
                .build();

        // 3. Mettre à jour les stocks dans LA BASE DU FOURNISSEUR CONCERNÉ
        for (OrderItem item : items) {
            ProductRepositoryPort supplierRepo = supplierRouter.getRepositoryForSupplier(item.getSupplierId());
            Product product = supplierRepo.findById(item.getProductId()).get();
            product.decreaseStock(item.getQuantity());
            // Mise à jour dans la bonne base de données fournisseur
            supplierRepo.updateStock(product.getId(), product.getStock());
            log.info("Stock mis à jour - Fournisseur: {}, Produit: {}, Nouveau stock: {}",
                    item.getSupplierId(), item.getProductId(), product.getStock());
        }

        // 4. Confirmer la commande
        order.confirm();

        // 5. Sauvegarder et retourner
        Order savedOrder = orderRepository.save(order);
        log.info("Commande créée avec succès: {}", savedOrder.getId());
        return savedOrder;
    }

    @Override
    public Optional<Order> getOrderById(String orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public List<Order> getOrdersByCustomer(String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @Override
    public Order cancelOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.cancel();
        return orderRepository.save(order);
    }

    @Override
    public Order updateOrderStatus(String orderId, Order.OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }
}
