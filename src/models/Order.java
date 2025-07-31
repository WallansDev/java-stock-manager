package models;

import java.util.List;

import controllers.OrderController;

/**
 * Représente une commande passée par un client, contenant des informations sur
 * le client,
 * les produits commandés, les quantités, les dates d'achat et de livraison,
 * ainsi que le statut de la commande.
 */
public class Order {

    /**
     * Contrôleur associé à la commande (peut être utilisé pour des opérations
     * spécifiques).
     */
    private OrderController controller;

    /**
     * Identifiant unique de la commande.
     */
    public String id;
    /**
     * Numéro de la commande.
     */
    public Integer orderNumber;
    /**
     * Identifiant du client ayant passé la commande.
     */
    public String clientId;
    /**
     * Liste des identifiants des produits commandés.
     */
    public List<String> productIds;
    /**
     * Liste des quantités correspondant à chaque produit commandé.
     */
    public List<Integer> quantities;
    /**
     * Date d'achat de la commande (format chaîne de caractères).
     */
    public String purchaseDate;
    /**
     * Date de livraison prévue ou effective (format chaîne de caractères).
     */
    public String deliveryDate;
    /**
     * Statut actuel de la commande (ex : "en cours", "livrée", etc.).
     */
    public String status;

    /**
     * Construit une nouvelle commande avec les informations fournies.
     *
     * @param id           Identifiant unique de la commande
     * @param orderNumber  Numéro de la commande
     * @param clientId     Identifiant du client
     * @param productIds   Liste des identifiants des produits commandés
     * @param quantities   Liste des quantités pour chaque produit
     * @param purchaseDate Date d'achat de la commande
     * @param deliveryDate Date de livraison prévue ou effective
     * @param status       Statut de la commande
     */
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

    /**
     * Retourne l'identifiant unique de la commande.
     * 
     * @return Identifiant unique
     */
    public String getId() {
        return this.id;
    }

    /**
     * Définit l'identifiant unique de la commande.
     * 
     * @param id Identifiant unique
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Retourne l'identifiant du client ayant passé la commande.
     * 
     * @return Identifiant du client
     */
    public String getClientId() {
        return this.clientId;
    }

    /**
     * Définit le numéro de la commande.
     * 
     * @param orderNumber Numéro de la commande
     */
    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    /**
     * Retourne le numéro de la commande.
     * 
     * @return Numéro de la commande
     */
    public Integer getOrderNumber() {
        return this.orderNumber;
    }

    /**
     * Définit l'identifiant du client ayant passé la commande.
     * 
     * @param clientId Identifiant du client
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Retourne la liste des identifiants des produits commandés.
     * 
     * @return Liste des identifiants de produits
     */
    public List<String> getProductIds() {
        return this.productIds;
    }

    /**
     * Définit la liste des identifiants des produits commandés.
     * 
     * @param productIds Liste des identifiants de produits
     */
    public void setProductIds(List<String> productIds) {
        this.productIds = productIds;
    }

    /**
     * Retourne la liste des quantités pour chaque produit commandé.
     * 
     * @return Liste des quantités
     */
    public List<Integer> getQuantities() {
        return this.quantities;
    }

    /**
     * Définit la liste des quantités pour chaque produit commandé.
     * 
     * @param quantities Liste des quantités
     */
    public void setQuantities(List<Integer> quantities) {
        this.quantities = quantities;
    }

    /**
     * Retourne la date d'achat de la commande.
     * 
     * @return Date d'achat (format chaîne)
     */
    public String getPurchaseDate() {
        return this.purchaseDate;
    }

    /**
     * Définit la date d'achat de la commande.
     * 
     * @param purchaseDate Date d'achat (format chaîne)
     */
    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    /**
     * Retourne la date de livraison prévue ou effective.
     * 
     * @return Date de livraison (format chaîne)
     */
    public String getDeliveryDate() {
        return this.deliveryDate;
    }

    /**
     * Définit la date de livraison prévue ou effective.
     * 
     * @param deliveryDate Date de livraison (format chaîne)
     */
    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    /**
     * Retourne le statut actuel de la commande.
     * 
     * @return Statut de la commande
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * Définit le statut actuel de la commande.
     * 
     * @param status Statut de la commande
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Retourne une représentation textuelle de la commande.
     * 
     * @return Chaîne décrivant la commande
     */
    @Override
    public String toString() {
        return " - De : " + getClientId() + " - " + getProductIds() + " - Commandé le : "
                + getPurchaseDate() + " - Statut : " + getStatus();
    }

}
