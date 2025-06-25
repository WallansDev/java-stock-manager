package src.models;

import src.controllers.*;

import java.util.*;

public class StockModel {
    private List<Product> products;
    private List<Client> clients;
    private List<Order> orders;
    private List<Bill> bills;
    private List<DeliveryNote> deliveries;

    private List<ModelObserver> observers = new ArrayList<>();

    public StockModel() {
        loadData();
    }

    private void loadData() {
        products = DataManager.loadProducts();
        clients = DataManager.loadClients();
        orders = DataManager.loadOrders();
        bills = DataManager.loadBills();
        deliveries = DataManager.loadDeliveries();
    }

    public void addObserver(ModelObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(ModelObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(String eventType) {
        for (ModelObserver observer : observers) {
            observer.onModelChanged(eventType);
        }
    }

    // === PRODUITS ===
    public List<Product> getProducts() {
        return new ArrayList<>(products);
    }

    public void addProduct(Product product) {
        products.add(product);
        DataManager.saveProducts(products);
        notifyObservers("PRODUCT_ADDED");
    }

    public void updateProduct(int index, Product product) {
        if (index >= 0 && index < products.size()) {
            products.set(index, product);
            DataManager.saveProducts(products);
            notifyObservers("PRODUCT_UPDATED");
        }
    }

    public void deleteProduct(int index) {
        if (index >= 0 && index < products.size()) {
            products.remove(index);
            DataManager.saveProducts(products);
            notifyObservers("PRODUCT_DELETED");
        }
    }

    public Product getProduct(int index) {
        return index >= 0 && index < products.size() ? products.get(index) : null;
    }

    // === CLIENTS ===
    public List<Client> getClients() {
        return new ArrayList<>(clients);
    }

    public void addClient(Client client) {
        clients.add(client);
        DataManager.saveClients(clients);
        notifyObservers("CLIENT_ADDED");
    }

    public void updateClient(int index, Client client) {
        if (index >= 0 && index < clients.size()) {
            clients.set(index, client);
            DataManager.saveClients(clients);
            notifyObservers("CLIENT_UPDATED");
        }
    }

    public void deleteClient(int index) {
        if (index >= 0 && index < clients.size()) {
            clients.remove(index);
            DataManager.saveClients(clients);
            notifyObservers("CLIENT_DELETED");
        }
    }

    public Client getClient(int index) {
        return index >= 0 && index < clients.size() ? clients.get(index) : null;
    }

    public Client getClientById(String id) {
        return clients.stream().filter(c -> c.id.equals(id)).findFirst().orElse(null);
    }

    // === COMMANDES ===
    public List<Order> getOrders() {
        return new ArrayList<>(orders);
    }

    public void addOrder(Order order) {
        orders.add(order);
        DataManager.saveOrders(orders);
        notifyObservers("ORDER_ADDED");
    }

    public void updateOrder(int index, Order order) {
        if (index >= 0 && index < orders.size()) {
            orders.set(index, order);
            DataManager.saveOrders(orders);
            notifyObservers("ORDER_UPDATED");
        }
    }

    public void deleteOrder(int index) {
        if (index >= 0 && index < orders.size()) {
            Order order = orders.get(index);
            // Remettre les produits en stock
            for (String prodId : order.productIds) {
                products.stream().filter(p -> p.id.equals(prodId))
                        .findFirst().ifPresent(p -> p.quantity++);
            }
            orders.remove(index);
            DataManager.saveOrders(orders);
            DataManager.saveProducts(products);
            notifyObservers("ORDER_DELETED");
        }
    }

    public Order getOrder(int index) {
        return index >= 0 && index < orders.size() ? orders.get(index) : null;
    }

    public String generateNextOrderNumber() {
        return DataManager.generateNextOrderNumber(orders);
    }

    // === FACTURES ===
    public List<Bill> getBills() {
        return new ArrayList<>(bills);
    }

    public void addBill(Bill bill) {
        bills.add(bill);
        DataManager.saveBills(bills);
        notifyObservers("BILL_ADDED");
    }

    public void updateBill(int index, Bill bill) {
        if (index >= 0 && index < bills.size()) {
            bills.set(index, bill);
            DataManager.saveBills(bills);
            notifyObservers("BILL_UPDATED");
        }
    }

    public void deleteBill(int index) {
        if (index >= 0 && index < bills.size()) {
            bills.remove(index);
            DataManager.saveBills(bills);
            notifyObservers("BILL_DELETED");
        }
    }

    public Bill getBill(int index) {
        return index >= 0 && index < bills.size() ? bills.get(index) : null;
    }

    // === BONS DE LIVRAISON ===
    public List<DeliveryNote> getDeliveries() {
        return new ArrayList<>(deliveries);
    }

    public void addDelivery(DeliveryNote delivery) {
        deliveries.add(delivery);
        DataManager.saveDeliveries(deliveries);
        notifyObservers("DELIVERY_ADDED");
    }

    public void deleteDelivery(int index) {
        if (index >= 0 && index < deliveries.size()) {
            deliveries.remove(index);
            DataManager.saveDeliveries(deliveries);
            notifyObservers("DELIVERY_DELETED");
        }
    }

    public DeliveryNote getDelivery(int index) {
        return index >= 0 && index < deliveries.size() ? deliveries.get(index) : null;
    }

    // === MÉTHODES UTILITAIRES ===
    public boolean checkProductStock(String productId, int quantity) {
        Product product = products.stream().filter(p -> p.id.equals(productId)).findFirst().orElse(null);
        return product != null && product.quantity >= quantity;
    }

    public void updateProductStock(String productId, int quantity) {
        products.stream().filter(p -> p.id.equals(productId))
                .findFirst().ifPresent(p -> p.quantity -= quantity);
        DataManager.saveProducts(products);
        notifyObservers("PRODUCT_UPDATED");
    }
}
