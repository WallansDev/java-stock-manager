package views;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import controllers.*;
import models.Bill;

public class MainView extends JFrame {

    // Controllers
    private ProductController productController;
    private ClientController clientController;
    private OrderController orderController;
    private DeliveryNoteController deliveryNoteController;
    private BillController billController;

    // Views
    private ProductView productView;
    private ClientView clientView;
    private OrderView orderView;
    private DeliveryNoteView deliveryNoteView;
    private BillView billView;

    public MainView() {
        initializeControllers();
        initializeViews();
        setupUI();
    }

    private void initializeControllers() {
        productController = new ProductController();
        clientController = new ClientController();
        orderController = new OrderController();
        deliveryNoteController = new DeliveryNoteController();
        billController = new BillController();
    }

    private void initializeViews() {
        productView = new ProductView(productController);
        clientView = new ClientView(clientController);
        orderView = new OrderView(orderController, billController, clientController);
        deliveryNoteView = new DeliveryNoteView(deliveryNoteController);
        billView = new BillView(billController);
    }

    private void setupUI() {
        setTitle("Gestion de Stock");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Produits", productView);
        tabs.addTab("Clients", clientView);
        tabs.addTab("Commandes", orderView);
        tabs.addTab("Bons de livraison", deliveryNoteView);
        tabs.addTab("Factures", billView);

        add(tabs);
    }

}
