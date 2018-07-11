package model.managers;

import model.configs.Config;
import model.data.*;
import model.interfaces.Savable;

import java.io.File;
import java.util.ArrayList;

/**
 * The OrderManager class
 *
 * It can create, edit, and delete orders. It manages orders, items, and all the action related to, such as lock
 * order file and search order file in the data base by table number or order number.
 */
public class OrderManager {
    private FileManager fileManager;
    private ItemManager itemManager;

    /**
     * Constructs an OrderManager
     */
    public OrderManager(){
        fileManager = new FileManager();
        itemManager = new ItemManager();
    }

    /**
     * Starts the new order cycle for an order. The order will be send to the kitchen. Must call this method
     * when the new order is created
     * @param order the new order
     */
    public void startOrderCycle(Order2 order){
        for (Item item : order.getItems()){
            itemManager.startItemCycle(item);
        }
        fileManager.saveToFile(order, Config.getOrdersPath());
    }

    /**
     * Saves the order to order file
     * @param order the order
     */
    public void saveOrderFile(Order2 order){
        fileManager.saveToFile(order, Config.getOrdersPath());
    }

    /**
     * Deletes the order file from data base.
     * @param order the order to delete
     */
    public void deleteOrderFile(Order2 order){
        for(Item item : order.getItems()){
            itemManager.deleteItemFile(item);
        }
        fileManager.deleteFile(order, Config.getOrdersPath());
    }

    /**
     * Creates a lock file which represents this file has been locked.
     * @param order the locked order
     */
    public void lockOrderFile(Order2 order){
        String path = Config.getLockFilesPath() + order.getFileName();
        fileManager.writeFile("", path);
    }

    /**
     * Unlocks the order file and deletes the lock file if exists.
     * @param order the locked order
     */
    public void unlockOrderFile(Order2 order){
        fileManager.deleteFile(order, Config.getLockFilesPath());
    }

    /**
     * Send the order to the finish order folder. The items in the order will be send to finished item as well.
     * @param order the order that is going to be finished
     */
    public void sendToFinishedOrder(Order2 order){
        order.finished(); // (must call here)
        for(Item item : order.getItems()){
            itemManager.sendToFinishedItem(item);
        }
        fileManager.changeFilePath(order, Config.getOrdersPath(), Config.getFinishedOrdersPath());
    }

    /**
     * Returns a list of orders that contains the order number.
     * @param tableNumber the order number
     * @return a list of orders
     */
    public ArrayList<Order2> searchByTable(int tableNumber){
        ArrayList<Order2> orderList = new ArrayList<>();
        for(Order2 order : getOrderList()){
            if(order.getTableNumber() == tableNumber){
                orderList.add(order);
            }
        }
        return orderList;
    }

    /**
     * Searches the entire system for an order. Search active order folder first.
     * Return null if Order not found
     * @param orderNumber the search order number
     * @return the order
     */
    public Order2 getOrder(int orderNumber){
        for(Order2 order : getOrderList()){
            if(order.getOrderNumber() == orderNumber){
                return order;
            }
        }
        for(Order2 order : getFinishedOrderList()){
            if(order.getOrderNumber() == orderNumber){
                return order;
            }
        }
        return null;
    }

    /**
     * Returns the current order number. Increment the order number file by one each time this method is called.
     * @return the order number
     */
    public int getCurrentOrderNumber(){
        String content = fileManager.readFileContent(Config.getOrderNumberFile());
        int number = Integer.parseInt(content.trim());
        fileManager.writeFile(Integer.toString(number+1), Config.getOrderNumberFile());
        return number;
    }

    /**
     * Returns true if the lock file exists for this order.
     * @param order the check order
     * @return true iff the lock file exists
     */
    public boolean isLockedOrder(Order2 order){
        return fileManager.isExisted(order, Config.getLockFilesPath());
    }

    /**
     * Returns a list of active orders in the data base
     * @return a list of active orders
     */
    public ArrayList<Order2> getOrderList(){
        ArrayList<Order2> orderList = new ArrayList<>();
        for(File orderFile : fileManager.getFileList(Config.getOrdersPath())){
            Savable order = fileManager.readFromFile(Config.getOrdersPath() + orderFile.getName());
            if(order != null) {
                orderList.add((Order2)order);
            }
        }
        return orderList;
    }

    /**
     * Return a list of finished orders
     * @return a list of finished orders
     */
    private ArrayList<Order2> getFinishedOrderList(){
        ArrayList<Order2> orderList = new ArrayList<>();
        for(File orderFile : fileManager.getFileList(Config.getFinishedOrdersPath())){
            Order2 order = (Order2)fileManager.readFromFile(Config.getFinishedOrdersPath()+ orderFile.getName());
            if(order != null) {
                orderList.add(order);
            }
        }
        return orderList;
    }
}