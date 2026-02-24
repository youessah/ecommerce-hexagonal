package com.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée de l'application e-commerce hexagonale.
 *
 * Architecture Hexagonale (Ports & Adapters) :
 * ┌─────────────────────────────────────────────────────┐
 * │                   ADAPTATEURS                        │
 * │  REST API / Angular UI  ←──→  CŒUR MÉTIER  ←──→  DB│
 * │                         Ports d'entrée/sortie        │
 * └─────────────────────────────────────────────────────┘
 */
@SpringBootApplication
public class EcommerceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
    }
}
