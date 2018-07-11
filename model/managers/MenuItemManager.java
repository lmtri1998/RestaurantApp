package model.managers;


import model.configs.Config;
import model.data.Item;
import model.data.Meal;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The MenuItemManager class.
 *
 * The MenuItemManager class manages the menu items in this program. This class is responsible to save the Item
 * into the MenuItems folder. It also creates the lock file and check if an MenuItem is lock
 * (i,e, the Item is being editing by the program). The directory of the
 * folder can be changed by editing the config file which is used by the Config class.
 * @see Item
 * @see Config
 */
public class MenuItemManager {
    private FileManager fileManager;


    /**
     * Constructs a MenuItemManager.
     */
    public MenuItemManager() {
        fileManager = new FileManager();
    }

    /**
     * Deletes a Meal menu item.
     * @param item the menu item
     */
    public void deleteMealItem(Item item) {
        fileManager.deleteFile(item, Config.getFoodPath());
    }

    /**
     * Saves the Meal item file
     * @param item the menu item
     */
    public void saveMealItemFile(Item item) {
        fileManager.saveToFile(item, Config.getFoodPath());
    }

    /**
     * Creates a lock file for menu Item
     * @param item the menu item
     */
    public void lockMenuItemFile(Item item){
        String path = Config.getLockFilesPath() + item.getFileName();
        fileManager.writeFile("", path);
    }

    /**
     * Deletes the lock file for menu Item if exists
     * @param item the menu item
     */
    public void unlockMenuItemFile(Item item){
        fileManager.deleteFile(item, Config.getLockFilesPath());
    }

    /**
     * Loads all the Meal menu items and returns the Meal list.
     * @return the Meal menu item list
     */
    public ArrayList<Meal> getMealItemList(){
        // the path directory will be changed when we have more subclass of menu items.
        ArrayList<Meal> menuItems =new ArrayList<>();
        for(File fileName :fileManager.getFileList(Config.getFoodPath())){
            Meal item = (Meal) (fileManager.readFromFile(Config.getFoodPath() + fileName.getName()));
            if(item != null){
                menuItems.add(item);
            }

        }
        return menuItems;
    }

    /**
     * Returns the Meal menu item given the name or null if Meal not found
     * @param name
     * @return
     */
    public Meal getMealItem(String name){
        ArrayList<Meal> menuItems = getMealItemList();
        Meal newItem = null;
        for(Meal item: menuItems) {
            if(item.getName().equals(name)) {
                newItem = item;
            }
        }
        return newItem;
    }
}
