package views;

import controllers.ClientController;
import models.Client;
import java.util.UUID;
import javax.swing.*;
import java.awt.BorderLayout;

/**
 * Vue Swing permettant d'afficher et de gérer la liste des clients.
 * Permet d'ajouter, modifier et supprimer des clients via une interface
 * graphique.
 */
public class ClientView extends JPanel {
    /**
     * Liste graphique des clients.
     */
    private JList<Client> clientList;
    /**
     * Modèle de données pour la liste des clients.
     */
    private DefaultListModel<Client> listModel;

    /**
     * Contrôleur de clients associé à la vue.
     */
    private ClientController controller;

    /**
     * Construit la vue des clients et initialise l'interface graphique.
     *
     * @param controller Contrôleur de clients à utiliser
     */
    public ClientView(ClientController controller) {
        this.controller = controller;
        initializeUI();

        controller.subscribeToClienChange(clients -> {
            listModel.clear();
            for (Client c : clients) {
                listModel.addElement(c);
            }
        });

        controller.loadClients();
    }

    /**
     * Initialise l'interface graphique de la vue des clients.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        clientList = new JList<>(listModel);

        JButton addBtn = new JButton("Ajouter un client");
        JButton updateBtn = new JButton("Modifier le client");
        JButton deleteBtn = new JButton("Supprimer le client");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);

        add(new JScrollPane(clientList), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> {
            JFrame windowParent = (JFrame) SwingUtilities.getWindowAncestor(this);

            final JDialog frame = new JDialog(windowParent, "Ajouter un client", true);
            frame.setSize(500, 300);
            frame.setLocationRelativeTo(null);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JLabel nameLabel = new JLabel("Nom du client:");
            JTextField nameField = new JTextField(20);

            JLabel emailLabel = new JLabel("Email de contact :");
            JTextField emailField = new JTextField(20);

            JLabel addressLabel = new JLabel("Adresse de livraison / facturation :");
            JTextField addressField = new JTextField(20);

            JLabel phoneLabel = new JLabel("Numéro de téléphone :");
            JTextField phoneField = new JTextField(20);

            JButton submitBtn = new JButton("Ajouter");

            submitBtn.addActionListener(ev -> {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String address = addressField.getText().trim();
                String phone = phoneField.getText().trim();

                try {
                    // Appel méthode controller d'ajout d'un client.
                    controller.addClient(UUID.randomUUID().toString(), name, email, address, phone);

                    JOptionPane.showMessageDialog(frame, "Client ajouté !");
                    frame.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame,
                            "Veuillez entrer des informations valides.", "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            });

            panel.add(nameLabel);
            panel.add(nameField);
            panel.add(Box.createVerticalStrut(10));
            panel.add(emailLabel);
            panel.add(emailField);
            panel.add(Box.createVerticalStrut(10));
            panel.add(addressLabel);
            panel.add(addressField);
            panel.add(Box.createVerticalStrut(10));
            panel.add(phoneLabel);
            panel.add(phoneField);
            panel.add(submitBtn);

            frame.getContentPane().add(panel);
            frame.setVisible(true);
        });

        updateBtn.addActionListener(e -> {
            int index = clientList.getSelectedIndex();

            Client client = controller.getClient(index);

            JFrame windowParent = (JFrame) SwingUtilities.getWindowAncestor(this);

            final JDialog frame = new JDialog(windowParent, "Modification client : " + client.name, true);
            frame.setSize(500, 300);
            frame.setLocationRelativeTo(null);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JLabel nameLabel = new JLabel("Nom du client:");
            JTextField nameField = new JTextField(client.getName());

            JLabel emailLabel = new JLabel("Email de contact :");
            JTextField emailField = new JTextField(client.getEmail());

            JLabel addressLabel = new JLabel("Adresse de livraison / facturation :");
            JTextField addressField = new JTextField(client.getAddress());

            JLabel phoneLabel = new JLabel("Numéro de téléphone :");
            JTextField phoneField = new JTextField(client.getPhone());

            JButton submitBtn = new JButton("Ajouter");

            submitBtn.addActionListener(ev -> {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String address = addressField.getText().trim();
                String phone = phoneField.getText().trim();

                try {
                    // Appel méthode controller de modification d'un client.
                    controller.updateClient(index, name, email, address, phone);

                    JOptionPane.showMessageDialog(frame, "Client modifié !");
                    frame.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame,
                            "Veuillez entrer des informations valides.", "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            });

            panel.add(nameLabel);
            panel.add(nameField);
            panel.add(Box.createVerticalStrut(10));
            panel.add(emailLabel);
            panel.add(emailField);
            panel.add(Box.createVerticalStrut(10));
            panel.add(addressLabel);
            panel.add(addressField);
            panel.add(Box.createVerticalStrut(10));
            panel.add(phoneLabel);
            panel.add(phoneField);
            panel.add(submitBtn);

            frame.getContentPane().add(panel);
            frame.setVisible(true);
        });

        deleteBtn.addActionListener(e -> {
            int index = clientList.getSelectedIndex();

            JFrame windowParent = (JFrame) SwingUtilities.getWindowAncestor(this);

            final JDialog frame = new JDialog(windowParent, true);

            controller.deleteProduct(index);

            JOptionPane.showMessageDialog(frame, "Client supprimé !");

        });

    }
}
