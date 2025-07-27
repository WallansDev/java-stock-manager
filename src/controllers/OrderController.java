package src.controllers;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import src.views.*;
import src.models.*;

public class OrderController {

    private Order model;
    private OrderView view;

    private final String filename = "data/orders.json";

    private List<Order> orders = loadOrders();

    private Consumer<List<Order>> onOrderChange = null;

    public void saveAllOrders() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(orders, writer);
            this.callSubscribes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void subscribeToOrderChange(Consumer<List<Order>> con) {
        this.onOrderChange = con;
    }

    public List<Order> loadOrders() {
        Gson gson = new Gson();
        File file = new File(filename);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<Order>>() {
            }.getType();
            List<Order> list = gson.fromJson(reader, listType);
            this.orders = list;

            this.callSubscribes();

            return list;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void callSubscribes() {
        if (this.onOrderChange != null) {
            this.onOrderChange.accept(orders);
        }
    }

    public Order getOrder(int index) {
        return index >= 0 && index < orders.size() ? orders.get(index) : null;
    }

    public void addOrder(Client client, List<Product> products, List<Integer> quantities) {
        if (client == null || products == null || quantities == null) {
            System.out.println("Erreur : client ou products ou quantités null");
            return;
        }
        if (products.size() != quantities.size()) {
            System.out.println("Erreur : taille des listes products/quantités incohérente");
            return;
        }

        List<String> productIds = new ArrayList<>();
        for (Product p : products) {
            productIds.add(p.getId());
        }

        LocalDate futurDate = LocalDate.now().plusDays(60);
        String deliveryDate = futurDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        int nextOrderNumber = 1;
        List<Order> orders = loadOrders();
        nextOrderNumber = orders.stream()
                .mapToInt(Order::getOrderNumber)
                .max()
                .orElse(0) + 1;

        Order newOrder = new Order(UUID.randomUUID().toString(), nextOrderNumber, client.getId(), productIds,
                quantities,
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), deliveryDate,
                "A expédier");

        orders.add(newOrder);

        this.saveAllOrders();
    }

    public void updateOrderStatus(Integer orderNumber, String newStatus, BillController billController,
            ClientController clientController) {
        Order order = orders.stream()
                .filter(o -> o.getOrderNumber() == orderNumber)
                .findFirst()
                .orElse(null);

        if (order != null) {
            order.setStatus(newStatus);
            saveAllOrders();
            if ("Livrée".equalsIgnoreCase(newStatus)) {
                Client client = clientController.getClientById(order.getClientId());
                billController.createBill(order, client);
            }
        }
    }
}
