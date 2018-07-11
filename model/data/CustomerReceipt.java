package model.data;

import model.configs.Config;
import model.interfaces.Receipt;
import model.managers.FileManager;
import java.util.ArrayList;


/**
 * The CustomerReceipt class.
 *
 * This class is used to calculate payments (include tax and gratuity) and print receipts.
 */
public class CustomerReceipt implements Receipt{

    private final double TAX_PERCENT = 13;
    private final double GRATUITY = 15;

    /**
     * Calculate total price for the items in order. (without tax/ gratuity)
     * @param items list of items to pay.
     * @return double total price before tax/fees
     */
    @Override
    public double calculateTotalPrice(ArrayList<Item> items) {
        double price = 0.0;
        for(Item item: items) {
            price += item.getPrice();
        }
        return price;
    }

    /**
     * Calculate the payment price (with tax/ gratuity if there are 8 or more people)
     * @param items list of items to pay.
     * @param checkNumPeople boolean to see if there are 8 or more people
     * @return double total price after tax/fees
     */
    public double calculatePaymentPrice(ArrayList<Item> items, boolean checkNumPeople){
        double price = calculateTotalPrice(items);
        if(checkNumPeople)
            price = (price*(TAX_PERCENT/100)) + (price*(GRATUITY/100)) + price;
        else
             price = (price*(TAX_PERCENT/100)) + price;
        return price;
    }

    /**
     * Print out the bill
     * @param items list of items to pay.
     * @param checkNumPeople boolean to see if there are 8 or more people
     */
    @Override
    public void printBill(ArrayList<Item> items, boolean checkNumPeople) {
        double price = calculateTotalPrice(items);
        double priceWithTax = calculatePaymentPrice(items, checkNumPeople);

        for (Item item: items){
            System.out.println("--------------------------------------------");
            System.out.println(item.getName());
            System.out.println("\t \t \t $" + item.getPrice());
            }
        System.out.println("============================================");
        System.out.println("Total: $" + String.format("%.2f", price));
        System.out.println("Tax: " + TAX_PERCENT + "%");
        if(checkNumPeople) {
            System.out.println("Gratuity: "  + GRATUITY + "%");
        }
        System.out.println("Final price: $" + String.format("%.2f", priceWithTax));
    }

    /**
     * Print out the receipt after the costumer pay.
     * @param items list of items to pay.
     * @param checkNumPeople boolean to see if there are 8 or more people
     * @param paidAmount amount costumer paid
     */
    @Override
    public void printPayment(ArrayList<Item> items, boolean checkNumPeople, double paidAmount) {
        double price = calculateTotalPrice(items);
        double priceWithTax = calculatePaymentPrice(items, checkNumPeople);
        String content = "";

        for (Item item: items){
            content += "--------------------------------------------\n";
            content += item.getName() + "\n";
            content += "\t \t \t $" + item.getPrice() + "\n";
        }
        content += "============================================\n";
        content += "Total: $" + String.format("%.2f", price) + "\n";
        content += "Tax: " + TAX_PERCENT + "%\n";
        if(checkNumPeople) {
            content += "Gratuity: "  + GRATUITY + "%\n";
        }
        content += "Final price: $" + String.format("%.2f", priceWithTax) + "\n";
        content += "Amount paid: $" + String.format("%.2f", paidAmount) + "\n";

        new FileManager().writeFile(content, Config.getBillsPath() + items.get(0).getItemInfo().getOrderNumber() + ".txt");
    }
}
