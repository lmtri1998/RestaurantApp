package model.data;
import java.io.Serializable;

/**
 * The ItemInfo class.
 *
 * This class keeps track and modified of all the information of an Item, including it's path, additional requests,
 * status and current location of the item, the item's number, table number and order number associated to the item.
 */
public class ItemInfo implements Serializable{

    private String filePath;
    private String additionalRequest;
    private String status;  // [ready, served, seen]
    private String location; // [kitchen, front] used by
    private int itemNumber;
    private int tableNumber;
    private int orderNumber;

    /**
     * Constructs an ItemInfo.
     * @param itemNumber the Item's number
     * @param orderNumber the order's number that is associated to the item.
     * @param tableNumber the table number that the item belongs to.
     */
    public ItemInfo(int itemNumber, int orderNumber, int tableNumber){
        status = "";
        additionalRequest = "";
        filePath = "";
        this.itemNumber = itemNumber;
        this.orderNumber = orderNumber;
        this.tableNumber = tableNumber;
    }

    /**
     * Get the location of the item (kitchen or front).
     * @return String of the item's location
     */
    public String getLocation(){
        return location;
    }
    public String getAdditionalRequest() {
        return additionalRequest;
    }

    /**
     * Get the item's number.
     * @return int item's number.
     */
    public int getItemNumber() {
        return itemNumber;
    }

    /**
     * Get the table number that the item belongs to.
     * @return int item's table number.
     */
    public int getTableNumber() {
        return tableNumber;
    }

    /**
     * Get the order number that the item belongs to.
     * @return int item's order number.
     */
    public int getOrderNumber() {
        return orderNumber;
    }

    /**
     * The item's status is now acknowledged by the kitchen and is being prepared.
     */
    public void seen(){
        status = "seen";
    }

    /**
     * The item's status is now served to the costumer.
     */
    public void served(){
        status = "served";
    }

    /**
     * The item's status is now ready to serve.
     */
    public void ready(){
        status = "ready";
    }

    /**
     * Set additional requests for this item.
     * @param additionalRequest
     */
    public void setAdditionalRequest(String additionalRequest) {
        this.additionalRequest = additionalRequest;
    }

    /**
     * Set location of this item.
     * @param location of the item's location (kitchen or front).
     */
    public void setLocation(String location){
        this.location = location;
    }

    /**
     * Check if the item is acknowledge by the kitchen.
     * @return boolean
     */
    public boolean isSeen() {
        return !status.equals("");
    }

    /**
     * Check if the item is already served to the customer.
     * @return boolean
     */
    public boolean isServed() {
        return status.equals("served");
    }

    /**
     * Check if the item is ready to serve.
     * @return boolean
     */
    public boolean isReady() {
        return status.equals("ready") || status.equals("served");
    }

    /**
     * Reset the item status to the original status, which is empty.
     */
    public void resetStatus(){
        status = "";
    }

    /**
     * Append the txt to the current additionalRequest
     * @param txt message to append
     */
    public void addAdditionalRequest(String txt){
        additionalRequest += "\n" + txt;
    }
}
