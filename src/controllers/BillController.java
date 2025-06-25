package src.controllers;

import src.models.*;

public class BillController {
    private StockModel model;

    public BillController(StockModel model) {
        this.model = model;
    }

    public void updateBillStatus(int index, String newStatus) {
        Bill bill = model.getBill(index);
        if (bill != null) {
            Bill updatedBill = new Bill(bill.id, bill.orderId, bill.date, newStatus);
            model.updateBill(index, updatedBill);
        }
    }

    public void deleteBill(int index) {
        model.deleteBill(index);
    }
}
