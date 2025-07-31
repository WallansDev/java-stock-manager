package tests.models;

import org.junit.Test;
import static org.junit.Assert.*;
import models.Product;

public class ProductTest {
    @Test
    public void testProductConstructor() {
        Product p = new Product("P001", "Produit Test", 10, 4.99);
        assertEquals("P001", p.getId());
        assertEquals("Produit Test", p.getName());
        assertEquals(10, p.getQuantity());
        assertEquals(4.99, p.getPrice(), 0.001);
    }
}