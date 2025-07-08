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

public class ClientController {

    private Client model;
    private ClientView view;

    private final String filename = "data/clients.json";

    private List<Client> clients = loadClients();

    private Consumer<List<Client>> onClientChange = null;

    private void saveAllClients() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(clients, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void subscribeToClienChange(Consumer<List<Client>> con) {
        this.onClientChange = con;
    }

    public List<Client> loadClients() {
        Gson gson = new Gson();
        File file = new File(filename);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<Client>>() {
            }.getType();
            List<Client> list = gson.fromJson(reader, listType);
            this.clients = list;

            this.callSubscribes();

            return list;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void callSubscribes() {
        if (this.onClientChange != null) {
            this.onClientChange.accept(clients);
        }
    }

    public Client getClient(int index) {
        return index >= 0 && index < clients.size() ? clients.get(index) : null;
    }


    public void addClient(String id, String name, String email, String address, String phone) {
        Client newClient = new Client(id, name, email, address, phone);

        clients.add(newClient);

        this.saveAllClients();

        this.callSubscribes();
    }

    // public void updateProduct(int index, String name, int quantity, double price) {
    //     if (index >= 0 && index < products.size()) {
    //         // Product product = products.get(index);

    //         System.out.println("Modification du produit");
            
    //         Product existingProduct = this.getProduct(index);
    //     if (existingProduct != null) {
    //         Product updatedProduct = new Product(existingProduct.id, name, quantity, price);
    //         this.updateProductList(index, updatedProduct);
    //         this.callSubscribes();
    //     }
    //     } else {
    //         JOptionPane.showMessageDialog(view,
    //                 "Choisir un produit à modifier.", "Erreur",
    //                 JOptionPane.ERROR_MESSAGE);
    //     }
    // }

    // public void deleteProduct(int index) {
    //     if (index >= 0 && index < products.size()) {
    //         products.remove(index);
    //         saveAllProducts();
    //         this.callSubscribes();

    //         System.out.println("Suppression du produit.");

    //     } else {
    //         JOptionPane.showMessageDialog(view,
    //                 "Choisir un produit à supprimer.", "Erreur",
    //                 JOptionPane.ERROR_MESSAGE);
    //     }
    // }

}
