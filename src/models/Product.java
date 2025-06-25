package src.models;

public class Product {
    public String id;
    public String name;
    public int quantity;
    public float price;

    public Product(String id, String name, int quantity, float price) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }
}
