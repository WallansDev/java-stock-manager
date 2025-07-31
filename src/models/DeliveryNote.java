package models;

/**
 * Représente un bon de livraison du système.
 * Un bon de livraison possède un identifiant, et un identifiant de commande.
 */
public class DeliveryNote {

    /**
     * Identifiant unique du bon.
     */
    public String id;

    /**
     * Identifiant unique de la commande.
     */
    public String orderId;

    /**
     * Construit un nouveau bon de livraison avec les informations fournies.
     *
     * @param id      Identifiant unique du bon
     * @param orderId Identifiant de la commande
     */
    public DeliveryNote(String id, String orderId) {
        this.id = id;
        this.orderId = orderId;
    }

    /**
     * Définit l'identifiant unique du bon de livraison.
     * 
     * @param id Identifiant unique du bon
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Retourne l'identifiant de la commande associée.
     * 
     * @return Identifiant de la commande
     */
    public String getOrderId() {
        return this.orderId;
    }

    /**
     * Définit l'identifiant de la commande associée.
     * 
     * @param orderId Identifiant de la commande
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    /**
     * Retourne une représentation textuelle du bon de livraison.
     * 
     * @return Identifiant de la commande associée
     */
    @Override
    public String toString() {
        return this.getOrderId();
    }
}
