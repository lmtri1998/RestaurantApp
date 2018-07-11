package model.managers;

import model.configs.Config;
import model.data.Item;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.*;

/**
 * The ItemManager class.
 *
 * The ItemManager class manages the customer menu items in this program. This class is responsible to save the Item
 * into the Items folder which represents all Items that are currently operating in the restaurant. It also creates
 * the lock file and check if an Item is lock (i,e, the Item is being editing by the program). The directory of the
 * folder can be changed by editing the config file which is used by the Config class.
 * @see Item
 * @see Config
 */
public class ItemManager {
    private final String KITCHEN = "kitchen";
    private final String FRONT = "front";

    private FileManager fileManager;

    /**
     * Constructs an ItemManager.
     */
    public ItemManager(){
        fileManager = new FileManager();
    }

    /**
     * Sends the item into the kitchen by changing its location status. This method must be called when the Item is
     * first created.
     * @param item
     */
    public void startItemCycle(Item item){
        item.getItemInfo().setLocation(KITCHEN);
        fileManager.saveToFile(item, Config.getItemsPath());
    }

    /**
     * Saves Item file in Items directory.
     * @param item the customer item
     */
    public void saveItemFile(Item item){
        fileManager.saveToFile(item, Config.getItemsPath());
    }

    /**
     * Sends the Item to the kitchen and saves the file
     * @param item the customer item
     */
    public void sendToKitchen(Item item){
        item.getItemInfo().setLocation(KITCHEN);
        saveItemFile(item);
    }

    /**
     * Sends the Item to the front and saves the file.
     * @param item the customer item
     */
    public void sendToFront(Item item){
        item.getItemInfo().ready();
        item.getItemInfo().setLocation(FRONT);
        saveItemFile(item);
    }

    /**
     * Sends the Item to the finished Item folder.
     * @param item the customer item
     */
    public void sendToFinishedItem(Item item){
        fileManager.changeFilePath(item, Config.getItemsPath(), Config.getFinishedItemsPath());
    }

    /**
     * Acknowledge the Item. This means the Item is being seen in the kitchen and is preparing.
     * @param item the customer item
     */
    public void acknowledgeItem(Item item){
        item.getItemInfo().seen();
        saveItemFile(item);
    }

    /**
     * Confirms that this Item has been delivered to the customer.
     * @param item the customer item
     */
    public void conFirmDeliveryItem(Item item){
        item.getItemInfo().served();
        saveItemFile(item);
    }

    /**
     * Creates a lock file for Item
     * @param item the customer item
     */
    public void lockItemFile(Item item){
        String path = Config.getLockFilesPath() + item.getFileName();
        fileManager.writeFile("", path);
    }

    /**
     * Deletes the lock file for Item if exists
     * @param item the customer item
     */
    public void unlockItemFile(Item item){
        fileManager.deleteFile(item, Config.getLockFilesPath());
    }

    /**
     * Checks if the lock file exists or not.
     * @param item the customer item
     * @return true iff the lock file exists
     */
    public boolean isLockedItem(Item item){
        return fileManager.isExisted(item, Config.getLockFilesPath());
    }

    /**
     * Returns a active Item (in kitchen or in front) with given Item number. Return null if Item not found.
     * @param itemNumber the item number
     * @return the customer item or null
     */
    public Item getItem(int itemNumber){
        for(Item item : getItemList(FRONT)){
            if(item.getItemInfo().getItemNumber() == itemNumber){
                return item;
            }
        }
        for(Item item : getItemList(KITCHEN)){
            if(item.getItemInfo().getItemNumber() == itemNumber){
                return item;
            }
        }
        return null;
    }

    /**
     * Returns all front Items.
     * @return all front Items
     */
    public ArrayList<Item> getFrontItems(){
        return getItemList(FRONT);
    }

    /**
     * Returns all kitchen Items.
     * @return all kitchen Items
     */
    public ArrayList<Item> getKitchenItems() {
        return getItemList(KITCHEN);
    }

    /**
     * Returns the current item number from the currentItemNumber file and increment the number by one in the file
     * @return the current item number
     */
    public int getCurrentItemNumber(){
        String content = fileManager.readFileContent(Config.getItemNumberFile());
        int number = Integer.parseInt(content.trim());
        fileManager.writeFile(Integer.toString(number+1), Config.getItemNumberFile());
        return number;
    }

    /**
     * Returns all Items that for a table
     * @param tableNumber the table number
     * @return the Item list
     */
    public ArrayList<Item> searchByTableNumber(int tableNumber){
        ArrayList<Item> items = getItemList("");
        ArrayList<Item> searchItems = new ArrayList<>();
        for(Item item : items){
            if(item.getItemInfo().getTableNumber() == tableNumber){
                searchItems.add(item);
            }
        }
        return searchItems.size()==0 ? null : searchItems;
    }

    /**
     * Returns all Items that for an order
     * @param orderNumber the table number
     * @return the Item list
     */
    public ArrayList<Item> searchByOrderNumber(int orderNumber){
        ArrayList<Item> items = getItemList("");
        ArrayList<Item> searchItems = new ArrayList<>();
        for(Item item : items){
            if(item.getItemInfo().getOrderNumber() == orderNumber){
                searchItems.add(item);
            }
        }
        return searchItems.size()==0 ? null : searchItems;
    }

    /**
     * Returns all Items from a given location. Pass empty string to search for all Items
     * @param location the location
     * @return the Item list
     */
    private ArrayList<Item> getItemList(String location){
        ArrayList<Item> items = new ArrayList<>();
        File[] itemFiles = fileManager.getFileList(Config.getItemsPath());
        for(File file: itemFiles) {
            Item item = (Item)fileManager.readFromFile(Config.getItemsPath() + file.getName());
            if(item != null || location.equals("") || location.equals(item.getItemInfo().getLocation()))
                items.add(item);
        }
        return items;
    }

    /**
     * Deletes the Item file
     * @param item the custom item
     */
    public void deleteItemFile(Item item){
        fileManager.deleteFile(item, Config.getItemsPath());
    }

}
