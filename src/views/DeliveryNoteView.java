package src.views;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.*;
import java.awt.BorderLayout;
import java.util.List;
import java.util.ArrayList;
import java.awt.*;
import src.controllers.*;
import src.models.*;
import java.text.DecimalFormat;

public class DeliveryNoteView extends JPanel {
    private JList<DeliveryNote> deliveryNotesList;
    private DefaultListModel<DeliveryNote> listModel;
    private DeliveryNoteController controller;

    private JList<Order> orderList;
    private DefaultListModel<Order> orderListModel;
    private OrderController orderController;

    public DeliveryNoteView(DeliveryNoteController controller) {
        this.controller = controller;
        this.orderController = new OrderController();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        orderListModel = new DefaultListModel<>();

        orderList = new JList<>(orderListModel);

        orderList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Order) {
                    Order order = (Order) value;
                    // Récupérer le nom du client si tu veux l’afficher
                    ClientController clientController = new ClientController();
                    Client client = clientController.getClientById(order.getClientId());
                    String clientName = client != null ? client.getName() : "Inconnu";
                    setText("Commande #" + order.getOrderNumber() +
                            " - " + clientName +
                            " [" + order.getPurchaseDate() + "]");
                }
                return this;
            }
        });

        List<Order> orders = orderController.loadOrders();
        for (Order order : orders) {
            orderListModel.addElement(order);
        }

        JButton showBtn = new JButton("Afficher le bon de livraison");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(showBtn);

        add(new JScrollPane(orderList), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        showBtn.addActionListener(e -> {
            int selectedIndex = orderList.getSelectedIndex();

            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner une commande à afficher.");
                return;
            }

            Order order = orderListModel.getElementAt(selectedIndex);

            ClientController clientController = new ClientController();
            Client client = clientController.getClientById(order.getClientId());
            String clientName = client != null ? client.getName() : "Inconnu";

            ProductController productController = new ProductController();
            StringBuilder productsDetails = new StringBuilder();
            double totalHT = 0.0;

            int TVA = 20;
            double frais_de_livraison = 4.99;
            List<String> productIds = order.getProductIds();
            List<Integer> quantities = order.getQuantities();
            for (int i = 0; i < productIds.size(); i++) {
                Product p = productController.getProductById(productIds.get(i));
                String productName = (p != null) ? p.getName() : "Inconnu";
                int qte = (quantities != null && i < quantities.size()) ? quantities.get(i) : 0;
                double prix = (p != null) ? p.getPrice() : 0.0f;
                String prixFormate = String.format("%.2f", prix);

                productsDetails.append("- ")
                        .append(productName)
                        .append(" (x").append(qte).append(") : ")
                        .append(prixFormate).append("€/unité\n");

                totalHT += prix * qte;

            }
            double totalTTC = totalHT * (1 + TVA / 100.0) + frais_de_livraison;
            String totalHTStr = String.format("%.2f", totalHT);
            String totalTTCStr = String.format("%.2f", totalTTC);

            String message = "Client : " + clientName +
                    "\nDate d'achat : " + order.getPurchaseDate() +
                    "\nDate de livraison : " + order.getDeliveryDate() +
                    "\nFrais de livraison : " + order.getDeliveryDate() +
                    "\n\nProduits :\n" + productsDetails.toString() +
                    "\nTotal HT : " + totalHTStr + " €" +
                    "\n\nTVA : " + TVA +
                    "\nFrais de livraison : " + frais_de_livraison +
                    "\nTotal TTC : " + totalTTCStr + " €";

            JOptionPane.showMessageDialog(this, message, "Bon de livraison de la commande #" + order.getOrderNumber(),
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }
}