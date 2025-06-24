package src;

// MainFrame.java
import javax.swing.*;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class MainFrame extends JFrame {
    private List<Product> products;
    private List<Client> clients;
    private List<Order> orders;
    private List<Bill> bills;
    private List<DeliveryNote> deliveries;
    private DefaultListModel<String> productListModel;
    private DefaultListModel<String> billModel;
    private DefaultListModel<String> deliveryListModel;

    public MainFrame() {
        setTitle("Gestion de Stock");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        products = DataManager.loadProducts();
        clients = DataManager.loadClients();
        orders = DataManager.loadOrders();
        bills = DataManager.loadBills();
        deliveries = DataManager.loadDeliveries();

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Produits", createProductPanel());
        tabs.addTab("Clients", createClientPanel());
        tabs.addTab("Commandes", createOrderPanel());
        tabs.addTab("Bon de livraisons", createDeliveryPanel());
        tabs.addTab("Factures", createBillPanel());

        add(tabs);

    }

    // Onglet Produits
    private JPanel createProductPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        productListModel = new DefaultListModel<>();
        JList<String> productList = new JList<>(productListModel);
        refreshProductList(productListModel);

        JButton addBtn = new JButton("Ajouter un produit");
        JButton updateStatusBtn = new JButton("Modifier le produit");
        JButton deleteBtn = new JButton("Supprimer le produit");

        addBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Nom du produit :");
            String qtyStr = JOptionPane.showInputDialog(this, "Quantité :");
            String priceStr = JOptionPane.showInputDialog(this, "Prix (HT) :");

            if (name != null && qtyStr != null && priceStr != null) {
                try {
                    int qty = Integer.parseInt(qtyStr);
                    float price = Float.parseFloat(priceStr);
                    products.add(new Product(UUID.randomUUID().toString(), name, qty, price));
                    DataManager.saveProducts(products);
                    refreshProductList(productListModel);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Produit invalide !");
                }
            }
        });

        updateStatusBtn.addActionListener(e -> {
            int index = productList.getSelectedIndex();
            if (index >= 0) {
                Product selectedProduct = products.get(index);
                String name = JOptionPane.showInputDialog(this, "Nom du produit :", selectedProduct.name);
                String qtyStr = JOptionPane.showInputDialog(this, "Quantité en stock (exclu les livraisons) :",
                        selectedProduct.quantity);
                String priceStr = JOptionPane.showInputDialog(this, "Prix unitaire HT", selectedProduct.price);

                if (name != null && qtyStr != null && priceStr != null) {
                    selectedProduct.name = name.trim();

                    int qty = Integer.parseInt(qtyStr);
                    selectedProduct.quantity = qty;

                    float price = Float.parseFloat(priceStr);
                    selectedProduct.price = price;

                    DataManager.saveClients(clients);
                    refreshProductList(productListModel);
                }
            }
        });

        deleteBtn.addActionListener(e -> {
            int index = productList.getSelectedIndex();
            if (index >= 0) {
                products.remove(index);
                DataManager.saveProducts(products);
                refreshProductList(productListModel);
            }
        });

        JPanel btnPanel = new JPanel();
        btnPanel.add(addBtn);
        btnPanel.add(updateStatusBtn);
        btnPanel.add(deleteBtn);

        panel.add(new JScrollPane(productList), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Onglet Clients
    private JPanel createClientPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> clientList = new JList<>(model);
        refreshClientList(model);

        JButton addBtn = new JButton("Ajouter un client");
        JButton updateStatusBtn = new JButton("Modifier le client");
        JButton deleteBtn = new JButton("Supprimer le client");

        addBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Nom du client :");
            String email = JOptionPane.showInputDialog(this, "Email :");
            String phone = JOptionPane.showInputDialog(this, "Téléphone (Ex: +33) :");
            String address = JOptionPane.showInputDialog(this, "Adresse postale :");

            if (name != null && email != null && phone != null) {
                clients.add(new Client(UUID.randomUUID().toString(), name, email, phone, address));
                DataManager.saveClients(clients);
                refreshClientList(model);
            }
        });

        updateStatusBtn.addActionListener(e -> {
            int index = clientList.getSelectedIndex();
            if (index >= 0) {
                Client selectedClient = clients.get(index);
                String name = JOptionPane.showInputDialog(this, "Nom du client :", selectedClient.name);
                String email = JOptionPane.showInputDialog(this, "Email :", selectedClient.email);
                String phone = JOptionPane.showInputDialog(this, "Téléphone (Ex: +33) :", selectedClient.phone);
                String address = JOptionPane.showInputDialog(this, "Adresse postale :", selectedClient.address);

                if (name != null && email != null && phone != null) {
                    selectedClient.name = name.trim();
                    selectedClient.email = email.trim();
                    selectedClient.phone = phone.trim();
                    selectedClient.address = address.trim();

                    DataManager.saveClients(clients);
                    refreshClientList(model);
                }
            }
        });

        deleteBtn.addActionListener(e -> {
            int index = clientList.getSelectedIndex();
            if (index >= 0) {
                clients.remove(index);
                DataManager.saveClients(clients);
                refreshClientList(model);
            }
        });

        JPanel btnPanel = new JPanel();
        btnPanel.add(addBtn);
        btnPanel.add(updateStatusBtn);
        btnPanel.add(deleteBtn);

        panel.add(new JScrollPane(clientList), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Onglet Commandes
    private JPanel createOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> orderList = new JList<>(model);
        refreshOrderList(model);

        JButton addBtn = new JButton("Créer une commande");
        JButton updateBtn = new JButton("Modifier la commande");
        JButton updateStatusBtn = new JButton("Modifier le statut");
        JButton deleteBtn = new JButton("Supprimer la commande");

        addBtn.addActionListener(e -> {
            if (clients.isEmpty() || products.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Il faut d'abord avoir des clients et des produits !");
                return;
            }

            // 1. Sélection du client
            String[] clientNames = clients.stream().map(c -> c.name).toArray(String[]::new);
            String selectedClientName = (String) JOptionPane.showInputDialog(this, "Sélectionner un client :", "Client",
                    JOptionPane.PLAIN_MESSAGE, null, clientNames, clientNames[0]);
            if (selectedClientName == null)
                return;

            String clientId = clients.stream().filter(c -> c.name.equals(selectedClientName)).findFirst().get().id;

            // 2. Sélection de plusieurs produits
            String[] productNames = products.stream().map(p -> p.name).toArray(String[]::new);
            JList<String> productList = new JList<>(productNames);
            productList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

            int result = JOptionPane.showConfirmDialog(this, new JScrollPane(productList), "Sélectionner les produits",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION)
                return;

            List<String> selectedProducts = productList.getSelectedValuesList();
            if (selectedProducts.isEmpty())
                return;

            // 3. Pour chaque produit sélectionné, demander une quantité
            List<String> productIds = new ArrayList<>();
            for (String productName : selectedProducts) {
                Product prod = products.stream().filter(p -> p.name.equals(productName)).findFirst().orElse(null);
                if (prod == null)
                    continue;

                String qtyStr = JOptionPane.showInputDialog(this, "Quantité pour le produit : " + prod.name);
                if (qtyStr == null)
                    continue;

                try {
                    int qty = Integer.parseInt(qtyStr);
                    if (qty <= 0) {
                        JOptionPane.showMessageDialog(this, "Quantité invalide pour " + prod.name);
                        return;
                    }
                    if (prod.quantity < qty) {
                        JOptionPane.showMessageDialog(this, "Stock insuffisant pour " + prod.name);
                        return;
                    }

                    // Déduire le stock
                    prod.quantity -= qty;

                    // Ajouter plusieurs fois l'ID dans la commande
                    for (int i = 0; i < qty; i++) {
                        productIds.add(prod.id);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Entrée invalide pour la quantité de " + prod.name);
                    return;
                }
            }

            if (productIds.isEmpty())
                return;

            // 4. Créer la commande
            String orderNumber = DataManager.generateNextOrderNumber(orders);

            Order order = new Order(
                    UUID.randomUUID().toString(),
                    orderNumber,
                    clientId,
                    productIds,
                    LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).toString(),
                    "À expédier");
            orders.add(order);

            Client clientObj = clients.stream()
                    .filter(c -> c.id.equals(clientId))
                    .findFirst()
                    .orElse(null);

            if (clientObj != null) {
                DeliveryNote note = new DeliveryNote(
                        UUID.randomUUID().toString(),
                        order.id,
                        LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        clientObj.name,
                        "À expédier");
                deliveries.add(note);
                DataManager.saveDeliveries(deliveries);
                refreshDeliveryList(deliveryListModel);
            }

            // 5. Sauvegarder
            DataManager.saveOrders(orders);
            DataManager.saveProducts(products);
            refreshOrderList(model);
            refreshProductList(productListModel);
        });

        deleteBtn.addActionListener(e -> {
            int index = orderList.getSelectedIndex();
            if (index >= 0) {
                Order orderToDelete = orders.get(index);

                // Retourne les produits en stock SI commande supprimée
                for (String prodId : orderToDelete.productIds) {
                    for (Product p : products) {
                        if (p.id.equals(prodId)) {
                            p.quantity++;
                            break;
                        }
                    }
                }

                orders.remove(index);

                DataManager.saveOrders(orders);
                DataManager.saveProducts(products);

                refreshOrderList(model);
                refreshProductList(productListModel);
            }
        });

        updateBtn.addActionListener(e -> {
            int index = orderList.getSelectedIndex();
            if (index < 0) {
                JOptionPane.showMessageDialog(this, "Sélectionnez une commande à modifier.");
                return;
            }

            Order order = orders.get(index);

            // 1. Modifier le client
            String[] clientNames = clients.stream().map(c -> c.name).toArray(String[]::new);
            Client currentClient = clients.stream().filter(c -> c.id.equals(order.clientId)).findFirst().orElse(null);
            String selectedClientName = (String) JOptionPane.showInputDialog(this,
                    "Sélectionner un client :", "Client",
                    JOptionPane.PLAIN_MESSAGE, null,
                    clientNames, currentClient != null ? currentClient.name : clientNames[0]);
            if (selectedClientName == null)
                return;

            String newClientId = clients.stream().filter(c -> c.name.equals(selectedClientName)).findFirst().get().id;

            // 2. Modifier les produits
            String[] productNames = products.stream().map(p -> p.name).toArray(String[]::new);
            JList<String> productList = new JList<>(productNames);
            productList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

            int result = JOptionPane.showConfirmDialog(this, new JScrollPane(productList),
                    "Sélectionner les nouveaux produits", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION)
                return;

            List<String> selectedProducts = productList.getSelectedValuesList();
            if (selectedProducts.isEmpty())
                return;

            // Restaurer le stock des anciens produits
            for (String pid : order.productIds) {
                products.stream().filter(p -> p.id.equals(pid)).findFirst().ifPresent(p -> p.quantity++);
            }

            // 3. Recréer la nouvelle liste de produits
            List<String> newProductIds = new ArrayList<>();
            for (String productName : selectedProducts) {
                Product prod = products.stream().filter(p -> p.name.equals(productName)).findFirst().orElse(null);
                if (prod == null)
                    continue;

                String qtyStr = JOptionPane.showInputDialog(this, "Quantité pour le produit : " + prod.name);
                if (qtyStr == null)
                    continue;

                try {
                    int qty = Integer.parseInt(qtyStr);
                    if (qty <= 0) {
                        JOptionPane.showMessageDialog(this, "Quantité invalide pour " + prod.name);
                        return;
                    }
                    if (prod.quantity < qty) {
                        JOptionPane.showMessageDialog(this, "Stock insuffisant pour " + prod.name);
                        return;
                    }

                    prod.quantity -= qty;
                    for (int i = 0; i < qty; i++)
                        newProductIds.add(prod.id);

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Entrée invalide pour la quantité de " + prod.name);
                    return;
                }
            }

            // 4. Mettre à jour la commande
            order.clientId = newClientId;
            order.productIds = newProductIds;

            // 5. Mettre à jour le nom du client dans le bon de livraison
            deliveries.stream()
                    .filter(d -> d.orderId.equals(order.id))
                    .findFirst()
                    .ifPresent(d -> d.clientName = selectedClientName);

            // 6. Sauvegarde et rafraîchissement
            DataManager.saveOrders(orders);
            DataManager.saveProducts(products);
            DataManager.saveDeliveries(deliveries);

            refreshOrderList(model);
            refreshProductList(productListModel);
            refreshDeliveryList(deliveryListModel);
        });

        updateStatusBtn.addActionListener(e -> {
            int index = orderList.getSelectedIndex();
            if (index >= 0) {
                Order selectedOrder = orders.get(index);
                String[] statuses = { "À expédier", "En cours de livraison", "Livré" };
                String newStatus = (String) JOptionPane.showInputDialog(this,
                        "Choisir un nouveau statut :",
                        "Modifier le statut",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        statuses,
                        selectedOrder.status);

                if (newStatus != null && !newStatus.equals(selectedOrder.status)) {
                    selectedOrder.status = newStatus;
                    DataManager.saveOrders(orders);
                    refreshOrderList(model);

                    // Si livré → créer une facture
                    if (newStatus.equals("Livré")) {
                        boolean alreadyExists = bills.stream()
                                .anyMatch(b -> b.orderId.equals(selectedOrder.id));
                        if (!alreadyExists) {
                            Bill newBill = new Bill(
                                    UUID.randomUUID().toString(),
                                    selectedOrder.id,
                                    LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).toString(),
                                    "Non payé");
                            bills.add(newBill); // ✅ sur la liste chargée
                            DataManager.saveBills(bills);
                            refreshBillList(billModel);
                        }
                    }
                }
            }
        });

        JPanel btnPanel = new JPanel();
        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(updateStatusBtn);
        btnPanel.add(deleteBtn);

        panel.add(new JScrollPane(orderList), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Onglet Factures
    private JPanel createBillPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        billModel = new DefaultListModel<>();
        JList<String> billList = new JList<>(billModel);
        refreshBillList(billModel);

        JButton updateBtn = new JButton("Modifier le statut");
        JButton deleteBtn = new JButton("Supprimer la facture");

        updateBtn.addActionListener(e -> {
            int index = billList.getSelectedIndex();
            if (index >= 0) {
                Bill selectedBill = bills.get(index);
                String[] statuses = { "Non payé", "Payée" };
                String newStatus = (String) JOptionPane.showInputDialog(this,
                        "Nouveau statut :",
                        "Modifier le statut de facture",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        statuses,
                        selectedBill.status);

                if (newStatus != null && !newStatus.equals(selectedBill.status)) {
                    selectedBill.status = newStatus;
                    DataManager.saveBills(bills);
                    refreshBillList(billModel);
                }
            }
        });

        deleteBtn.addActionListener(e -> {
            int index = billList.getSelectedIndex();
            if (index >= 0) {
                bills.remove(index);
                DataManager.saveBills(bills);
                refreshBillList(billModel);
            }
        });

        JPanel btnPanel = new JPanel();
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);

        panel.add(new JScrollPane(billList), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Onglet Bon de livraison
    private JPanel createDeliveryPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        deliveryListModel = new DefaultListModel<>();
        JList<String> deliveriesList = new JList<>(deliveryListModel);
        refreshDeliveryList(deliveryListModel);

        JButton showBtn = new JButton("Afficher le bon de commande");
        JButton deleteBtn = new JButton("Supprimer le bon de commande");

        showBtn.addActionListener(e -> {
            int index = deliveriesList.getSelectedIndex();
            if (index >= 0) {
                DeliveryNote selectedNote = deliveries.get(index);

                Order order = orders.stream()
                        .filter(o -> o.id.equals(selectedNote.orderId))
                        .findFirst()
                        .orElse(null);

                if (order == null) {
                    JOptionPane.showMessageDialog(this, "Commande introuvable.");
                    return;
                }

                Client client = clients.stream()
                        .filter(c -> c.id.equals(order.clientId))
                        .findFirst()
                        .orElse(null);

                StringBuilder message = new StringBuilder();
                message.append("Commande n°").append(order.orderNumber).append("\n");
                message.append("Client : ").append(client != null ? client.name : "Inconnu").append("\n");
                message.append("Adresse de livraison : ").append(client != null ? client.address : "Inconnu")
                        .append("\n");
                message.append("Date de commande : ").append(order.date).append("\n");
                message.append("\nProduits :\n");

                double totalHT = 0.0;
                double TVA = 20;
                double totalTTC = 0.0;

                Map<String, Long> productCount = order.productIds.stream()
                        .collect(Collectors.groupingBy(id -> id, Collectors.counting()));

                for (Map.Entry<String, Long> entry : productCount.entrySet()) {
                    Product p = products.stream().filter(prod -> prod.id.equals(entry.getKey())).findFirst()
                            .orElse(null);
                    if (p != null) {
                        double subtotal = p.price * entry.getValue();
                        totalHT += subtotal;

                        message.append("- ").append(p.name)
                                .append(" (x").append(entry.getValue()).append(")")
                                .append(" - ").append(String.format("%.2f €", p.price)).append(" /unité")
                                .append(" => ").append(String.format("%.2f €", subtotal)).append("\n");
                    }
                }

                message.append("\nPrix total (HT) : " + String.format("%.2f", totalHT) + "€").append("\n");
                totalTTC = (totalHT * 1.2);
                message.append("TVA : " + TVA + "%").append("\n");
                message.append("Prix total (TTC) : " + String.format("%.2f", totalTTC) + "€").append("\n");

                JOptionPane.showMessageDialog(this, message.toString(), "Détails de la commande",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Sélectionnez un bon de livraison d’abord.");
            }
        });

        deleteBtn.addActionListener(e -> {
            int index = deliveriesList.getSelectedIndex();
            if (index >= 0) {
                deliveries.remove(index);
                DataManager.saveDeliveries(deliveries);
                refreshDeliveryList(deliveryListModel);
            }
        });

        JPanel btnPanel = new JPanel();
        btnPanel.add(showBtn);
        btnPanel.add(deleteBtn);

        panel.add(new JScrollPane(deliveriesList), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshProductList(DefaultListModel<String> model) {
        model.clear();
        for (Product p : products) {
            model.addElement(
                    "Nom : " + p.name + " - Stock dispo : x" + p.quantity + " - Prix unitaire HT " + p.price + "€");
        }
    }

    private void refreshClientList(DefaultListModel<String> model) {
        model.clear();
        for (Client c : clients) {
            model.addElement(c.name + " - " + c.email + " - " + c.phone + " - " + c.address);
        }
    }

    private void refreshBillList(DefaultListModel<String> model) {
        model.clear();
        for (Bill b : bills) {
            String clientName = "Inconnu";
            String orderNumber = "Inconnu";

            Order relatedOrder = orders.stream()
                    .filter(o -> o.id.equals(b.orderId))
                    .findFirst()
                    .orElse(null);

            if (relatedOrder != null) {
                clientName = clients.stream()
                        .filter(c -> c.id.equals(relatedOrder.clientId))
                        .map(c -> c.name)
                        .findFirst()
                        .orElse("Inconnu");

                orderNumber = relatedOrder.orderNumber != null ? relatedOrder.orderNumber : "Inconnu";
            }

            model.addElement(
                    "[" + b.date + "] Facture de la commande #" + orderNumber + " (" + clientName + ") - " + b.status);
        }
    }

    private void refreshOrderList(DefaultListModel<String> model) {
        model.clear();
        for (Order o : orders) {
            String clientName = clients.stream()
                    .filter(c -> c.id.equals(o.clientId))
                    .map(c -> c.name)
                    .findFirst()
                    .orElse("Inconnu");

            Map<String, Long> productCount = o.productIds.stream()
                    .collect(Collectors.groupingBy(pid -> pid, Collectors.counting()));

            String productNames = productCount.entrySet().stream()
                    .map(entry -> {
                        String productId = entry.getKey();
                        long count = entry.getValue();
                        String name = products.stream()
                                .filter(p -> p.id.equals(productId))
                                .map(p -> p.name)
                                .findFirst()
                                .orElse("Inconnu");
                        return name + " (x" + count + ")";
                    })
                    .collect(Collectors.joining(", "));

            model.addElement(
                    "[#" + o.orderNumber + "] - " + o.date + " - " + clientName + " : " + productNames
                            + " - "
                            + o.status);
        }
    }

    private void refreshDeliveryList(DefaultListModel<String> model) {
        model.clear();
        for (DeliveryNote d : deliveries) {
            String orderNumber = orders.stream()
                    .filter(o -> o.id.equals(d.orderId))
                    .map(o -> o.orderNumber)
                    .findFirst()
                    .orElse("Inconnu");

            model.addElement(
                    "[" + d.date + "] - Bon de livraison de la commande #" + orderNumber + " - Client : "
                            + d.clientName);
        }
    }

}
