package src.models;

public class DeliveryNote {
    public String id;
    public String orderId;
    public String date;
    public String clientName;
    public String status;

    public DeliveryNote(String id, String orderId, String date, String clientName, String status) {
        this.id = id;
        this.orderId = orderId;
        this.date = date;
        this.clientName = clientName;
        this.status = status;
    }
}