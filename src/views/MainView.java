package src.views;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import src.controllers.*;
import src.controllers.*;

public class MainView extends JFrame {

    // Controllers
    private ProductController productController;
    private ClientController clientController;
    private OrderController orderController;
    
    
    // Views
    private ProductView productView;
    private ClientView clientView;
    private OrderView orderView;

    public MainView() {
        initializeControllers();
        initializeViews();
        setupUI();
    }

    private void initializeControllers() {
        productController = new ProductController();
        clientController = new ClientController();
        orderController = new OrderController();
    }

    private void initializeViews() {
        productView = new ProductView(productController);
        clientView = new ClientView(clientController);
        orderView = new OrderView(orderController);
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
        tabs.addTab("Bons de livraison", null);
        tabs.addTab("Factures", null);

        add(tabs);
    }

}
