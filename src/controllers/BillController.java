package controllers;

import models.*;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

/**
 * Contrôleur permettant de gérer les factures (Bill) : chargement, sauvegarde,
 * création, abonnement aux changements et gestion du paiement.
 */
public class BillController {
    /**
     * Liste des factures en mémoire.
     */
    private List<Bill> bills = new ArrayList<>();
    /**
     * Chemin du fichier JSON de stockage des factures.
     */
    private final String filename = "data/bills.json";

    /**
     * Callback appelé lors d'un changement sur la liste des factures.
     */
    private Consumer<List<Bill>> onBillChange = null;

    /**
     * Charge la liste des factures depuis le fichier JSON.
     *
     * @return Liste des factures chargées
     */
    public List<Bill> loadBills() {
        Gson gson = new Gson();
        File file = new File(filename);
        if (!file.exists()) {
            this.bills = new ArrayList<>();
            return bills;
        }
        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<Bill>>() {
            }.getType();
            this.bills = gson.fromJson(reader, listType);
            return bills;
        } catch (IOException e) {
            e.printStackTrace();
            this.bills = new ArrayList<>();
            return bills;
        }
    }

    /**
     * Permet de s'abonner aux changements de la liste des factures.
     *
     * @param con Consommateur appelé lors d'un changement
     */
    public void subscribeToBillChange(Consumer<List<Bill>> con) {
        this.onBillChange = con;
    }

    /**
     * Appelle le callback d'abonnement s'il existe.
     */
    private void callSubscribes() {
        if (this.onBillChange != null) {
            this.onBillChange.accept(bills);
        }
    }

    /**
     * Sauvegarde toutes les factures dans le fichier JSON et notifie les abonnés.
     */
    public void saveAllBills() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(bills, writer);
            this.callSubscribes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Crée une nouvelle facture pour une commande et un client donnés, ou retourne
     * la facture existante si elle existe déjà pour cette commande.
     *
     * @param order  Commande associée
     * @param client Client associé
     * @return Facture créée ou existante
     */
    public Bill createBill(Order order, Client client) {

        // Vérifie si une facture existe déjà pour cette commande
        for (Bill b : bills) {
            if (b.getOrder().getOrderNumber().equals(order.getOrderNumber())) {
                return b;
            }
        }
        String billId = UUID.randomUUID().toString().substring(0, 8);
        Bill bill = new Bill(billId, order, client, false,
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        bills.add(bill);
        saveAllBills();
        return bill;
    }

    /**
     * Marque une facture comme payée à partir de son identifiant.
     *
     * @param billId Identifiant de la facture à marquer comme payée
     */
    public void markAsPaid(String billId) {
        for (Bill b : bills) {
            if (b.getBillId().equals(billId)) {
                b.setPaid(true);

                saveAllBills();
                break;
            }
        }
    }
}