package views;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.*;
import java.awt.BorderLayout;
import java.util.List;
import java.awt.*;
import controllers.*;
import models.*;

/**
 * Vue Swing permettant d'afficher et de gérer la liste des bons de livraison.
 * Permet d'afficher les détails d'un bon de livraison pour une commande
 * sélectionnée.
 */
public class DeliveryNoteView extends JPanel {
    /**
     * Liste graphique des bons de livraison (affiche les commandes).
     */
    private JList<DeliveryNote> deliveryNotesList;
    /**
     * Modèle de données pour la liste des bons de livraison.
     */
    private DefaultListModel<DeliveryNote> listModel;
    /**
     * Contrôleur de bons de livraison associé à la vue.
     */
    private DeliveryNoteController controller;

    /**
     * Liste graphique des commandes.
     */
    private JList<Order> orderList;
    /**
     * Modèle de données pour la liste des commandes.
     */
    private DefaultListModel<Order> orderListModel;
    /**
     * Contrôleur de commandes utilisé pour charger les commandes.
     */
    private OrderController orderController;

    /**
     * Construit la vue des bons de livraison et initialise l'interface graphique.
     *
     * @param controller Contrôleur de bons de livraison à utiliser
     */
    public DeliveryNoteView(DeliveryNoteController controller) {
        this.controller = controller;
        this.orderController = new OrderController();
        initializeUI();
    }

    /**
     * Initialise l'interface graphique de la vue des bons de livraison.
     */
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