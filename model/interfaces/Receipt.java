package model.interfaces;

import model.data.Item;

import java.util.ArrayList;

/**
 * The Receipt interface.
 */
public interface Receipt {
    double calculateTotalPrice(ArrayList<Item> items);
    void printBill(ArrayList<Item> items, boolean checkNumPeople);
    void printPayment(ArrayList<Item> items, boolean checkNumPeople, double totalPrice);
}
