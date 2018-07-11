package control;

import model.configs.Config;
import model.data.Item;
import model.data.ObservableItem;
import model.data.Order2;
import model.managers.FileManager;
import model.managers.ItemManager;
import model.managers.OrderManager;
import model.managers.SupplyManager;

public class StatusChecker {
    private OrderManager orderManager;
    private FileManager fileManager;
    private ItemManager itemManager;
    private SupplyManager supplyManager;
    public StatusChecker(){
        itemManager = new ItemManager();
        orderManager = new OrderManager();
        fileManager = new FileManager();
        supplyManager = new SupplyManager();
    }

    /**
     * Returns true if the order is currently locked, meaning
     * there exist a lock file for this order
     * @param orderNum for the order
     * @return true if the order can be edit
     */
    public boolean isEditableOrder(int orderNum){
        Order2 order = orderManager.getOrder(orderNum);
        if(order != null){
            return !(orderManager.isLockedOrder(order));
        }
        return false;
    }

    /**
     * Returns true if the order is ready by its order number. An order is ready iff all of the items are ready.
     * Returns false if order not found
     * @param orderNum the check order
     * @return true if all of the items are ready, or false if order not found
     */
    public boolean isReadyOrder(int orderNum){
        Order2 order = orderManager.getOrder(orderNum);
        if(order != null){
            for(Item item : order.getItems()) {
                if (!item.getItemInfo().isReady()){
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Check if everyItem in this order has been seen, i.e. been confirmed by the kitchen
     * . Return true if it has and false otehrwise
     * @param orderNum - the order number of the order
     * @return true if all item have been confirmed by the kitchen
     */
    public boolean isSeenOrder(int orderNum){
        Order2 order = orderManager.getOrder(orderNum);
        if(order != null){
            for(Item item : order.getItems()) {
                if (!item.getItemInfo().isSeen()){
                    return false;
                }
            }
            return true;
        }
        return false;
    }


    /**
     * Check if this order has been deleted. If it has been deleted,
     * then there will not exists an order file in the dataBase
     * @param order - the order to check
     * @return true if there does not exists a file
     */
    public boolean isDeleteOrder(Order2 order){
        return !fileManager.isExisted(order, Config.getOrdersPath());
    }

    /**
     * Check if the item has been deleted. If it has been deleted,
     * then there will not exists an item file in dataBase.
     * @param item - item to check
     * @return true if there does not exists the file
     */
    public boolean isDeleteItem(ObservableItem item){
        if(isMenuItem(item)){
            return !fileManager.isExisted(item, Config.getFoodPath());
        }
        return !fileManager.isExisted(item, Config.getItemsPath());
    }

    /**
     * Check if the order is empty or not. If the order is empty, then
     * it will have no items in it
     * @param order - order to check
     * @return true if the order is empty
     */
    public boolean isEmptyOrder(Order2 order){
        return order.getItems().isEmpty();
    }

    /**
     * Check if the item is a menu item. If the item is a menu item,
     * then the item number will be negitave 1
     * @param item -  item to check
     * @return treu if it is a menu item.
     */
    public boolean isMenuItem(Item item) {
        return item.getItemInfo().getItemNumber() < 0;
    }

}
