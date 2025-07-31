package views;

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
import controllers.*;
import models.*;

/**
 * Vue Swing permettant d'afficher et de gérer la liste des commandes.
 * Permet d'ajouter, afficher et modifier le statut des commandes via une
 * interface graphique.
 */
public class OrderView extends JPanel {
    /**
     * Modèle de données pour la liste des commandes (affichage sous forme de
     * chaînes).
     */
    private DefaultListModel<String> listModel;
    /**
     * Liste graphique des commandes (affichage sous forme de chaînes).
     */
    private JList<String> orderList;

    /**
     * Contrôleur de commandes associé à la vue.
     */
    private OrderController controller;
    /**
     * Contrôleur de factures associé à la vue.
     */
    private BillController billController;
    /**
     * Contrôleur de clients associé à la vue.
     */
    private ClientController clientController;

    /**
     * Construit la vue des commandes et initialise l'interface graphique.
     *
     * @param controller       Contrôleur de commandes à utiliser
     * @param billController   Contrôleur de factures à utiliser
     * @param clientController Contrôleur de clients à utiliser
     */
    public OrderView(OrderController controller, BillController billController, ClientController clientController) {
        this.controller = controller;
        this.billController = billController;
        this.clientController = clientController;

        initializeUI();

        controller.subscribeToOrderChange(orders -> {
            listModel.clear();
            for (Order order : orders) {
                Client client = clientController.getClientById(order.getClientId());
                String clientName = client != null ? client.getName() : "Inconnu";

                ProductController productController = new ProductController();
                List<String> productsName = new ArrayList<>();
                for (String productId : order.getProductIds()) {
                    Product p = productController.getProductById(productId);
                    if (p != null) {
                        productsName.add(p.getName());
                    }
                }

                listModel.addElement(
                        "Commande : #" + order.getOrderNumber() + " - Client : " + clientName + " - Commandé le : "
                                + order.getPurchaseDate()
                                + " - [" +
                                order.status + "]");
            }
        });

        controller.loadOrders();
    }

    /**
     * Initialise l'interface graphique de la vue des commandes.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        orderList = new JList<>(listModel);

        JButton addBtn = new JButton("Ajouter une commande");
        JButton showBtn = new JButton("Afficher la commande");
        JButton statusBtn = new JButton("Modifier le statut de la commande");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addBtn);
        buttonPanel.add(showBtn);
        buttonPanel.add(statusBtn);

        add(new JScrollPane(orderList), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> {

            ClientController clientController = new ClientController();
            List<Client> clients = clientController.loadClients();
            ProductController productController = new ProductController();
            List<Product> products = productController.loadProducts();

            if (clients.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Aucun client disponible. Créez d'abord des clients.");
                return;
            }
            if (products.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Aucun produit disponible. Créez d'abord des produits.");
                return;
            }

            JFrame windowParent = (JFrame) SwingUtilities.getWindowAncestor(this);

            final JDialog frame = new JDialog(windowParent, "Ajouter une commande", true);
            frame.setSize(500, 300);
            frame.setLocationRelativeTo(null);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JComboBox<Client> clientComboBox = new JComboBox<>();
            clientComboBox.removeAllItems();
            for (Client client : clients) {
                clientComboBox.addItem(client);
            }

            JButton submitBtn = new JButton("Ajouter");

            JPanel productPanel = new JPanel();
            productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.Y_AXIS));

            List<JCheckBox> productCheckBoxes = new ArrayList<>();
            List<JSpinner> quantitySpinners = new ArrayList<>();

            for (Product product : products) {
                JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JCheckBox checkBox = new JCheckBox(product.getName());
                JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, product.quantity, 1));
                quantitySpinner.setEnabled(false);

                checkBox.addActionListener(evt -> quantitySpinner.setEnabled(checkBox.isSelected()));

                row.add(checkBox);
                row.add(new JLabel("Quantité :"));
                row.add(quantitySpinner);

                productPanel.add(row);
                productCheckBoxes.add(checkBox);
                quantitySpinners.add(quantitySpinner);
            }

            submitBtn.addActionListener(ev -> {
                Client client = (Client) clientComboBox.getSelectedItem();

                List<Product> produitsSelectionnes = new ArrayList<>();
                List<Integer> quantites = new ArrayList<>();
                for (int i = 0; i < products.size(); i++) {
                    if (productCheckBoxes.get(i).isSelected()) {
                        produitsSelectionnes.add(products.get(i));
                        quantites.add((Integer) quantitySpinners.get(i).getValue());
                    }
                }

                if (produitsSelectionnes.isEmpty()) {
                    JOptionPane.showMessageDialog(frame,
                            "Aucun produit sélectionné.", "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    try {

                        // Appel méthode controller d'ajout d'une commande.
                        controller.addOrder(client, produitsSelectionnes, quantites);

                        JOptionPane.showMessageDialog(frame, "Commande crée !");
                        frame.dispose();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame,
                                "Veuillez entrer des informations valides.", "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }

            });

            panel.add(clientComboBox);
            panel.add(Box.createVerticalStrut(10));
            panel.add(new JLabel("Produits :"));
            panel.add(new JScrollPane(productPanel));
            panel.add(Box.createVerticalStrut(10));
            panel.add(submitBtn);
            frame.getContentPane().add(panel);
            frame.setVisible(true);
        });

        showBtn.addActionListener(e -> {
            int selectedIndex = orderList.getSelectedIndex();

            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner une commande à afficher.");
                return;
            }

            List<Order> orders = controller.loadOrders();
            Order order = orders.get(selectedIndex);

            // Client
            ClientController clientController = new ClientController();
            Client client = clientController.getClientById(order.getClientId());
            String clientName = client != null ? client.getName() : "Inconnu";

            // Produits
            ProductController productController = new ProductController();
            StringBuilder productsDetails = new StringBuilder();
            List<String> productIds = order.getProductIds();
            List<Integer> quantities = order.getQuantities();
            for (int i = 0; i < productIds.size(); i++) {
                Product p = productController.getProductById(productIds.get(i));
                String productName = (p != null) ? p.getName() : "Inconnu";
                int qte = (quantities != null && i < quantities.size()) ? quantities.get(i) : 0;
                productsDetails.append("- ").append(productName).append(" (x").append(qte).append(")\n");
            }

            String message = "Client : " + clientName +
                    "\nDate d'achat : " + order.getPurchaseDate() +
                    "\nDate de livraison : " + order.getDeliveryDate() +
                    "\nStatut : " + order.status +
                    "\n\nProduits :\n" + productsDetails.toString();

            JOptionPane.showMessageDialog(this, message, "Détails de la commande #" + order.getOrderNumber(),
                    JOptionPane.INFORMATION_MESSAGE);
        });

        statusBtn.addActionListener(e -> {

            int selectedIndex = orderList.getSelectedIndex();

            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner une commande à modifier.");
                return;
            }

            List<Order> orders = controller.loadOrders();
            Order order = orders.get(selectedIndex);

            String[] statusOptions = { "A expédier", "En cours de livraison", "Livrée" };
            String currentStatus = order.status;

            JComboBox<String> statusComboBox = new JComboBox<>(statusOptions);
            statusComboBox.setSelectedItem(currentStatus);

            int result = JOptionPane.showConfirmDialog(
                    this,
                    statusComboBox,
                    "Sélectionnez le nouveau statut",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String newStatus = (String) statusComboBox.getSelectedItem();
                if (!newStatus.equals(currentStatus)) {
                    controller.updateOrderStatus(order.getOrderNumber(), newStatus, billController, clientController);
                    JOptionPane.showMessageDialog(this, "Statut mis à jour !");

                } else {
                    JOptionPane.showMessageDialog(this, "Le statut n'a pas changé.");
                }
            }
        });
    }
}