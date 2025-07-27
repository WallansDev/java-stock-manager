package src.controllers;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import src.models.*;
import src.views.*;

public class DeliveryNoteController {
    private DeliveryNote model;
    private DeliveryNoteView view;

    private final String filename = "data/deliveries.json";

    private List<DeliveryNote> deliveriesNotes = loadDeliveriesNotes();

    private Consumer<List<DeliveryNote>> onDeliveryNoteChange = null;

    public void saveAllDeliveriesNotes() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(deliveriesNotes, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void subscribeToDeliveryNoteChange(Consumer<List<DeliveryNote>> con) {
        this.onDeliveryNoteChange = con;
    }

    public List<DeliveryNote> loadDeliveriesNotes() {
        Gson gson = new Gson();
        File file = new File(filename);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<DeliveryNote>>() {
            }.getType();
            List<DeliveryNote> list = gson.fromJson(reader, listType);
            this.deliveriesNotes = list;

            this.callSubscribes();

            return list;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void callSubscribes() {
        if (this.onDeliveryNoteChange != null) {
            this.onDeliveryNoteChange.accept(deliveriesNotes);
        }
    }

    public void addDeliveryNote(String orderId) {
        DeliveryNote deliverynote = new DeliveryNote(UUID.randomUUID().toString(), orderId);
        deliveriesNotes.add(deliverynote);

        this.saveAllDeliveriesNotes();
        this.callSubscribes();
    }
}
