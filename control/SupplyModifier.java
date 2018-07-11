package control;

import model.configs.Config;
import model.data.Item;
import model.data.Supply2;
import model.managers.MyLogger;
import model.managers.SupplyManager;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * The supply modifier class.
 *
 * This class controls the supply parts of the program.
 */
public class SupplyModifier {
    private SupplyManager supplyManager;
    private Logger logger;
    private FileController fileController;
    private ItemConverter itemConverter;

    /**
     * Constructs a SupplyModifier.
     */
    public SupplyModifier(){
        supplyManager = new SupplyManager();
        logger = (new MyLogger(Config.getLoggerFile())).getLogger();
        fileController = FileController.getFileController();
        itemConverter = new ItemConverter();
    }

    /**
     * Adds the quantity to the supply with given name
     * @param name name
     * @param quantity the quantity
     */
    public void addQuantityToSupply(String name, int quantity) {
        Supply2 s = supplyManager.getSupplyByName(name);
        if(s != null) {
            String msg = quantity + " " + name +" has been added to the stock";
            logger.info(msg);
            s.addQuantity(quantity);
            supplyManager.saveSupplyFile(s);
            fileController.notifyChange(s);
            supplyManager.updateSupplyRequests();
        }
    }

    /**
     * Edits the supply's request amount.
     * @param name the supply name
     * @param requestAmount the requestAmount
     */
    public void editSupply(String name, int requestAmount) {
        Supply2 s = supplyManager.getSupplyByName(name);
        if(s != null) {
            String msg = s.toString() + " has been modified";
            logger.info(msg);
            s.setRequestAmount(requestAmount);
            supplyManager.saveSupplyFile(s);
            fileController.notifyChange(s);
        }

    }

    /**
     * Creates new supply given its name
     * @param name the supply name
     * @param initQuantity the init quantity
     */
    public void createSupply(String name, int initQuantity){
        String msg = "New supply " + name + " has been added to the stock with quantity of " + initQuantity;
        logger.info(msg);
        Supply2 s = new Supply2(name, 20, initQuantity);
        supplyManager.saveSupplyFile(s);
        fileController.notifyChange(s);
    }

    /**
     * Checks if the there is enough supply for this item
     * @param item the item
     * @param needReserve whether there is enough supply for this item to be reserved
     * @return true if there is enough supply
     */
    public boolean haveEnoughSupply(Item item, boolean needReserve){
        return supplyManager.checkNeededSupply(item.getIngredients().getNeededIngredients(), needReserve);
    }

    /**
     * Checks if the there is enough supply for this item
     * @param itemInfo the item
     * @param needReserve whether there is enough supply for this item to be reserved
     * @return true if there is enough supply
     */
    public boolean haveEnoughSupply(String itemInfo, boolean needReserve){
        Item item = itemConverter.stringToItem(-1, -1, -1, itemInfo);
        return supplyManager.checkNeededSupply(item.getIngredients().getNeededIngredients(), needReserve);
    }

    /**
     * Cancel the reserved the supply and reduces the reserved supply
     * @param itemInfos the item representation
     */
    public void cancelSupplyReserved(ArrayList<String> itemInfos){
        for (String itemInfo : itemInfos) {
            Item item = itemConverter.stringToItem(-1, -1,-1, itemInfo);
            supplyManager.deductIngredientsFromReservedSupply(item.getIngredients().getNeededIngredients());
        }
    }
    public void cancelSupplyReserved(String itemInfo){
        Item item = itemConverter.stringToItem(-1, -1,-1, itemInfo);
        supplyManager.deductIngredientsFromReservedSupply(item.getIngredients().getNeededIngredients());
    }
}
