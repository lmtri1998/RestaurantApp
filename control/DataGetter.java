package control;

import model.data.Item;
import model.data.Meal;
import model.data.Order2;
import model.data.Supply2;
import model.managers.ItemManager;
import model.managers.MenuItemManager;
import model.managers.OrderManager;
import model.managers.SupplyManager;

import java.util.ArrayList;

/**
 * This class is the Getter for the data in the model. View and view controller
 * should use this class to get all the information from the model. This class
 * can return list of MenuItems, Orders, OrderedItems, and Supplies
 */
public class DataGetter {
    private MenuItemManager menuItemManager;
    private OrderManager orderManager;
    private ItemManager itemManager;
    private SupplyManager supplyManager;
    public DataGetter(){
        menuItemManager = new MenuItemManager();
        orderManager = new OrderManager();
        itemManager = new ItemManager();
        supplyManager= new SupplyManager();
    }

    /**
     * return a list of menuItems from the dataBase.
     * @return list of menuItems
     */
    public ArrayList<Meal> getMealMenuItems(){
        return menuItemManager.getMealItemList();
    }

    /**
     * Return a list of Active orders from the dataBase
     * @return list of active orders
     */
    public ArrayList<Order2> getCurrentActiveOrders(){
        return orderManager.getOrderList();
    }

    /**
     * return a list of Ready-to-Served items from the
     * data base
     * @return list of ready-to-serve items
     */
    public ArrayList<Item> getReadyItems() {
        ArrayList<Item> items = new ArrayList<>();
        for (Item item : itemManager.getFrontItems()){
            if(item.getItemInfo().isReady() && !item.getItemInfo().isServed()){
                items.add(item);
            }
        }
        return items;
    }

    /**
     * return a list of supplies from the dataBase
     * @return list of supplies.
     */
    public ArrayList<Supply2> getCurrentSupplies(){
        ArrayList<Supply2> supplies = new ArrayList<>();
        for(String name : supplyManager.getSupplyNameList()){
            Supply2 supply = supplyManager.getSupplyByName(name);
            if(supply != null) {
                supplies.add(supplyManager.getSupplyByName(name));
            }
        }
        return supplies;
    }
}
