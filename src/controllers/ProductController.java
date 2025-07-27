package src.controllers;

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
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import src.models.*;
import src.views.*;

public class ProductController {

    private Product model;
    private ProductView view;

    private final String filename = "data/products.json";

    private List<Product> products = loadProducts();

    private Consumer<List<Product>> onProductChange = null;

    private void saveAllProducts() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(products, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void subscribeToProductChange(Consumer<List<Product>> con) {
        this.onProductChange = con;
    }

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

    private void callSubscribes() {
        if (this.onProductChange != null) {
            this.onProductChange.accept(products);
        }
    }

    public Product getProduct(int index) {
        return index >= 0 && index < products.size() ? products.get(index) : null;
    }

    public Product getProductById(String id) {
        List<Product> products = loadProducts();
        for (Product product : products) {
            if (product.getId().equals(id)) {
                return product;
            }
        }
        return null;
    }

    public void updateProductList(int index, Product product) {
        if (index >= 0 && index < products.size()) {
            products.set(index, product);
            this.saveAllProducts();
        }
    }

    public void addProduct(String id, String name, int quantity, float price) {
        Product newProduct = new Product(id, name, quantity, price);

        products.add(newProduct);

        this.saveAllProducts();

        this.callSubscribes();
    }

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
