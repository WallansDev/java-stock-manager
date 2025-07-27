package src.models;

import java.util.List;

import src.controllers.ClientController;
import src.controllers.OrderController;

public class Order {

    private OrderController controller;

    public String id;
    public Integer orderNumber;
    public String clientId;
    public List<String> productIds;
    public List<Integer> quantities;
    public String purchaseDate;
    public String deliveryDate;
    public String status;

    public Order(String id, Integer orderNumber, String clientId, List<String> productIds, List<Integer> quantities,
            String purchaseDate, String deliveryDate, String status) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.clientId = clientId;
        this.productIds = productIds;
        this.quantities = quantities;
        this.purchaseDate = purchaseDate;
        this.deliveryDate = deliveryDate;
        this.status = status;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientId() {
        return this.clientId;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Integer getOrderNumber() {
        return this.orderNumber;
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

    public List<Integer> getQuantities() {
        return this.quantities;
    }

    public void setQuantities(List<Integer> quantities) {
        this.quantities = quantities;
    }

    public String getPurchaseDate() {
        return this.purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getDeliveryDate() {
        return this.deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return " - De : " + getClientId() + " - " + getProductIds() + " - Commandé le : "
                + getPurchaseDate() + " - Statut : " + getStatus();
    }

}
