package com.ecommerce.domain.service;

import com.ecommerce.domain.exception.ProductNotFoundException;
import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.port.input.ProductUseCase;
import com.ecommerce.domain.port.output.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * SERVICE MÉTIER - Logique de gestion des produits.
 *
 * Implémente ProductUseCase.
 * Aucune dépendance vers JPA, MongoDB ou autre infrastructure.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService implements ProductUseCase {

    private final ProductRepositoryPort productRepository;

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> getProductById(String productId) {
        return productRepository.findById(productId);
    }

    @Override
    public List<Product> getProductsBySupplier(String supplierId) {
        log.debug("Recherche des produits du fournisseur: {}", supplierId);
        return productRepository.findBySupplierId(supplierId);
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    @Override
    public Product createProduct(Product product) {
        log.info("Création d'un produit: {}", product.getName());
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(String productId, Product updatedProduct) {
        log.info("Mise à jour du produit: {}", productId);
        Product existing = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        existing.setName(updatedProduct.getName());
        existing.setDescription(updatedProduct.getDescription());
        existing.setPrice(updatedProduct.getPrice());
        existing.setStock(updatedProduct.getStock());
        existing.setCategory(updatedProduct.getCategory());
        existing.setAvailable(updatedProduct.isAvailable());

        return productRepository.save(existing);
    }

    @Override
    public void deleteProduct(String productId) {
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException(productId);
        }
        productRepository.deleteById(productId);
        log.info("Produit supprimé: {}", productId);
    }
}
