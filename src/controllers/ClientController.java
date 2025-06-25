package src.controllers;

import src.models.*;
import java.util.UUID;

public class ClientController {
    private StockModel model;

    public ClientController(StockModel model) {
        this.model = model;
    }

    public void addClient(String name, String email, String phone, String address) throws IllegalArgumentException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du client ne peut pas être vide");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("L'email ne peut pas être vide");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Le téléphone ne peut pas être vide");
        }

        Client client = new Client(UUID.randomUUID().toString(), name.trim(), email.trim(), phone.trim(),
                address != null ? address.trim() : "");
        model.addClient(client);
    }

    public void updateClient(int index, String name, String email, String phone, String address)
            throws IllegalArgumentException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du client ne peut pas être vide");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("L'email ne peut pas être vide");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Le téléphone ne peut pas être vide");
        }

        Client existingClient = model.getClient(index);
        if (existingClient != null) {
            Client updatedClient = new Client(existingClient.id, name.trim(), email.trim(), phone.trim(),
                    address != null ? address.trim() : "");
            model.updateClient(index, updatedClient);
        }
    }

    public void deleteClient(int index) {
        model.deleteClient(index);
    }
}
