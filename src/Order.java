package src;

import java.util.List;

public class Order {
    public String id;
    public String clientId;
    public List<String> productIds;
    public String date;
    public String status;
    public String orderNumber;

    public Order(String id, String orderNumber, String clientId, List<String> productIds, String date, String status) {
        this.id = id;
        this.clientId = clientId;
        this.productIds = productIds;
        this.date = date;
        this.status = status;
        this.orderNumber = orderNumber;
    }
}
