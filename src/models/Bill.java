package models;

/**
 * Représente une facture associée à une commande et à un client, contenant les
 * informations
 * sur le paiement, la date de facturation, et les références liées.
 */
public class Bill {
    /**
     * Identifiant unique de la facture.
     */
    private String billId;
    /**
     * Commande associée à la facture.
     */
    private Order order;
    /**
     * Client associé à la facture.
     */
    private Client client;
    /**
     * Indique si la facture a été payée.
     */
    private boolean paid;
    /**
     * Date d'émission de la facture (format chaîne de caractères).
     */
    private String billDate;

    /**
     * Construit une nouvelle facture avec les informations fournies.
     *
     * @param billId   Identifiant unique de la facture
     * @param order    Commande associée
     * @param client   Client associé
     * @param paid     Statut de paiement de la facture
     * @param billDate Date d'émission de la facture
     */
    public Bill(String billId, Order order, Client client, boolean paid, String billDate) {
        this.billId = billId;
        this.order = order;
        this.client = client;
        this.paid = paid;
        this.billDate = billDate;
    }

    /**
     * Retourne l'identifiant unique de la facture.
     * 
     * @return Identifiant de la facture
     */
    public String getBillId() {
        return billId;
    }

    /**
     * Retourne la commande associée à la facture.
     * 
     * @return Commande associée
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Retourne le client associé à la facture.
     * 
     * @return Client associé
     */
    public Client getClient() {
        return client;
    }

    /**
     * Indique si la facture a été payée.
     * 
     * @return true si payée, false sinon
     */
    public boolean isPaid() {
        return paid;
    }

    /**
     * Retourne la date d'émission de la facture.
     * 
     * @return Date d'émission (format chaîne)
     */
    public String getBillDate() {
        return billDate;
    }

    /**
     * Définit le statut de paiement de la facture.
     * 
     * @param paid true si payée, false sinon
     */
    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    /**
     * Retourne une représentation textuelle de la facture.
     * 
     * @return Chaîne décrivant la facture
     */
    @Override
    public String toString() {
        return "Facture de la commande #" + order.getOrderNumber() + " - " + client.getName() + " - "
                + (paid ? "Payé" : "Non payé");
    }
}