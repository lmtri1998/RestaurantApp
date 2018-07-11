package control;

import model.configs.Config;
import model.data.Item;
import model.data.ObservableItem;
import model.data.Order2;
import model.data.Supply2;
import model.managers.*;

import java.util.HashMap;
import java.util.logging.Logger;

/**
 * This class is responsible for letting the model knows that
 * a Customer Item status is being requested to be changed from
 * the view. It will also log all the changes that have been changed.
 */
public class CustomerItemController {
    private OrderManager orderManager;
    private ItemManager itemManager;
    private SupplyManager supplyManager;
    private Logger logger;
    private FileManager fileManager;
    private FileController fileController;
    public CustomerItemController(){
        orderManager = new OrderManager();
        itemManager = new ItemManager();
        logger = (new MyLogger(Config.getLoggerFile())).getLogger();
        supplyManager = new SupplyManager();
        fileController = FileController.getFileController();
        fileManager = new FileManager();
    }

    /**
     * This method is called when an item in the kitchen have
     * been prepared and needs to be served. This class will
     * let the models know the change and log the change to the file.
     * @param orderNum - order number that this item is in.
     * @param itemNum - item number for the item that is finished cooking.
     */
    public void readyItem(int orderNum, int itemNum) {
        Order2 order = orderManager.getOrder(orderNum);
        if(order != null){
            for(Item item : order.getItems()){
                if(item.getItemInfo().getItemNumber() == itemNum){
                    String msg = order.toString() + " "
                            + item.toString() + " has been cooked and is waiting for delivery";
                    logger.info(msg);
                    itemManager.sendToFront(item);
                    HashMap<String, Integer> ingredients = item.getIngredients().getNeededIngredients();
                    supplyManager.deductIngredientUsage(ingredients);
                    fileController.notifyChange((ObservableItem) item);
                    notifyAllIngredients(ingredients);
                }
            }
            fileController.notifyChange(order);
            supplyManager.updateSupplyRequests();

        }
    }

    /**
     * Notify other programs that the supply amount have been changed
     * @param ingredients hashmap of ingredients and quantity that is been changed.
     */
    private void notifyAllIngredients(HashMap<String, Integer> ingredients){
        for(String supplyName : ingredients.keySet()){
            Supply2 supply = supplyManager.getSupplyByName(supplyName);
            fileController.notifyChange(supply);
        }
    }

    /**
     * This method is called when the kitchen have seen the item.
     * This will log the change and notify other programs the change.
     * @param orderNum - the order number that this item is in.
     * @param itemNum - the item  that is been knowledge.
     */
    public void acknowledgeItem(int orderNum, int itemNum) {
        Order2 order = orderManager.getOrder(orderNum);
        if(order != null){
            for(Item item : order.getItems()){
                if(item.getItemInfo().getItemNumber() == itemNum){
                    String msg = order.toString() + ":"
                            + item.toString() + " has been acknowledge by the kitchen";
                    logger.info(msg);
                    itemManager.acknowledgeItem(item);
                }
            }
            fileController.notifyChange(order);
        }
    }

    /**
     * This method is called when the server serve the item. This will
     * be logged and notify other program
     * @param itemNumber - item number of the item that have been delivered.
     */
    public void confirmDelivery(int itemNumber){
        ObservableItem item = (ObservableItem) itemManager.getItem(itemNumber);
        if(item != null) {
            String msg = item.toString() + " has been delivered to table #" +item.getItemInfo().getTableNumber();
            logger.info(msg);
            itemManager.conFirmDeliveryItem(item);
            fileController.notifyChange(item);
            Order2 order = orderManager.getOrder(item.getItemInfo().getOrderNumber());
            if(order != null){
                fileController.notifyChange(order);
            }
        }

    }
}
