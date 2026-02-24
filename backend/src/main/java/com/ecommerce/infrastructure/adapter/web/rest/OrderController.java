package com.ecommerce.infrastructure.adapter.web.rest;

import com.ecommerce.domain.model.Order;
import com.ecommerce.domain.model.OrderItem;
import com.ecommerce.domain.port.input.OrderUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ADAPTATEUR WEB - API REST pour les commandes.
 *
 * Démontre le passage de commande avec routing vers la bonne BDD fournisseur.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Commandes", description = "Gestion des commandes")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "http://localhost:4200")
public class OrderController {

    private final OrderUseCase orderUseCase;

    @PostMapping
    @Operation(summary = "Passe une nouvelle commande",
               description = "Vérifie les stocks, met à jour la BDD du fournisseur concerné et crée la commande")
    public ResponseEntity<Order> placeOrder(@Valid @RequestBody PlaceOrderRequest request,
                                             Authentication authentication) {
        String customerId = authentication.getName(); // username comme ID client

        List<OrderItem> items = request.items().stream()
                .map(i -> OrderItem.builder()
                        .productId(i.productId())
                        .supplierId(i.supplierId())
                        .quantity(i.quantity())
                        .build())
                .toList();

        Order order = orderUseCase.placeOrder(customerId, items, request.shippingAddress());
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupère une commande par son ID")
    public ResponseEntity<Order> getOrder(@PathVariable String id) {
        return orderUseCase.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/my-orders")
    @Operation(summary = "Commandes de l'utilisateur connecté")
    public ResponseEntity<List<Order>> getMyOrders(Authentication authentication) {
        return ResponseEntity.ok(orderUseCase.getOrdersByCustomer(authentication.getName()));
    }

    @DeleteMapping("/{id}/cancel")
    @Operation(summary = "Annule une commande")
    public ResponseEntity<Order> cancelOrder(@PathVariable String id) {
        return ResponseEntity.ok(orderUseCase.cancelOrder(id));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Met à jour le statut d'une commande (Admin)")
    public ResponseEntity<Order> updateStatus(@PathVariable String id,
                                               @RequestParam Order.OrderStatus status) {
        return ResponseEntity.ok(orderUseCase.updateOrderStatus(id, status));
    }

    // DTOs
    public record PlaceOrderRequest(
            @NotEmpty List<OrderItemRequest> items,
            @NotBlank String shippingAddress
    ) {}

    public record OrderItemRequest(
            @NotBlank String productId,
            @NotBlank String supplierId,
            int quantity
    ) {}
}
