package model.data;

import model.configs.Config;
import model.interfaces.Observable;
import model.interfaces.Observer;
import model.interfaces.Savable;
import model.managers.FileManager;

import java.io.IOException;
import java.util.ArrayList;

/**
 * The Order class.
 *
 * It contains all the items ordered by customer and a reference to a table number.
 * We can modify items in an order and whether the order is finished or not.
 */
public class Order2 implements Savable, Comparable<Order2>, Observable {
    private ArrayList<Item> items;
    private int orderNumber;
    private int tableNumber;
    private boolean isFinished; // bill is paid

    public ArrayList<Observer> observers;

    /**
     * Constructs an order with a unique order number and a table number.
     * @param orderNumber the order's number.
     * @param tableNumber the table's number of this order.
     */
    public Order2(int orderNumber, int tableNumber){
        items = new ArrayList<>();
        this.orderNumber = orderNumber;
        this.tableNumber = tableNumber;
        observers = new ArrayList<>();
    }

    /**
     * Return list of items in this Order.
     * @return list of items.
     */
    public ArrayList<Item> getItems() {
        return items;
    }

    /**
     * Get this order's number.
     * @return int order number.
     */
    public int getOrderNumber() {
        return orderNumber;
    }

    /**
     * Get this order's table number.
     * @return int table number.
     */
    public int getTableNumber() {
        return tableNumber;
    }

    /**
     * Get this order's file name.
     * @return String file name of this Order.
     */
    public String getFileName(){
        return Integer.toString(orderNumber);
    }

    /**
     * Check to see if this order is finished (paid)
     * @return boolean
     */
    public boolean isFinished(){
        return isFinished;
    }

    /**
     * Finish this order.
     */
    public void finished() {
        isFinished = true;
    }

    /**
     * Add item to this order.
     * @param item to be added.
     */
    public void addItem(Item item){
        items.add(item);
    }

    /**
     * Remove item from this order.
     * @param item to be removed.
     */
    public void removeItem(Item item){
        items.remove(item);
    }

    /**
     * Compare two orders base on their order number.
     * @param o the order we're comparing.
     * @return int -1 (less than), 0 (equals to), 1 (greater than)
     */
    @Override
    public int compareTo(Order2 o) {
        int i;
        if (this.orderNumber < o.getOrderNumber()) {
            i = -1;
        } else if (this.orderNumber > o.getOrderNumber()) {
            i = 1;
        } else i = 0;
        return i;
    }

    /**
     * Attach an observer to this order.
     * @param observer the observer we want to attach.
     */
    @Override
    public void attachObserver(Observer observer) {
        observers.add(observer);
    }

    /**
     * Remove an observer from this order.
     * @param observer the observer we want to remove.
     */
    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    /**
     * Update the observers of this order.
     */
    @Override
    public void update() {
        for(Observer observer : observers){
            observer.update(this);
        }
    }

    /**
     * Return True if the object is the same as this Order.
     * @param o the other Object we're comparing.
     * @return boolean
     */
    @Override
    public boolean equals(Object o){
        if (o instanceof Order2 && ((Order2)o).orderNumber == orderNumber){
            return true;
        }
        return false;
    }

    /**
     * Serialize object.
     * @param out output
     * @throws IOException
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        FileManager fileManager = new FileManager();
        ArrayList<String> itemPaths = new ArrayList<>();
        String folderName = isFinished ? Config.getFinishedItemsPath() : Config.getItemsPath();
        for(Item item : items){
            String path = item.getFileName();
            itemPaths.add(path);
            fileManager.saveToFile(item, folderName);
        }
        out.writeInt(orderNumber);
        out.writeInt(tableNumber);
        out.writeBoolean(isFinished);
        out.writeObject(itemPaths);
    }

    /**
     * Deserialize object.
     * @param in input
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException{
        FileManager fileManager = new FileManager();
        items = new ArrayList<>(); // Initiate the items instance here.....
        orderNumber = in.readInt();
        tableNumber = in.readInt();
        isFinished = in.readBoolean();
        ArrayList<String> itemPaths = (ArrayList<String>)in.readObject();
        String folderName = isFinished ? Config.getFinishedItemsPath() : Config.getItemsPath();
        for(String path : itemPaths){
            Item item = (Item)fileManager.readFromFile(folderName + path);
            if(item != null) {
                items.add(item);
            }
        }
        observers = new ArrayList<>();
    }

    /**
     * String representation of this order.
     * @return String
     */
    @Override
    public String toString() {
        return "Order#" + orderNumber;
    }
}
