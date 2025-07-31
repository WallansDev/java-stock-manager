package controllers;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JOptionPane;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import models.*;
import views.*;

/**
 * Contrôleur permettant de gérer les produits : chargement, sauvegarde, ajout,
 * modification,
 * suppression, abonnement aux changements et accès aux produits par index ou
 * identifiant.
 */
public class ProductController {

    /**
     * Vue associée au contrôleur produit.
     */
    private ProductView view;

    /**
     * Chemin du fichier JSON de stockage des produits.
     */
    private final String filename = "data/products.json";

    /**
     * Liste des produits en mémoire.
     */
    private List<Product> products = loadProducts();

    /**
     * Callback appelé lors d'un changement sur la liste des produits.
     */
    private Consumer<List<Product>> onProductChange = null;

    /**
     * Sauvegarde tous les produits dans le fichier JSON.
     */
    private void saveAllProducts() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(products, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Permet de s'abonner aux changements de la liste des produits.
     *
     * @param con Consommateur appelé lors d'un changement
     */
    public void subscribeToProductChange(Consumer<List<Product>> con) {
        this.onProductChange = con;
    }

    /**
     * Charge la liste des produits depuis le fichier JSON.
     *
     * @return Liste des produits chargés
     */
    public List<Product> loadProducts() {
        Gson gson = new Gson();
        File file = new File(filename);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<Product>>() {
            }.getType();
            List<Product> list = gson.fromJson(reader, listType);
            this.products = list;

            this.callSubscribes();

            return list;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Appelle le callback d'abonnement s'il existe.
     */
    private void callSubscribes() {
        if (this.onProductChange != null) {
            this.onProductChange.accept(products);
        }
    }

    /**
     * Retourne le produit à l'index donné dans la liste.
     *
     * @param index Index du produit
     * @return Produit correspondant ou null si l'index est invalide
     */
    public Product getProduct(int index) {
        return index >= 0 && index < products.size() ? products.get(index) : null;
    }

    /**
     * Retourne le produit correspondant à l'identifiant donné.
     *
     * @param id Identifiant du produit
     * @return Produit correspondant ou null si non trouvé
     */
    public Product getProductById(String id) {
        List<Product> products = loadProducts();
        for (Product product : products) {
            if (product.getId().equals(id)) {
                return product;
            }
        }
        return null;
    }

    /**
     * Met à jour le produit à l'index donné dans la liste.
     *
     * @param index   Index du produit à mettre à jour
     * @param product Nouveau produit à placer à cet index
     */
    public void updateProductList(int index, Product product) {
        if (index >= 0 && index < products.size()) {
            products.set(index, product);
            this.saveAllProducts();
        }
    }

    /**
     * Ajoute un nouveau produit à la liste et sauvegarde.
     *
     * @param id       Identifiant du produit
     * @param name     Nom du produit
     * @param quantity Quantité en stock
     * @param price    Prix du produit
     */
    public void addProduct(String id, String name, int quantity, float price) {
        Product newProduct = new Product(id, name, quantity, price);

        products.add(newProduct);

        this.saveAllProducts();

        this.callSubscribes();
    }

    /**
     * Met à jour les informations d'un produit à l'index donné.
     *
     * @param index    Index du produit à mettre à jour
     * @param name     Nouveau nom
     * @param quantity Nouvelle quantité
     * @param price    Nouveau prix
     */
    public void updateProduct(int index, String name, int quantity, double price) {
        if (index >= 0 && index < products.size()) {

            System.out.println("Modification du produit");

            Product existingProduct = this.getProduct(index);
            if (existingProduct != null) {
                Product updatedProduct = new Product(existingProduct.id, name, quantity, price);
                this.updateProductList(index, updatedProduct);
                this.callSubscribes();
            }
        } else {
            JOptionPane.showMessageDialog(view,
                    "Choisir un produit à modifier.", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Supprime un produit à l'index donné de la liste et sauvegarde.
     *
     * @param index Index du produit à supprimer
     */
    public void deleteProduct(int index) {
        if (index >= 0 && index < products.size()) {
            products.remove(index);
            saveAllProducts();
            this.callSubscribes();

            System.out.println("Suppression du produit.");

        } else {
            JOptionPane.showMessageDialog(view,
                    "Choisir un produit à supprimer.", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

}
