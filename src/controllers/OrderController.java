package src.controllers;

import src.models.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OrderController {
    private StockModel model;

    public OrderController(StockModel model) {
        this.model = model;
    }

    public void createOrder(String clientId, Map<String, Integer> productQuantities) throws IllegalArgumentException {
        if (clientId == null || clientId.trim().isEmpty()) {
            throw new IllegalArgumentException("Le client doit être sélectionné");
        }
        if (productQuantities.isEmpty()) {
            throw new IllegalArgumentException("Au moins un produit doit être sélectionné");
        }

        // Vérifier le stock
        for (Map.Entry<String, Integer> entry : productQuantities.entrySet()) {
            if (!model.checkProductStock(entry.getKey(), entry.getValue())) {
                throw new IllegalArgumentException("Stock insuffisant pour un ou plusieurs produits");
            }
        }

        // Créer la liste des IDs de produits
        List<String> productIds = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : productQuantities.entrySet()) {
            String productId = entry.getKey();
            int quantity = entry.getValue();

            for (int i = 0; i < quantity; i++) {
                productIds.add(productId);
            }

            // Déduire le stock
            model.updateProductStock(productId, quantity);
        }

        // Créer la commande
        String orderNumber = model.generateNextOrderNumber();
        Order order = new Order(
                UUID.randomUUID().toString(),
                orderNumber,
                clientId,
                productIds,
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                "À expédier");

        model.addOrder(order);

        // Créer automatiquement le bon de livraison
        Client client = model.getClientById(clientId);
        if (client != null) {
            DeliveryNote delivery = new DeliveryNote(
                    UUID.randomUUID().toString(),
                    order.id,
                    LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    client.name,
                    "À expédier");
            model.addDelivery(delivery);
        }
    }

    public void updateOrderStatus(int index, String newStatus) {
        Order order = model.getOrder(index);
        if (order != null) {
            Order updatedOrder = new Order(order.id, order.orderNumber, order.clientId,
                    order.productIds, order.date, newStatus);
            model.updateOrder(index, updatedOrder);

            // Si livré, créer une facture automatiquement
            if ("Livré".equals(newStatus)) {
                boolean billExists = model.getBills().stream()
                        .anyMatch(b -> b.orderId.equals(order.id));

                if (!billExists) {
                    Bill bill = new Bill(
                            UUID.randomUUID().toString(),
                            order.id,
                            LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            "Non payé");
                    model.addBill(bill);
                }
            }
        }
    }

    public void deleteOrder(int index) {
        model.deleteOrder(index);
    }
}
