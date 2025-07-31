package controllers;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import models.*;
import views.*;

/**
 * Contrôleur permettant de gérer les bons de livraison : chargement,
 * sauvegarde, ajout,
 * abonnement aux changements et accès à la liste des bons de livraison.
 */
public class DeliveryNoteController {
    /**
     * Modèle de bon de livraison associé au contrôleur (peut être utilisé pour des
     * opérations spécifiques).
     */
    private DeliveryNote model;
    /**
     * Vue associée au contrôleur de bon de livraison.
     */
    private DeliveryNoteView view;

    /**
     * Chemin du fichier JSON de stockage des bons de livraison.
     */
    private final String filename = "data/deliveries.json";

    /**
     * Liste des bons de livraison en mémoire.
     */
    private List<DeliveryNote> deliveriesNotes = loadDeliveriesNotes();

    /**
     * Callback appelé lors d'un changement sur la liste des bons de livraison.
     */
    private Consumer<List<DeliveryNote>> onDeliveryNoteChange = null;

    /**
     * Sauvegarde tous les bons de livraison dans le fichier JSON.
     */
    public void saveAllDeliveriesNotes() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(deliveriesNotes, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Permet de s'abonner aux changements de la liste des bons de livraison.
     *
     * @param con Consommateur appelé lors d'un changement
     */
    public void subscribeToDeliveryNoteChange(Consumer<List<DeliveryNote>> con) {
        this.onDeliveryNoteChange = con;
    }

    /**
     * Charge la liste des bons de livraison depuis le fichier JSON.
     *
     * @return Liste des bons de livraison chargés
     */
    public List<DeliveryNote> loadDeliveriesNotes() {
        Gson gson = new Gson();
        File file = new File(filename);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<DeliveryNote>>() {
            }.getType();
            List<DeliveryNote> list = gson.fromJson(reader, listType);
            this.deliveriesNotes = list;

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
        if (this.onDeliveryNoteChange != null) {
            this.onDeliveryNoteChange.accept(deliveriesNotes);
        }
    }

    /**
     * Ajoute un nouveau bon de livraison à partir de l'identifiant de commande
     * fourni.
     *
     * @param orderId Identifiant de la commande associée au bon de livraison
     */
    public void addDeliveryNote(String orderId) {
        DeliveryNote deliverynote = new DeliveryNote(UUID.randomUUID().toString(), orderId);
        deliveriesNotes.add(deliverynote);

        this.saveAllDeliveriesNotes();
        this.callSubscribes();
    }
}
