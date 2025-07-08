package src.views;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import src.controllers.ClientController;
import src.controllers.ProductController;

public class MainView extends JFrame {

    // Controllers
    private ProductController productController;
    private ClientController clientController;
    
    
    // Views
    private ProductView productView;
    private ClientView clientView;

    public MainView() {
        initializeControllers();
        initializeViews();
        setupUI();
    }

    private void initializeControllers() {
        productController = new ProductController();
        clientController = new ClientController();
    }

    private void initializeViews() {
        productView = new ProductView(productController);
        clientView = new ClientView(clientController);
    }

    private void setupUI() {
        setTitle("Gestion de Stock");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Produits", productView);
        tabs.addTab("Clients", clientView);

        add(tabs);
    }

}
