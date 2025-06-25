package src.controllers;

import java.util.UUID;

import src.models.*;

public class ProductController {
    private StockModel model;

    public ProductController(StockModel model) {
        this.model = model;
    }

    public void addProduct(String name, int quantity, float price) throws IllegalArgumentException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du produit ne peut pas être vide");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("La quantité ne peut pas être négative");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Le prix ne peut pas être négatif");
        }

        Product product = new Product(UUID.randomUUID().toString(), name.trim(), quantity, price);
        model.addProduct(product);
    }

    public void updateProduct(int index, String name, int quantity, float price) throws IllegalArgumentException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du produit ne peut pas être vide");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("La quantité ne peut pas être négative");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Le prix ne peut pas être négatif");
        }

        Product existingProduct = model.getProduct(index);
        if (existingProduct != null) {
            Product updatedProduct = new Product(existingProduct.id, name.trim(), quantity, price);
            model.updateProduct(index, updatedProduct);
        }
    }

    public void deleteProduct(int index) {
        model.deleteProduct(index);
    }
}
