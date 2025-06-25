
package src.views;

import src.controllers.OrderController;
import src.models.*;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class OrderView extends JPanel implements ModelObserver {
    private StockModel model;
    private OrderController controller;
    private DefaultListModel<String> listModel;
    private JList<String> orderList;

    public OrderView(StockModel model, OrderController controller) {
        this.model = model;
        this.controller = controller;
        initializeUI();
        refreshList();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        orderList = new JList<>(listModel);

        JButton addBtn = new JButton("Créer une commande");
        JButton updateStatusBtn = new JButton("Modifier le statut");
        JButton deleteBtn = new JButton("Supprimer la commande");

        addBtn.addActionListener(e -> createOrder());
        updateStatusBtn.addActionListener(e -> updateOrderStatus());
        deleteBtn.addActionListener(e -> deleteOrder());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addBtn);
        buttonPanel.add(updateStatusBtn);
        buttonPanel.add(deleteBtn);

        add(new JScrollPane(orderList), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void createOrder() {
        try {
            // Sélection du client
            java.util.List<Client> clients = model.getClients();
            if (clients.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Aucun client disponible. Créez d'abord des clients.");
                return;
            }

            String[] clientNames = clients.stream()
                    .map(c -> c.name + " (" + c.email + ")")
                    .toArray(String[]::new);

            String selectedClient = (String) JOptionPane.showInputDialog(this,
                    "Sélectionnez un client :", "Créer une commande",
                    JOptionPane.QUESTION_MESSAGE, null, clientNames, clientNames[0]);

            if (selectedClient == null)
                return;

            int clientIndex = java.util.Arrays.asList(clientNames).indexOf(selectedClient);
            String clientId = clients.get(clientIndex).id;

            // Sélection des produits
            java.util.List<Product> products = model.getProducts();
            if (products.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Aucun produit disponible.");
                return;
            }

            Map<String, Integer> productQuantities = new HashMap<>();

            // Interface pour sélectionner les produits et quantités
            JPanel productPanel = new JPanel();
            productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.Y_AXIS));

            JCheckBox[] productCheckboxes = new JCheckBox[products.size()];
            JSpinner[] quantitySpinners = new JSpinner[products.size()];

            for (int i = 0; i < products.size(); i++) {
                Product p = products.get(i);
                JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));

                productCheckboxes[i] = new JCheckBox(String.format("%s (Stock: %d, Prix: %.2f€)",
                        p.name, p.quantity, p.price));
                quantitySpinners[i] = new JSpinner(new SpinnerNumberModel(1, 1, p.quantity, 1));
                quantitySpinners[i].setEnabled(false);

                final int index = i;
                productCheckboxes[i].addActionListener(
                        e -> quantitySpinners[index].setEnabled(productCheckboxes[index].isSelected()));

                row.add(productCheckboxes[i]);
                row.add(new JLabel("Quantité:"));
                row.add(quantitySpinners[i]);

                productPanel.add(row);
            }

            JScrollPane scrollPane = new JScrollPane(productPanel);
            scrollPane.setPreferredSize(new Dimension(500, 300));

            int result = JOptionPane.showConfirmDialog(this, scrollPane,
                    "Sélectionnez les produits", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                for (int i = 0; i < products.size(); i++) {
                    if (productCheckboxes[i].isSelected()) {
                        productQuantities.put(products.get(i).id,
                                (Integer) quantitySpinners[i].getValue());
                    }
                }

                if (productQuantities.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Sélectionnez au moins un produit.");
                    return;
                }

                controller.createOrder(clientId, productQuantities);
                JOptionPane.showMessageDialog(this, "Commande créée avec succès !");
            }

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateOrderStatus() {
        int index = orderList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une commande à modifier.");
            return;
        }

        String[] statuses = { "À expédier", "En cours d'expédition", "Livré", "Annulé" };
        String newStatus = (String) JOptionPane.showInputDialog(this,
                "Nouveau statut :", "Modifier le statut",
                JOptionPane.QUESTION_MESSAGE, null, statuses, statuses[0]);

        if (newStatus != null) {
            controller.updateOrderStatus(index, newStatus);
        }
    }

    private void deleteOrder() {
        int index = orderList.getSelectedIndex();
        if (index >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Êtes-vous sûr de vouloir supprimer cette commande ?\n" +
                            "Les produits seront remis en stock.",
                    "Confirmation", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                controller.deleteOrder(index);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Sélectionnez une commande à supprimer.");
        }
    }

    private void refreshList() {
        SwingUtilities.invokeLater(() -> {
            listModel.clear();
            for (Order order : model.getOrders()) {
                Client client = model.getClientById(order.clientId);
                String clientName = client != null ? client.name : "Client inconnu";

                listModel.addElement(String.format("N°%s - %s - %s - %d produits - %s",
                        order.orderNumber, clientName, order.date,
                        order.productIds.size(), order.status));
            }
        });
    }

    @Override
    public void onModelChanged(String eventType) {
        if (eventType.startsWith("ORDER_") || eventType.startsWith("CLIENT_")) {
            refreshList();
        }
    }
}