package src.views;

import src.controllers.DeliveryController;
import src.models.*;
import javax.swing.*;
import java.awt.*;

public class DeliveryView extends JPanel implements ModelObserver {
    private StockModel model;
    private DeliveryController controller;
    private DefaultListModel<String> listModel;
    private JList<String> deliveryList;

    public DeliveryView(StockModel model, DeliveryController controller) {
        this.model = model;
        this.controller = controller;
        initializeUI();
        refreshList();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        deliveryList = new JList<>(listModel);

        JButton viewDetailsBtn = new JButton("Voir les détails");
        JButton deleteBtn = new JButton("Supprimer le bon");

        viewDetailsBtn.addActionListener(e -> viewDeliveryDetails());
        deleteBtn.addActionListener(e -> deleteDelivery());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(viewDetailsBtn);
        buttonPanel.add(deleteBtn);

        // Panel d'information
        JLabel infoLabel = new JLabel(
                "<html><i>Les bons de livraison sont créés automatiquement lors de la création d'une commande.</i></html>");
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(infoLabel, BorderLayout.NORTH);
        add(new JScrollPane(deliveryList), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void viewDeliveryDetails() {
        int index = deliveryList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un bon de livraison à consulter.");
            return;
        }

        String details = controller.getDeliveryDetails(index);
        if (details != null) {
            JTextArea textArea = new JTextArea(details);
            textArea.setEditable(false);
            textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 400));

            JOptionPane.showMessageDialog(this, scrollPane, "Détails du bon de livraison",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Impossible de récupérer les détails du bon de livraison.");
        }
    }

    private void deleteDelivery() {
        int index = deliveryList.getSelectedIndex();
        if (index >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Êtes-vous sûr de vouloir supprimer ce bon de livraison ?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                controller.deleteDelivery(index);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Sélectionnez un bon de livraison à supprimer.");
        }
    }

    private void refreshList() {
        SwingUtilities.invokeLater(() -> {
            listModel.clear();
            for (DeliveryNote delivery : model.getDeliveries()) {
                // Récupérer les informations de la commande associée
                Order order = model.getOrders().stream()
                        .filter(o -> o.id.equals(delivery.orderId))
                        .findFirst().orElse(null);

                String orderNumber = order != null ? order.orderNumber : "N/A";

                listModel.addElement(String.format("Commande N°%s - %s - %s - %s",
                        orderNumber, delivery.clientName, delivery.date, delivery.status));
            }
        });
    }

    @Override
    public void onModelChanged(String eventType) {
        if (eventType.startsWith("DELIVERY_") || eventType.startsWith("ORDER_")) {
            refreshList();
        }
    }
}