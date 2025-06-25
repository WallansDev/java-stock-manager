package src.views;

import src.*;
import src.controllers.*;
import src.models.*;
import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame implements ModelObserver {
    private StockModel model;
    private ProductController productController;
    private ClientController clientController;
    private OrderController orderController;
    private BillController billController;
    private DeliveryController deliveryController;

    private ProductView productView;
    private ClientView clientView;
    private OrderView orderView;
    private BillView billView;
    private DeliveryView deliveryView;

    public MainView() {
        initializeModel();
        initializeControllers();
        initializeViews();
        setupUI();

        model.addObserver(this);
    }

    private void initializeModel() {
        model = new StockModel();
    }

    private void initializeControllers() {
        productController = new ProductController(model);
        clientController = new ClientController(model);
        orderController = new OrderController(model);
        billController = new BillController(model);
        deliveryController = new DeliveryController(model);
    }

    private void initializeViews() {
        productView = new ProductView(model, productController);
        clientView = new ClientView(model, clientController);
        orderView = new OrderView(model, orderController);
        billView = new BillView(model, billController);
        deliveryView = new DeliveryView(model, deliveryController);

        // Ajouter les vues comme observateurs
        model.addObserver(productView);
        model.addObserver(clientView);
        model.addObserver(orderView);
        model.addObserver(billView);
        model.addObserver(deliveryView);
    }

    private void setupUI() {
        setTitle("Gestion de Stock - Architecture MVC");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Produits", productView);
        tabs.addTab("Clients", clientView);
        tabs.addTab("Commandes", orderView);
        tabs.addTab("Bons de livraison", deliveryView);
        tabs.addTab("Factures", billView);

        add(tabs);
    }

    @Override
    public void onModelChanged(String eventType) {
        // La vue principale peut réagir aux changements globaux si nécessaire
        SwingUtilities.invokeLater(() -> {
            // Mettre à jour le titre avec des statistiques par exemple
            int productCount = model.getProducts().size();
            int clientCount = model.getClients().size();
            int orderCount = model.getOrders().size();

            setTitle(String.format("Gestion de Stock - %d produits, %d clients, %d commandes",
                    productCount, clientCount, orderCount));
        });
    }
}