package com.ecommerce.domain.port.input;

import com.ecommerce.domain.model.Order;
import com.ecommerce.domain.model.OrderItem;

import java.util.List;
import java.util.Optional;

/**
 * PORT D'ENTRÉE - Cas d'usage commande.
 *
 * PasserCommandeUseCase : logique de vente avec mise à jour
 * des stocks dans la base du fournisseur concerné.
 */
public interface OrderUseCase {

    /**
     * Passe une commande pour un client.
     * La logique métier :
     * 1. Vérifie le stock pour chaque article
     * 2. Pour chaque article, met à jour la BDD du fournisseur associé
     * 3. Crée la commande
     * 4. Retourne la commande confirmée
     *
     * @param customerId  ID du client
     * @param items       liste des articles commandés
     * @param shippingAddress adresse de livraison
     * @return la commande créée et confirmée
     */
    Order placeOrder(String customerId, List<OrderItem> items, String shippingAddress);

    /**
     * Récupère une commande par son ID.
     */
    Optional<Order> getOrderById(String orderId);

    /**
     * Récupère toutes les commandes d'un client.
     */
    List<Order> getOrdersByCustomer(String customerId);

    /**
     * Annule une commande.
     */
    Order cancelOrder(String orderId);

    /**
     * Met à jour le statut d'une commande (Admin).
     */
    Order updateOrderStatus(String orderId, Order.OrderStatus newStatus);
}
