package com.ecommerce.domain.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String productId) {
        super("Produit introuvable : " + productId);
    }
}
