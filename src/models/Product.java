package models;

/**
 * Représente un produit dans le système de gestion de stock.
 */
public class Product {
    /**
     * Identifiant unique du produit.
     */
    public String id;

    /**
     * Nom du produit.
     */
    public String name;

    /**
     * Quantité en stock.
     */
    public int quantity;

    /**
     * Prix unitaire du produit.
     */
    public double price;

    /**
     * Crée un nouveau produit.
     * 
     * @param id       Identifiant du produit
     * @param name     Nom du produit
     * @param quantity Quantité en stock
     * @param price    Prix unitaire
     */
    public Product(String id, String name, int quantity, double price) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public String getId() {
        return this.id;
    }

    /**
     * Modifie l'identifiant du produit.
     * 
     * @param id le nouvel identifiant du produit
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Retourne le nom du produit.
     * 
     * @return le nom du produit
     */
    public String getName() {
        return this.name;
    }

    /**
     * Modifie le nom du produit.
     * 
     * @param name le nouveau nom du produit
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retourne la quantité en stock du produit.
     * 
     * @return la quantité en stock
     */
    public int getQuantity() {
        return this.quantity;
    }

    /**
     * Modifie la quantité en stock du produit.
     * 
     * @param quantity la nouvelle quantité en stock
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Retourne le prix unitaire du produit.
     * 
     * @return le prix unitaire
     */
    public double getPrice() {
        return this.price;
    }

    /**
     * Modifie le prix unitaire du produit.
     * 
     * @param price le nouveau prix unitaire
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Retourne une représentation textuelle du produit.
     * 
     * @return une chaîne décrivant le produit
     */
    @Override
    public String toString() {
        return "Nom : " + name + " - Quantité en stock : " + quantity + " - Prix : " + String.format("%.2f", price)
                + "€";
    }
}
