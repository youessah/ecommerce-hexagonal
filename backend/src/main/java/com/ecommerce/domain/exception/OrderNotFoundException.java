package com.ecommerce.domain.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String orderId) {
        super("Commande introuvable : " + orderId);
    }
}
