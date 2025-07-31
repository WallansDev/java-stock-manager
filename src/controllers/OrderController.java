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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import views.*;
import models.*;

/**
 * Contrôleur permettant de gérer les commandes : chargement, sauvegarde, ajout,
 * modification du statut, abonnement aux changements et accès à la liste des
 * commandes.
 */
public class OrderController {

    /**
     * Modèle de commande associé au contrôleur (peut être utilisé pour des
     * opérations spécifiques).
     */
    private Order model;
    /**
     * Vue associée au contrôleur de commande.
     */
    private OrderView view;

    /**
     * Chemin du fichier JSON de stockage des commandes.
     */
    private final String filename = "data/orders.json";

    /**
     * Liste des commandes en mémoire.
     */
    private List<Order> orders = loadOrders();

    /**
     * Callback appelé lors d'un changement sur la liste des commandes.
     */
    private Consumer<List<Order>> onOrderChange = null;

    /**
     * Sauvegarde toutes les commandes dans le fichier JSON et notifie les abonnés.
     */
    public void saveAllOrders() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(orders, writer);
            this.callSubscribes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Permet de s'abonner aux changements de la liste des commandes.
     *
     * @param con Consommateur appelé lors d'un changement
     */
    public void subscribeToOrderChange(Consumer<List<Order>> con) {
        this.onOrderChange = con;
    }

    /**
     * Charge la liste des commandes depuis le fichier JSON.
     *
     * @return Liste des commandes chargées
     */
    public List<Order> loadOrders() {
        Gson gson = new Gson();
        File file = new File(filename);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<Order>>() {
            }.getType();
            List<Order> list = gson.fromJson(reader, listType);
            this.orders = list;

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
        if (this.onOrderChange != null) {
            this.onOrderChange.accept(orders);
        }
    }

    /**
     * Retourne la commande à l'index donné dans la liste.
     *
     * @param index Index de la commande
     * @return Commande correspondante ou null si l'index est invalide
     */
    public Order getOrder(int index) {
        return index >= 0 && index < orders.size() ? orders.get(index) : null;
    }

    /**
     * Ajoute une nouvelle commande à la liste à partir d'un client, d'une liste de
     * produits et de quantités.
     *
     * @param client     Client associé à la commande
     * @param products   Liste des produits commandés
     * @param quantities Liste des quantités pour chaque produit
     */
    public void addOrder(Client client, List<Product> products, List<Integer> quantities) {
        if (client == null || products == null || quantities == null) {
            System.out.println("Erreur : client ou products ou quantités null");
            return;
        }
        if (products.size() != quantities.size()) {
            System.out.println("Erreur : taille des listes products/quantités incohérente");
            return;
        }

        List<String> productIds = new ArrayList<>();
        for (Product p : products) {
            productIds.add(p.getId());
        }

        LocalDate futurDate = LocalDate.now().plusDays(60);
        String deliveryDate = futurDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        int nextOrderNumber = 1;
        List<Order> orders = loadOrders();
        nextOrderNumber = orders.stream()
                .mapToInt(Order::getOrderNumber)
                .max()
                .orElse(0) + 1;

        Order newOrder = new Order(UUID.randomUUID().toString(), nextOrderNumber, client.getId(), productIds,
                quantities,
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), deliveryDate,
                "A expédier");

        orders.add(newOrder);

        this.saveAllOrders();
    }

    /**
     * Met à jour le statut d'une commande à partir de son numéro, et crée une
     * facture si la commande est livrée.
     *
     * @param orderNumber      Numéro de la commande à mettre à jour
     * @param newStatus        Nouveau statut à appliquer
     * @param billController   Contrôleur de factures pour la création de facture
     * @param clientController Contrôleur de clients pour retrouver le client
     *                         associé
     */
    public void updateOrderStatus(Integer orderNumber, String newStatus, BillController billController,
            ClientController clientController) {
        Order order = orders.stream()
                .filter(o -> o.getOrderNumber() == orderNumber)
                .findFirst()
                .orElse(null);

        if (order != null) {
            order.setStatus(newStatus);
            saveAllOrders();
            if ("Livrée".equalsIgnoreCase(newStatus)) {
                Client client = clientController.getClientById(order.getClientId());
                billController.createBill(order, client);
            }
        }
    }
}
