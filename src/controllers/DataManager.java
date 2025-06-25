package src.controllers;

import com.google.gson.reflect.TypeToken;

import src.models.*;

import com.google.gson.Gson;

import java.io.*;
import java.util.*;

public class DataManager {
    private static final String PRODUCT_FILE = "data/products.json";
    private static final String CLIENT_FILE = "data/clients.json";
    private static final String ORDER_FILE = "data/orders.json";
    private static final String BILL_FILE = "data/bills.json";
    private static final String DELIVERY_FILE = "data/deliveries.json";

    private static final Gson gson = new Gson();

    // ----- Partie Produits -----
    public static List<Product> loadProducts() {
        return loadListFromFile(PRODUCT_FILE, new TypeToken<List<Product>>() {
        }.getType());
    }

    public static void saveProducts(List<Product> products) {
        saveListToFile(PRODUCT_FILE, products);
    }

    // ----- Partie Clients -----
    public static List<Client> loadClients() {
        return loadListFromFile(CLIENT_FILE, new TypeToken<List<Client>>() {
        }.getType());
    }

    public static void saveClients(List<Client> clients) {
        saveListToFile(CLIENT_FILE, clients);
    }

    // ----- Partie Commandes -----
    public static List<Order> loadOrders() {
        return loadListFromFile(ORDER_FILE, new TypeToken<List<Order>>() {
        }.getType());
    }

    public static void saveOrders(List<Order> orders) {
        saveListToFile(ORDER_FILE, orders);
    }

    // ----- Partie Factures -----
    public static List<Bill> loadBills() {
        return loadListFromFile(BILL_FILE, new TypeToken<List<Bill>>() {
        }.getType());
    }

    public static void saveBills(List<Bill> bills) {
        saveListToFile(BILL_FILE, bills);
    }

    // ----- Partie Bon de livraison -----
    public static List<DeliveryNote> loadDeliveries() {
        return loadListFromFile(DELIVERY_FILE, new TypeToken<List<DeliveryNote>>() {
        }.getType());
    }

    public static void saveDeliveries(List<DeliveryNote> deliveries) {
        saveListToFile(DELIVERY_FILE, deliveries);
    }

    // ----- Fonctions utilitaires -----
    private static <T> List<T> loadListFromFile(String filePath, java.lang.reflect.Type type) {
        File file = new File(filePath);
        if (!file.exists()) {
            saveListToFile(filePath, new ArrayList<>());
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(file)) {
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static <T> void saveListToFile(String filePath, List<T> list) {
        try {
            File file = new File(filePath);
            File parentDir = file.getParentFile();

            if (parentDir != null && !parentDir.exists()) {
                boolean created = parentDir.mkdirs();
                if (!created) {
                    System.err.println("Erreur : impossible de créer le dossier pour " + filePath);
                    return;
                }
            }

            try (Writer writer = new FileWriter(file)) {
                gson.toJson(list, writer);
                System.out.println("Données enregistrées dans " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String generateNextOrderNumber(List<Order> orders) {
        int max = orders.stream()
                .map(o -> {
                    try {
                        return Integer.parseInt(o.orderNumber);
                    } catch (Exception e) {
                        return 999; // Si champ vide ou mal formé
                    }
                })
                .max(Integer::compare)
                .orElse(999);
        return String.valueOf(max + 1);
    }
}
