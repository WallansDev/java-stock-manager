package src.controllers;

import src.models.*;
import java.util.Map;
import java.util.stream.Collectors;

public class DeliveryController {
    private StockModel model;

    public DeliveryController(StockModel model) {
        this.model = model;
    }

    public String getDeliveryDetails(int index) {
        DeliveryNote delivery = model.getDelivery(index);
        if (delivery == null)
            return null;

        Order order = model.getOrders().stream()
                .filter(o -> o.id.equals(delivery.orderId))
                .findFirst().orElse(null);

        if (order == null)
            return "Commande introuvable";

        Client client = model.getClientById(order.clientId);

        StringBuilder details = new StringBuilder();
        details.append("Commande n°").append(order.orderNumber).append("\n");
        details.append("Client : ").append(client != null ? client.name : "Inconnu").append("\n");
        details.append("Adresse de livraison : ").append(client != null ? client.address : "Inconnu").append("\n");
        details.append("Date de commande : ").append(order.date).append("\n");
        details.append("\nProduits :\n");

        double totalHT = 0.0;
        double TVA = 20;

        Map<String, Long> productCount = order.productIds.stream()
                .collect(Collectors.groupingBy(id -> id, Collectors.counting()));

        for (Map.Entry<String, Long> entry : productCount.entrySet()) {
            Product product = model.getProducts().stream()
                    .filter(p -> p.id.equals(entry.getKey()))
                    .findFirst().orElse(null);

            if (product != null) {
                double subtotal = product.price * entry.getValue();
                totalHT += subtotal;

                details.append("- ").append(product.name)
                        .append(" (x").append(entry.getValue()).append(")")
                        .append(" - ").append(String.format("%.2f €", product.price)).append(" /unité")
                        .append(" => ").append(String.format("%.2f €", subtotal)).append("\n");
            }
        }

        details.append("\nPrix total (HT) : ").append(String.format("%.2f", totalHT)).append("€\n");
        details.append("TVA : ").append(TVA).append("%\n");
        details.append("Prix total (TTC) : ").append(String.format("%.2f", totalHT * 1.2)).append("€\n");

        return details.toString();
    }

    public void deleteDelivery(int index) {
        model.deleteDelivery(index);
    }
}
