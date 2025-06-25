package src.views;

import src.controllers.BillController;
import src.models.*;
import javax.swing.*;
import java.awt.*;

public class BillView extends JPanel implements ModelObserver {
    private StockModel model;
    private BillController controller;
    private DefaultListModel<String> listModel;
    private JList<String> billList;

    public BillView(StockModel model, BillController controller) {
        this.model = model;
        this.controller = controller;
        initializeUI();
        refreshList();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        billList = new JList<>(listModel);

        JButton updateStatusBtn = new JButton("Modifier le statut");
        JButton viewDetailsBtn = new JButton("Voir les détails");
        JButton deleteBtn = new JButton("Supprimer la facture");

        updateStatusBtn.addActionListener(e -> updateBillStatus());
        viewDetailsBtn.addActionListener(e -> viewBillDetails());
        deleteBtn.addActionListener(e -> deleteBill());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(updateStatusBtn);
        buttonPanel.add(viewDetailsBtn);
        buttonPanel.add(deleteBtn);

        // Panel d'information
        JLabel infoLabel = new JLabel(
                "<html><i>Les factures sont créées automatiquement lorsqu'une commande est marquée comme 'Livrée'.</i></html>");
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(infoLabel, BorderLayout.NORTH);
        add(new JScrollPane(billList), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateBillStatus() {
        int index = billList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une facture à modifier.");
            return;
        }

        String[] statuses = { "Non payé", "Payé", "En attente", "Annulé" };
        String newStatus = (String) JOptionPane.showInputDialog(this,
                "Nouveau statut :", "Modifier le statut",
                JOptionPane.QUESTION_MESSAGE, null, statuses, statuses[0]);

        if (newStatus != null) {
            controller.updateBillStatus(index, newStatus);
        }
    }

    private void viewBillDetails() {
        int index = billList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une facture à consulter.");
            return;
        }

        Bill bill = model.getBill(index);
        if (bill == null)
            return;

        // Récupérer les détails de la commande associée
        Order order = model.getOrders().stream()
                .filter(o -> o.id.equals(bill.orderId))
                .findFirst().orElse(null);

        if (order == null) {
            JOptionPane.showMessageDialog(this, "Commande associée introuvable.");
            return;
        }

        Client client = model.getClientById(order.clientId);

        StringBuilder details = new StringBuilder();
        details.append("=== FACTURE ===\n\n");
        details.append("Date de facture : ").append(bill.date).append("\n");
        details.append("Statut : ").append(bill.status).append("\n\n");
        details.append("Commande n°").append(order.orderNumber).append("\n");
        details.append("Client : ").append(client != null ? client.name : "Inconnu").append("\n");
        details.append("Email : ").append(client != null ? client.email : "Inconnu").append("\n");
        details.append("Adresse : ").append(client != null ? client.address : "Inconnu").append("\n");
        details.append("Date de commande : ").append(order.date).append("\n\n");
        details.append("Produits facturés :\n");

        double totalHT = 0.0;
        double TVA = 20;

        // Compter les occurrences de chaque produit
        java.util.Map<String, Long> productCount = order.productIds.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        id -> id, java.util.stream.Collectors.counting()));

        for (java.util.Map.Entry<String, Long> entry : productCount.entrySet()) {
            Product product = model.getProducts().stream()
                    .filter(p -> p.id.equals(entry.getKey()))
                    .findFirst().orElse(null);

            if (product != null) {
                double subtotal = product.price * entry.getValue();
                totalHT += subtotal;

                details.append("- ").append(product.name)
                        .append(" (x").append(entry.getValue()).append(")")
                        .append(" - ").append(String.format("%.2f €", product.price)).append(" /unité")
                        .append(" => ").append(String.format("%.2f €", subtotal)).append("\n");
            }
        }

        details.append("\n");
        details.append("Prix total (HT) : ").append(String.format("%.2f", totalHT)).append("€\n");
        details.append("TVA : ").append(TVA).append("%\n");
        details.append("Prix total (TTC) : ").append(String.format("%.2f", totalHT * 1.2)).append("€\n");

        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Détails de la facture",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteBill() {
        int index = billList.getSelectedIndex();
        if (index >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Êtes-vous sûr de vouloir supprimer cette facture ?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                controller.deleteBill(index);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Sélectionnez une facture à supprimer.");
        }
    }

    private void refreshList() {
        SwingUtilities.invokeLater(() -> {
            listModel.clear();
            for (Bill bill : model.getBills()) {
                // Récupérer les informations de la commande associée
                Order order = model.getOrders().stream()
                        .filter(o -> o.id.equals(bill.orderId))
                        .findFirst().orElse(null);

                String orderNumber = order != null ? order.orderNumber : "N/A";
                Client client = order != null ? model.getClientById(order.clientId) : null;
                String clientName = client != null ? client.name : "Client inconnu";

                listModel.addElement(String.format("Commande N°%s - %s - %s - %s",
                        orderNumber, clientName, bill.date, bill.status));
            }
        });
    }

    @Override
    public void onModelChanged(String eventType) {
        if (eventType.startsWith("BILL_") || eventType.startsWith("ORDER_")) {
            refreshList();
        }
    }
}