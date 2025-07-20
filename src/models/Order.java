package src.models;

import java.util.List;

import src.controllers.ClientController;
import src.controllers.OrderController;

public class Order {

    private OrderController controller;

    public String id;
    public String orderNumber;
    public String clientId;
    public List<String> productIds;
    public String date;
    public String status;

    public Order(String id, String orderNumber, String clientId, List<String> productIds, String date, String status) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.clientId = clientId;
        this.productIds = productIds;
        this.date = date;
        this.status = status;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return this.orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getClientId() {
        return this.clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public List<String> getProductIds() {
        return this.productIds;
    }

    public void setProductIds(List<String> productIds) {
        this.productIds = productIds;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return '#' + getOrderNumber() + " - De : " + getClientId() + " - " + getProductIds() + " - Commandé le : "
                + getDate() + " - Statut : " + getStatus();
        // return '#' + getOrderNumber() + " - De : " +
        // ClientController.class.getClient() + " - " + getProductIds() + " - Commandé
        // le : " + getDate() + " - Statut : " + getStatus();
    }

}
