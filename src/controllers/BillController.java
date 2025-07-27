package src.controllers;

import src.models.*;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class BillController {
    private List<Bill> bills = new ArrayList<>();
    private final String filename = "data/bills.json";

    private Consumer<List<Bill>> onBillChange = null;

    public List<Bill> loadBills() {
        Gson gson = new Gson();
        File file = new File(filename);
        if (!file.exists()) {
            this.bills = new ArrayList<>();
            return bills;
        }
        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<Bill>>() {
            }.getType();
            this.bills = gson.fromJson(reader, listType);
            return bills;
        } catch (IOException e) {
            e.printStackTrace();
            this.bills = new ArrayList<>();
            return bills;
        }
    }

    public void subscribeToBillChange(Consumer<List<Bill>> con) {
        this.onBillChange = con;
    }

    private void callSubscribes() {
        if (this.onBillChange != null) {
            this.onBillChange.accept(bills);
        }
    }

    public void saveAllBills() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(bills, writer);
            this.callSubscribes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bill createBill(Order order, Client client) {

        // Vérifie si une facture existe déjà pour cette commande
        for (Bill b : bills) {
            if (b.getOrder().getOrderNumber().equals(order.getOrderNumber())) {
                return b;
            }
        }
        String billId = UUID.randomUUID().toString().substring(0, 8);
        Bill bill = new Bill(billId, order, client, false,
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        bills.add(bill);
        saveAllBills();
        return bill;
    }

    public void markAsPaid(String billId) {
        for (Bill b : bills) {
            if (b.getBillId().equals(billId)) {
                b.setPaid(true);

                saveAllBills();
                break;
            }
        }
    }
}