package src.views;

import src.controllers.ProductController;
import src.models.Product;
import java.util.UUID;

import javax.management.StringValueExp;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.UUID;

public class ProductView extends JPanel {
    private JList<Product> productList;
    private DefaultListModel<Product> listModel;

    private ProductController controller;

    public ProductView(ProductController controller) {
        this.controller = controller;
        initializeUI();

        controller.subscribeToProductChange(products -> {
            listModel.clear();
            for (Product p : products) {
                listModel.addElement(p);
            }
        });

        controller.loadProducts();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        productList = new JList<>(listModel);

        JButton addBtn = new JButton("Ajouter un produit");
        JButton updateBtn = new JButton("Modifier le produit");
        JButton deleteBtn = new JButton("Supprimer le produit");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);

        add(new JScrollPane(productList), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> {
            JFrame windowParent = (JFrame) SwingUtilities.getWindowAncestor(this);

            final JDialog frame = new JDialog(windowParent, "Ajouter un produit", true);
            frame.setSize(500, 300);
            frame.setLocationRelativeTo(null);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JLabel nameLabel = new JLabel("Nom du produit:");
            JTextField nameField = new JTextField(20);

            JLabel priceLabel = new JLabel("Prix (€):");
            JTextField priceField = new JTextField(20);

            JLabel stockLabel = new JLabel("Quantité en stock:");
            JTextField stockField = new JTextField(20);

            JButton submitBtn = new JButton("Ajouter");

            submitBtn.addActionListener(ev -> {
                String name = nameField.getText().trim();
                String priceText = priceField.getText().trim();
                String stockText = stockField.getText().trim();

                try {
                    float price = Float.parseFloat(priceText);
                    int stock = Integer.parseInt(stockText);

                    // Appel méthode controller d'ajout d'un produit.
                    controller.addProduct(UUID.randomUUID().toString(), name, stock, price);

                    JOptionPane.showMessageDialog(frame, "Produit ajouté !");
                    frame.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame,
                            "Veuillez entrer un prix valide (float) et une quantité valide (int).", "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            });

            panel.add(nameLabel);
            panel.add(nameField);
            panel.add(Box.createVerticalStrut(10));
            panel.add(priceLabel);
            panel.add(priceField);
            panel.add(Box.createVerticalStrut(10));
            panel.add(stockLabel);
            panel.add(stockField);
            panel.add(Box.createVerticalStrut(20));
            panel.add(submitBtn);

            frame.getContentPane().add(panel);
            frame.setVisible(true);
        });

        updateBtn.addActionListener(e -> {
            int index = productList.getSelectedIndex();

            Product product = controller.getProduct(index);

            JFrame windowParent = (JFrame) SwingUtilities.getWindowAncestor(this);

            final JDialog frame = new JDialog(windowParent, "Modifier un produit", true);
            frame.setSize(500, 300);
            frame.setLocationRelativeTo(null);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JLabel nameLabel = new JLabel("Nom du produit:");
            JTextField nameField = new JTextField(product.name);

            JLabel priceLabel = new JLabel("Prix (€):");
            JTextField priceField = new JTextField(String.format(Locale.US, "%.2f", product.price));

            JLabel stockLabel = new JLabel("Quantité en stock:");
            JTextField stockField = new JTextField(String.valueOf(product.quantity));

            JButton submitBtn = new JButton("Ajouter");

            submitBtn.addActionListener(ev -> {
                String name = nameField.getText().trim();
                String priceText = priceField.getText().trim();
                String stockText = stockField.getText().trim();

                System.out.println( name + " - " + stockText + " - "
                        + priceText);
                try {
                    float price = Float.parseFloat(priceText);
                    int stock = Integer.parseInt(stockText);
                    System.out.println(stock);

                    // Appel méthode controller de modification d'un produit.
                    controller.updateProduct(index, name, stock, price);

                    JOptionPane.showMessageDialog(frame, "Produit modifié !");
                    frame.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame,
                            "Veuillez entrer un prix valide (float) et une quantité valide (int).",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            });

            panel.add(nameLabel);
            panel.add(nameField);
            panel.add(Box.createVerticalStrut(10));
            panel.add(priceLabel);
            panel.add(priceField);
            panel.add(Box.createVerticalStrut(10));
            panel.add(stockLabel);
            panel.add(stockField);
            panel.add(Box.createVerticalStrut(20));
            panel.add(submitBtn);

            frame.getContentPane().add(panel);
            frame.setVisible(true);
        });

        deleteBtn.addActionListener(e -> {
            int index = productList.getSelectedIndex();

            // Appel de la méthode controller de suppression d'un produit.
            controller.deleteProduct(index);
        });
    }
}
