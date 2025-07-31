package views;

import javax.swing.*;
import java.awt.*;
import controllers.*;
import models.*;
import java.util.List;

/**
 * Vue Swing permettant d'afficher et de gérer la liste des factures (Bill).
 * Permet de marquer une facture comme payée et d'afficher les détails d'une
 * facture.
 */
public class BillView extends JPanel {
    /**
     * Liste graphique des factures.
     */
    private JList<Bill> billList;
    /**
     * Modèle de données pour la liste des factures.
     */
    private DefaultListModel<Bill> billListModel;
    /**
     * Contrôleur de factures associé à la vue.
     */
    private BillController billController;

    /**
     * Construit la vue des factures et initialise l'interface graphique.
     *
     * @param billController Contrôleur de factures à utiliser
     */
    public BillView(BillController billController) {
        this.billController = billController;
        initializeUI();

        billController.subscribeToBillChange(bills -> {
            billListModel.clear();
            for (Bill b : bills) {
                billListModel.addElement(b);
            }
        });

        billController.loadBills();
    }

    /**
     * Initialise l'interface graphique de la vue des factures.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());

        billListModel = new DefaultListModel<>();
        billList = new JList<>(billListModel);

        JButton markPaidBtn = new JButton("Marquer comme payé");
        JButton showBtn = new JButton("Détails de la facture");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(markPaidBtn);
        buttonPanel.add(showBtn);

        add(new JScrollPane(billList), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshBillList();

        markPaidBtn.addActionListener(e -> {
            int idx = billList.getSelectedIndex();
            if (idx == -1) {
                JOptionPane.showMessageDialog(this, "Choisir une facture à marquer comme payée.");
                return;
            }
            Bill bill = billListModel.getElementAt(idx);
            billController.markAsPaid(bill.getBillId());
            refreshBillList();
        });

        showBtn.addActionListener(e -> {
            int idx = billList.getSelectedIndex();
            if (idx == -1) {
                JOptionPane.showMessageDialog(this, "Choisir une facture à afficher.");
                return;
            }
            Bill bill = billListModel.getElementAt(idx);
            Order order = bill.getOrder();
            Client client = bill.getClient();
            StringBuilder details = new StringBuilder();
            details.append("\nNuméro de commande : #").append(order.getOrderNumber())
                    .append("\nClient : ").append(client.getName())
                    .append("\nDate de commande : ").append(order.getPurchaseDate())
                    .append("\nDate de livraison : ").append(order.getDeliveryDate())
                    .append("\nStatut de paiement : ").append(bill.isPaid() ? "Payé" : "Non payé")
                    .append("\n\nProduits :\n");

            ProductController productController = new ProductController();
            List<String> productIds = order.getProductIds();
            List<Integer> quantities = order.getQuantities();
            double totalHT = 0.0;
            for (int i = 0; i < productIds.size(); i++) {
                Product p = productController.getProductById(productIds.get(i));
                String productName = (p != null) ? p.getName() : "Inconnu";
                int qte = (quantities != null && i < quantities.size()) ? quantities.get(i) : 0;
                double prix = (p != null) ? p.getPrice() : 0.0f;
                String prixFormate = String.format("%.2f", prix);
                details.append("- ")
                        .append(productName)
                        .append(" (x").append(qte).append(") : ")
                        .append(prixFormate).append("€/unité\n");
                totalHT += prix * qte;
            }
            int TVA = 20;
            double frais_de_livraison = 4.99;
            double totalTTC = totalHT * (1 + TVA / 100.0) + frais_de_livraison;
            String totalHTStr = String.format("%.2f", totalHT);
            String totalTTCStr = String.format("%.2f", totalTTC);

            details.append("\nTotal HT : ").append(totalHTStr).append(" €")
                    .append("\n\nTVA : ").append(TVA).append("%")
                    .append("\nFrais de livraison : ").append(frais_de_livraison).append(" €")
                    .append("\n\nTotal TTC : ").append(totalTTCStr).append(" €");

            JOptionPane.showMessageDialog(this, details.toString(), "Facture de la commande #" + order.getOrderNumber(),
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }

    /**
     * Rafraîchit la liste graphique des factures à partir du contrôleur.
     */
    private void refreshBillList() {
        billListModel.clear();
        for (Bill b : billController.loadBills()) {
            billListModel.addElement(b);
        }
    }
}