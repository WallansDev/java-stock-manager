package src.views;

import src.controllers.ClientController;
import src.models.*;
import javax.swing.*;
import java.awt.*;

public class ClientView extends JPanel implements ModelObserver {
    private StockModel model;
    private ClientController controller;
    private DefaultListModel<String> listModel;
    private JList<String> clientList;

    public ClientView(StockModel model, ClientController controller) {
        this.model = model;
        this.controller = controller;
        initializeUI();
        refreshList();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        clientList = new JList<>(listModel);

        JButton addBtn = new JButton("Ajouter un client");
        JButton updateBtn = new JButton("Modifier le client");
        JButton deleteBtn = new JButton("Supprimer le client");

        addBtn.addActionListener(e -> addClient());
        updateBtn.addActionListener(e -> updateClient());
        deleteBtn.addActionListener(e -> deleteClient());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);

        add(new JScrollPane(clientList), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addClient() {
        try {
            JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
            JTextField nameField = new JTextField();
            JTextField emailField = new JTextField();
            JTextField phoneField = new JTextField();
            JTextField addressField = new JTextField();

            panel.add(new JLabel("Nom :"));
            panel.add(nameField);
            panel.add(new JLabel("Email :"));
            panel.add(emailField);
            panel.add(new JLabel("Téléphone :"));
            panel.add(phoneField);
            panel.add(new JLabel("Adresse :"));
            panel.add(addressField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Ajouter un client",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                controller.addClient(
                        nameField.getText(),
                        emailField.getText(),
                        phoneField.getText(),
                        addressField.getText());
            }

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateClient() {
        int index = clientList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un client à modifier.");
            return;
        }

        try {
            Client client = model.getClient(index);

            JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
            JTextField nameField = new JTextField(client.name);
            JTextField emailField = new JTextField(client.email);
            JTextField phoneField = new JTextField(client.phone);
            JTextField addressField = new JTextField(client.address);

            panel.add(new JLabel("Nom :"));
            panel.add(nameField);
            panel.add(new JLabel("Email :"));
            panel.add(emailField);
            panel.add(new JLabel("Téléphone :"));
            panel.add(phoneField);
            panel.add(new JLabel("Adresse :"));
            panel.add(addressField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Modifier le client",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                controller.updateClient(
                        index,
                        nameField.getText(),
                        emailField.getText(),
                        phoneField.getText(),
                        addressField.getText());
            }

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteClient() {
        int index = clientList.getSelectedIndex();
        if (index >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Êtes-vous sûr de vouloir supprimer ce client ?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                controller.deleteClient(index);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Sélectionnez un client à supprimer.");
        }
    }

    private void refreshList() {
        SwingUtilities.invokeLater(() -> {
            listModel.clear();
            for (Client c : model.getClients()) {
                listModel.addElement(String.format("%s - %s - %s - %s",
                        c.name, c.email, c.phone, c.address));
            }
        });
    }

    @Override
    public void onModelChanged(String eventType) {
        if (eventType.startsWith("CLIENT_")) {
            refreshList();
        }
    }
}