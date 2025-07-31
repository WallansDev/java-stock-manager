package controllers;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JOptionPane;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import models.*;
import views.*;

/**
 * Contrôleur permettant de gérer les clients : chargement, sauvegarde, ajout,
 * modification,
 * suppression, abonnement aux changements et accès aux clients par index ou
 * identifiant.
 */
public class ClientController {

    /**
     * Modèle client associé au contrôleur (peut être utilisé pour des opérations
     * spécifiques).
     */
    private Client model;
    /**
     * Vue associée au contrôleur client.
     */
    private ClientView view;

    /**
     * Chemin du fichier JSON de stockage des clients.
     */
    private final String filename = "data/clients.json";

    /**
     * Liste des clients en mémoire.
     */
    private List<Client> clients = loadClients();

    /**
     * Callback appelé lors d'un changement sur la liste des clients.
     */
    private Consumer<List<Client>> onClientChange = null;

    /**
     * Sauvegarde tous les clients dans le fichier JSON.
     */
    private void saveAllClients() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(clients, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Permet de s'abonner aux changements de la liste des clients.
     *
     * @param con Consommateur appelé lors d'un changement
     */
    public void subscribeToClienChange(Consumer<List<Client>> con) {
        this.onClientChange = con;
    }

    /**
     * Charge la liste des clients depuis le fichier JSON.
     *
     * @return Liste des clients chargés
     */
    public List<Client> loadClients() {
        Gson gson = new Gson();
        File file = new File(filename);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<Client>>() {
            }.getType();
            List<Client> list = gson.fromJson(reader, listType);
            this.clients = list;

            this.callSubscribes();

            return list;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Appelle le callback d'abonnement s'il existe.
     */
    private void callSubscribes() {
        if (this.onClientChange != null) {
            this.onClientChange.accept(clients);
        }
    }

    /**
     * Retourne le client à l'index donné dans la liste.
     *
     * @param index Index du client
     * @return Client correspondant ou null si l'index est invalide
     */
    public Client getClient(int index) {
        return index >= 0 && index < clients.size() ? clients.get(index) : null;
    }

    /**
     * Retourne le client correspondant à l'identifiant donné.
     *
     * @param id Identifiant du client
     * @return Client correspondant ou null si non trouvé
     */
    public Client getClientById(String id) {
        List<Client> clients = loadClients();
        for (Client client : clients) {
            if (client.getId().equals(id)) {
                return client;
            }
        }
        return null;
    }

    /**
     * Met à jour le client à l'index donné dans la liste.
     *
     * @param index  Index du client à mettre à jour
     * @param client Nouveau client à placer à cet index
     */
    public void updateClientList(int index, Client client) {
        if (index >= 0 && index < clients.size()) {
            clients.set(index, client);
            this.saveAllClients();
        }
    }

    /**
     * Ajoute un nouveau client à la liste et sauvegarde.
     *
     * @param id      Identifiant du client
     * @param name    Nom du client
     * @param email   Email du client
     * @param address Adresse du client
     * @param phone   Téléphone du client
     */
    public void addClient(String id, String name, String email, String address, String phone) {
        Client newClient = new Client(id, name, email, address, phone);

        clients.add(newClient);

        this.saveAllClients();

        this.callSubscribes();
    }

    /**
     * Met à jour les informations d'un client à l'index donné.
     *
     * @param index   Index du client à mettre à jour
     * @param name    Nouveau nom
     * @param email   Nouvel email
     * @param address Nouvelle adresse
     * @param phone   Nouveau téléphone
     */
    public void updateClient(int index, String name, String email, String address, String phone) {
        if (index >= 0 && index < clients.size()) {

            System.out.println("Modification du client");

            Client existingClient = this.getClient(index);
            if (existingClient != null) {
                Client updatedClient = new Client(existingClient.id, name, email, address, phone);
                this.updateClientList(index, updatedClient);
                this.callSubscribes();
            }
        } else {
            JOptionPane.showMessageDialog(view,
                    "Choisir un client à modifier.", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Supprime un client à l'index donné de la liste et sauvegarde.
     *
     * @param index Index du client à supprimer
     */
    public void deleteProduct(int index) {
        if (index >= 0 && index < clients.size()) {
            clients.remove(index);
            saveAllClients();
            this.callSubscribes();

            System.out.println("Suppression du client.");

        } else {
            JOptionPane.showMessageDialog(view,
                    "Choisir un client à supprimer.", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

}
