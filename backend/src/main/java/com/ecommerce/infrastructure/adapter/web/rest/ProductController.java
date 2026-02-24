package com.ecommerce.infrastructure.adapter.web.rest;

import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.port.input.ProductUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * ADAPTATEUR WEB - API REST pour les produits.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Produits", description = "Gestion des produits du catalogue")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductController {

    private final ProductUseCase productUseCase;

    @GetMapping
    @Operation(summary = "Liste tous les produits disponibles")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productUseCase.getAllProducts());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupère un produit par son ID")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        return productUseCase.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/supplier/{supplierId}")
    @Operation(summary = "Produits d'un fournisseur spécifique")
    public ResponseEntity<List<Product>> getBySupplier(@PathVariable String supplierId) {
        return ResponseEntity.ok(productUseCase.getProductsBySupplier(supplierId));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Produits par catégorie")
    public ResponseEntity<List<Product>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(productUseCase.getProductsByCategory(category));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crée un nouveau produit (Admin)")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequest request) {
        Product product = Product.builder()
                .name(request.name()).description(request.description())
                .price(request.price()).stock(request.stock())
                .supplierId(request.supplierId()).storeId(request.storeId())
                .category(request.category()).available(true)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(productUseCase.createProduct(product));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Met à jour un produit (Admin)")
    public ResponseEntity<Product> updateProduct(@PathVariable String id, @Valid @RequestBody ProductRequest request) {
        Product product = Product.builder()
                .name(request.name()).description(request.description())
                .price(request.price()).stock(request.stock())
                .supplierId(request.supplierId()).storeId(request.storeId())
                .category(request.category()).available(request.available())
                .build();
        return ResponseEntity.ok(productUseCase.updateProduct(id, product));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprime un produit (Admin)")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productUseCase.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    public record ProductRequest(
            @NotBlank String name,
            String description,
            @NotNull BigDecimal price,
            int stock,
            @NotBlank String supplierId,
            String storeId,
            String category,
            boolean available
    ) {}
}
