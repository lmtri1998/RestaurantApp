package control;

import model.configs.Config;
import model.data.Item;
import model.data.Order2;
import model.data.Supply2;
import model.interfaces.Observable;
import model.managers.ItemManager;
import model.managers.MyLogger;
import model.managers.OrderManager;
import model.managers.SupplyManager;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * This class is use for either making change to an order
 * or modifying an order. This is the connection between the UI
 * and the itemManager and OrderManager.
 * This class will also log any activity that happened.
 */
public class OrderModifier {
    private ItemManager itemManager;
    private Logger logger;
    private OrderManager orderManager;
    private FileController fileController;
    private ItemConverter itemConverter;
    private SupplyManager supplyManager;

    public OrderModifier(){
        itemManager = new ItemManager();
        logger = (new MyLogger(Config.getLoggerFile())).getLogger();
        orderManager = new OrderManager();
        fileController = FileController.getFileController();
        itemConverter = new ItemConverter();
        supplyManager = new SupplyManager();
    }

    /**
     * Return an item in a order back to the kitchen for the kitchen
     * to re-cook them
     *
     * PreCondition: the item must been delivered.
     *
     * @param itemNumber - the item that is needed to be returned
     * @param reason - the reason for the return
     */
    public void returnItem(int itemNumber, String reason){
        Item item = itemManager.getItem(itemNumber);
        if(item != null){
            String msg = "Order #" + item.getItemInfo().getOrderNumber()
                    + item.toString() + " is being returned with the reason of \n \t\"" + reason + "\"";
            logger.info(msg);
            item.getItemInfo().addAdditionalRequest(reason);
            item.getItemInfo().resetStatus();
            itemManager.sendToKitchen(item);

            fileController.notifyChange((Observable)item);

            Order2 order = orderManager.getOrder(item.getItemInfo().getOrderNumber());
            if(order != null){
                fileController.notifyChange(order);
            }
        }
    }

    /**
     * Modify the original item that was already in an order
     * to a new one
     *
     * Precondition: The string of item follows the item format
     * @param itemNumber - the original item
     * @param itemInfo - the description of the new item
     */
    public void editItem(int itemNumber, String itemInfo){
        Item item = itemManager.getItem(itemNumber);
        if(item != null) {
            String msg = item.toString() + " has been modified";
            logger.info(msg);
            Item newItem = itemConverter.stringToItem(-1, -1, -1, itemInfo);
            item.getItemInfo().setAdditionalRequest(newItem.getItemInfo().getAdditionalRequest());
            item.getIngredients().setAddition(newItem.getIngredients().getAddition());
            item.getIngredients().setSubtraction(newItem.getIngredients().getSubtraction());
            itemManager.saveItemFile(item);
            Order2 order = orderManager.getOrder(item.getItemInfo().getOrderNumber());
            fileController.notifyChange(order);
            fileController.notifyChange((Observable) item);
        }
    }

    /**
     * Delete an Ordered Item from the order. When the item is removed,
     * it will subtract the ingredients if the ite is already being made.
     * @param itemNumber - the item that need to be deleted
     */
    public void deleteItem(int itemNumber){
        Item item = itemManager.getItem(itemNumber);
        if(item != null){
            String msg = item.toString() + " has been deleted";
            logger.info(msg);
            Order2 order = orderManager.getOrder(item.getItemInfo().getOrderNumber());
            if(order != null){
                if(item.getItemInfo().isSeen()){
                    supplyManager.deductIngredientUsage(item.getIngredients().getNeededIngredients());
                    for(String supplyName : item.getIngredients().getNeededIngredients().keySet()){
                        Supply2 supply = supplyManager.getSupplyByName(supplyName);
                        fileController.notifyChange(supply);
                    }
                }else{
                    supplyManager.deductIngredientsFromReservedSupply((item.getIngredients().getNeededIngredients()));
                }

                order.removeItem(item);
                itemManager.deleteItemFile(item);
                orderManager.saveOrderFile(order);
                fileController.notifyChange(order);
                fileController.notifyChange((Observable)item);

            }
        }
    }

    /**
     * Delete  an order completely. When an order is deleted,
     * it will also delete all the items as well.
     * @param orderNum - the order that want to be deleted.
     */
    public void deleteOrder(int orderNum){
        Order2 order = orderManager.getOrder(orderNum);
        if(order != null){
            for(Item item : order.getItems()){
                if(item.getItemInfo().isSeen()){
                    supplyManager.deductIngredientUsage(item.getIngredients().getNeededIngredients());
                }else{
                    supplyManager.deductIngredientsFromReservedSupply((item.getIngredients().getNeededIngredients()));
                }
                itemManager.deleteItemFile(item);
                fileController.notifyChange((Observable)item);
            }
            for(Item item : order.getItems().toArray(new Item[order.getItems().size()])){
                order.removeItem(item);
            }
            orderManager.deleteOrderFile(order);
            fileController.notifyChange(order);
            String msg = order.toString() + " has been deleted";
            logger.info(msg);

        }
    }

    /**
     * Add an item to an existing order.
     *
     * Precondition: The String of item meet the format
     *
     * @param orderNum - the order that we want to add into
     * @param itemInfo - the itemInfo, which represent the item,
     *                 that is needed to be added to the order
     */
    public void addItemToOrder(int orderNum , String itemInfo){
        Order2 order = orderManager.getOrder(orderNum);
        if(order != null){
            Item item = itemConverter.stringToItem(order.getOrderNumber(), order.getTableNumber(), itemManager.getCurrentItemNumber(), itemInfo);
            String msg =item.toString() + " has been added to " + order.toString();
            logger.info(msg);
            order.addItem(item);
            orderManager.saveOrderFile(order);
            fileController.notifyChange(order);
            fileController.notifyChange((Observable) item);
        }
    }

    /**
     * This is called when the order is completely finished and the Bill
     * have been payed. This will move the order file to finishedOrders.
     * @param orderNum - the order that is finished
     */
    public void finishOrder(int orderNum){
        Order2 order = orderManager.getOrder(orderNum);
        if(order != null){
            orderManager.sendToFinishedOrder(order);
            fileController.notifyChange(order);
        }
    }


    /**
     * Make a new order that contains the items, which was given by the
     * string.
     *
     * PreCondition: the String of items meet the format.
     *
     * @param tableNumber - the table# for this order
     * @param items - Strings of items that is in the order
     */
    public void makeNewOrder(int tableNumber, ArrayList<String> items){
        if(!items.isEmpty()) {
            int orderNumber = orderManager.getCurrentOrderNumber();
            Order2 order = new Order2(orderNumber, tableNumber);
            for (String itemInfo : items) {
                order.addItem(itemConverter.stringToItem(orderNumber, tableNumber, itemManager.getCurrentItemNumber(), itemInfo));
            }
            orderManager.startOrderCycle(order);
            fileController.notifyChange(order);

            String msg = "new order " + order.toString() + " has been created";
            logger.info(msg);
        }
    }


}
