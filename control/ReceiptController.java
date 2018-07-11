package control;

import model.data.CustomerReceipt;
import model.data.Item;
import model.managers.ItemManager;

import java.util.ArrayList;

/**
 * This is a receipt controller lass where this
 * class can comunitcate with the customer receipt class
 * to generate receipt or get information from  it.
 */
public class ReceiptController {
    private ItemManager itemManager;
    private CustomerReceipt customerReceipt;
    public ReceiptController(){
        itemManager = new ItemManager();
        customerReceipt = new CustomerReceipt();
    }

    /**
     * This will tell the CustomerReceipt class to print a
     * receipt from the given orderd-items.
     * @param itemNumbers - item' numbers that needed to be printed
     * @param isMoreThanEight - if there is more than 8 people for this bill.
     */
    public void printReceipt(ArrayList<Integer> itemNumbers, boolean isMoreThanEight) {
        ArrayList<Item> items = new ArrayList<>();
        for(Integer i: itemNumbers) {
            items.add(itemManager.getItem(i));
        }
        customerReceipt.printBill(items, isMoreThanEight);
    }

    /**
     * This method will calculate the price for payment of the items that was passed in
     * and return the price
     * @param itemNumbers - items that need to be calculated
     * @param isMoreThanEight - if there is more than 8 people for this bill.
     * @return - the payment price.
     */
    public double calculatePaymentPrice(ArrayList<Integer> itemNumbers, boolean isMoreThanEight) {
        ArrayList<Item> items = new ArrayList<>();
        for(Integer i: itemNumbers) {
            items.add(itemManager.getItem(i));
        }
        return customerReceipt.calculatePaymentPrice(items, isMoreThanEight);
    }

    /**
     * Print the reciept from the given items.
     *
     * Precondition: the paidAmount must be greater than the total
     * price for the items.
     *
     * @param itemNumbers - items that need to be printed
     * @param isMoreThanEight - if there is more than 8 people for this bill
     * @param paidAmount - the amount the customer payed.
     */
    public void printPaymentReceipt(ArrayList<Integer> itemNumbers, boolean isMoreThanEight, double paidAmount) {
        ArrayList<Item> items = new ArrayList<>();
        for(Integer i: itemNumbers) {
            items.add(itemManager.getItem(i));
        }
        customerReceipt.printPayment(items, isMoreThanEight, paidAmount);
    }

}
