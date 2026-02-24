package com.ecommerce.infrastructure.config;

import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.model.User;
import com.ecommerce.domain.port.input.AuthenticationUseCase;
import com.ecommerce.domain.port.output.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Initialisation des données de test au démarrage.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final AuthenticationUseCase authUseCase;
    private final ProductRepositoryPort productRepository;

    @Override
    public void run(String... args) {
        log.info("=== Initialisation des données de test ===");

        // Créer un admin
        try {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@ecommerce.com")
                    .password("Admin@123")
                    .roles(Set.of("ADMIN", "USER"))
                    .build();
            authUseCase.register(admin);
            log.info("Admin créé: admin / Admin@123");
        } catch (Exception e) {
            log.info("Admin déjà existant");
        }

        // Créer un user standard
        try {
            User user = User.builder()
                    .username("client1")
                    .email("client1@example.com")
                    .password("Client@123")
                    .roles(Set.of("USER"))
                    .build();
            authUseCase.register(user);
            log.info("Client créé: client1 / Client@123");
        } catch (Exception e) {
            log.info("Client déjà existant");
        }

        // Produits du fournisseur f1
        createProduct("Laptop Pro X1", "Ordinateur portable haute performance", new BigDecimal("1500000"), 50, "f1", "Electronique");
        createProduct("Souris Gaming RGB", "Souris gamer 16000 DPI", new BigDecimal("15000"), 200, "f1", "Accessoires");
        createProduct("Clavier Mécanique", "Clavier Cherry MX Red", new BigDecimal("5000"), 100, "f1", "Accessoires");

        // Produits du fournisseur f2
        createProduct("Smartphone Ultra S23", "Écran AMOLED 6.7 pouces", new BigDecimal("900000"), 75, "f2", "Electronique");
        createProduct("Casque Bluetooth Pro", "Réduction de bruit active", new BigDecimal("12000"), 150, "f2", "Audio");
        createProduct("Tablette Tab 10", "10 pouces, 128GB", new BigDecimal("450000"), 60, "f2", "Electronique");

        log.info("=== Données de test initialisées avec succès ===");
    }

    private void createProduct(String name, String desc, BigDecimal price, int stock, String supplier, String category) {
        try {
            productRepository.save(Product.builder()
                    .name(name).description(desc).price(price).stock(stock)
                    .supplierId(supplier).storeId("store-" + supplier).category(category)
                    .available(true)
                    .build());
        } catch (Exception e) {
            log.debug("Produit déjà existant: {}", name);
        }
    }
}
