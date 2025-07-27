package src.models;

import java.util.Date;

public class Bill {
    private String billId;
    private Order order;
    private Client client;
    private boolean paid;
    private String billDate;

    public Bill(String billId, Order order, Client client, boolean paid, String billDate) {
        this.billId = billId;
        this.order = order;
        this.client = client;
        this.paid = paid;
        this.billDate = billDate;
    }

    public String getBillId() {
        return billId;
    }

    public Order getOrder() {
        return order;
    }

    public Client getClient() {
        return client;
    }

    public boolean isPaid() {
        return paid;
    }

    public String getBillDate() {
        return billDate;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    @Override
    public String toString() {
        return "Facture de la commande #" + order.getOrderNumber() + " - " + client.getName() + " - "
                + (paid ? "Payé" : "Non payé");
    }
}