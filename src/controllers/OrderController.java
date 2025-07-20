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
import java.util.Date;

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

    private void saveAllOrders() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(orders, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void subscribeToClienChange(Consumer<List<Order>> con) {
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

    public void addOrder(String clientId, List<String> productsId) {
        Order order = new Order(UUID.randomUUID().toString(), "1", clientId, productsId, new Date().toString(),
                "A expédier");
        orders.add(order);
        saveAllOrders();
        callSubscribes();
    }
}
