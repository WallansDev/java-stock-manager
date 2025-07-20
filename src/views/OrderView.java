package src.views;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.*;
import java.awt.BorderLayout;
import java.util.*;

import src.controllers.*;
import src.models.*;

public class OrderView extends JPanel {
    private JList<Order> orderList;
    private DefaultListModel<Order> listModel;

    private OrderController controller;

    public OrderView(OrderController controller) {
        this.controller = controller;
        initializeUI();

        controller.subscribeToClienChange(orders -> {
            listModel.clear();
            for (Order o : orders) {
                listModel.addElement(o);
            }
        });

        controller.loadOrders();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        orderList = new JList<>(listModel);

        JButton addBtn = new JButton("Ajouter une commande");
        JButton updateBtn = new JButton("Modifier une commande");
        JButton deleteBtn = new JButton("Supprimer une commande");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);

        add(new JScrollPane(orderList), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> {
            JFrame windowParent = (JFrame) SwingUtilities.getWindowAncestor(this);

            final JDialog frame = new JDialog(windowParent, "Ajouter une commande", true);
            frame.setSize(500, 300);
            frame.setLocationRelativeTo(null);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            ClientController clientController = new ClientController();
            List<Client> clients = clientController.loadClients();
            ProductController productController = new ProductController();
            List<Product> products = productController.loadProducts();

            JComboBox<Client> clientComboBox = new JComboBox<>();

            // Vide d'abord le combo
            clientComboBox.removeAllItems();

            // Remplit la combo avec les clients
            for (Client client : clients) {
                clientComboBox.addItem(client);
            }

            for (Product product : products) {
                // productComboBox.addItem(product);

            }

            JButton submitBtn = new JButton("Ajouter");

            submitBtn.addActionListener(ev -> {
                Client client = (Client) clientComboBox.getSelectedItem();
                System.out.println(client.address);
                // String name = nameField.getText().trim();
                // String email = emailField.getText().trim();
                // String address = addressField.getText().trim();
                // String phone = phoneField.getText().trim();

                // try {
                // // Appel méthode controller d'ajout d'un client.
                // controller.addClient(UUID.randomUUID().toString(), name, email, address,
                // phone);

                // JOptionPane.showMessageDialog(frame, "Client ajouté !");
                // frame.dispose();
                // } catch (NumberFormatException ex) {
                // JOptionPane.showMessageDialog(frame,
                // "Veuillez entrer des informations valides.", "Erreur",
                // JOptionPane.ERROR_MESSAGE);
                // }
            });

            panel.add(clientComboBox);
            panel.add(Box.createVerticalStrut(10));
            panel.add(submitBtn);

            frame.getContentPane().add(panel);
            frame.setVisible(true);
        });
    }
}