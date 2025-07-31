package models;

/**
 * Représente un client du système.
 * Un client possède un identifiant, un nom, un email, une adresse et un numéro
 * de téléphone.
 */
public class Client {
    /**
     * Identifiant unique du client.
     */
    public String id;

    /**
     * Nom du client.
     */
    public String name;

    /**
     * Adresse email du client.
     */
    public String email;

    /**
     * Adresse postale du client.
     */
    public String address;

    /**
     * Numéro de téléphone du client.
     */
    public String phone;

    /**
     * Construit un nouveau client avec les informations fournies.
     * 
     * @param id      Identifiant unique du client
     * @param name    Nom du client
     * @param email   Adresse email du client
     * @param address Adresse postale du client
     * @param phone   Numéro de téléphone du client
     */
    public Client(String id, String name, String email, String address, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
        this.phone = phone;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Nom : " + getName() + " - Email : " + getEmail() + " - Téléphone : " + getPhone() + " - Adresse : "
                + getAddress();
    }

}
