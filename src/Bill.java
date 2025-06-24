package src;

public class Bill {
    public String id;
    public String orderId;
    public String date;
    public String status;

    public Bill(String id, String orderId, String date, String status) {
        this.id = id;
        this.orderId = orderId;
        this.date = date;
        this.status = status;
    }
}