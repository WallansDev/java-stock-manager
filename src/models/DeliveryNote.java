package src.models;

import java.util.List;

public class DeliveryNote {
    public String id;
    public String orderId;

    public DeliveryNote(String id, String orderId) {
        this.id = id;
        this.orderId = orderId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return this.orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return this.getOrderId();
    }
}
