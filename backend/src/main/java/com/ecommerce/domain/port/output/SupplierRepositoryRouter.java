package com.ecommerce.domain.port.output;

/**
 * PORT DE SORTIE - Routeur vers la base de données du fournisseur.
 *
 * Pattern STRATEGY : sélectionne dynamiquement le bon dépôt de produits
 * selon le fournisseur. Si le produit vendu est celui du fournisseur f1,
 * c'est la base de données associée qui sera mise à jour.
 */
public interface SupplierRepositoryRouter {

    /**
     * Retourne le bon ProductRepositoryPort selon l'ID du fournisseur.
     *
     * @param supplierId l'identifiant du fournisseur (ex: "f1", "f2")
     * @return le port de dépôt adapté à ce fournisseur
     */
    ProductRepositoryPort getRepositoryForSupplier(String supplierId);
}
