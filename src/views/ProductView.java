package src.views;

import src.*;
import src.controllers.ProductController;
import src.models.*;
import javax.swing.*;
import java.awt.*;

public class ProductView extends JPanel implements ModelObserver {
    private StockModel model;
    private ProductController controller;
    private DefaultListModel<String> listModel;
    private JList<String> productList;

    public ProductView(StockModel model, ProductController controller) {
        this.model = model;
        this.controller = controller;
        initializeUI();
        refreshList();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        productList = new JList<>(listModel);

        JButton addBtn = new JButton("Ajouter un produit");
        JButton updateBtn = new JButton("Modifier le produit");
        JButton deleteBtn = new JButton("Supprimer le produit");

        addBtn.addActionListener(e -> addProduct());
        updateBtn.addActionListener(e -> updateProduct());
        deleteBtn.addActionListener(e -> deleteProduct());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);

        add(new JScrollPane(productList), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addProduct() {
        try {
            String name = JOptionPane.showInputDialog(this, "Nom du produit :");
            if (name == null)
                return;

            String qtyStr = JOptionPane.showInputDialog(this, "Quantité :");
            if (qtyStr == null)
                return;

            String priceStr = JOptionPane.showInputDialog(this, "Prix (HT) :");
            if (priceStr == null)
                return;

            int quantity = Integer.parseInt(qtyStr);
            float price = Float.parseFloat(priceStr);

            controller.addProduct(name, quantity, price);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valeurs numériques invalides !", "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateProduct() {
        int index = productList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un produit à modifier.");
            return;
        }

        try {
            Product product = model.getProduct(index);

            String name = JOptionPane.showInputDialog(this, "Nom du produit :", product.name);
            if (name == null)
                return;

            String qtyStr = JOptionPane.showInputDialog(this, "Quantité :", product.quantity);
            if (qtyStr == null)
                return;

            String priceStr = JOptionPane.showInputDialog(this, "Prix (HT) :", product.price);
            if (priceStr == null)
                return;

            int quantity = Integer.parseInt(qtyStr);
            float price = Float.parseFloat(priceStr);

            controller.updateProduct(index, name, quantity, price);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valeurs numériques invalides !", "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProduct() {
        int index = productList.getSelectedIndex();
        if (index >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Êtes-vous sûr de vouloir supprimer ce produit ?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                controller.deleteProduct(index);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Sélectionnez un produit à supprimer.");
        }
    }

    private void refreshList() {
        SwingUtilities.invokeLater(() -> {
            listModel.clear();
            for (Product p : model.getProducts()) {
                listModel.addElement(String.format("Nom : %s - Stock : x%d - Prix unitaire HT %.2f€",
                        p.name, p.quantity, p.price));
            }
        });
    }

    @Override
    public void onModelChanged(String eventType) {
        if (eventType.startsWith("PRODUCT_")) {
            refreshList();
        }
    }
}
