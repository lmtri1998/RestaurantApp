package control;

import model.data.Item;
import model.data.Order2;
import model.interfaces.Observable;
import model.interfaces.Savable;
import model.managers.ItemManager;
import model.managers.MenuItemManager;
import model.managers.OrderManager;

/**This class is a lockFileController class
 * This class can lock a file by creating a lock file
 * so that when a file is locked, only one program can
 * modify it.
 */
public class LockFileController {
    private ItemManager itemManager;
    private OrderManager orderManager;
    private MenuItemManager menuItemManager;
    private FileController fileController;
    private StatusChecker statusChecker;

    public LockFileController(){
        itemManager = new ItemManager();
        orderManager = new OrderManager();
        menuItemManager = new MenuItemManager();
        fileController = FileController.getFileController();
    }

    /**
     * Lock the file by creating a file with the same name that
     * ends with .lock . When this file exist, only one program
     * can edit it.
     * @param savable the file that needs to be lock.
     */
    public void lockFile(Savable savable){
        if(savable instanceof Item){
            if(statusChecker.isMenuItem((Item)savable)){
                menuItemManager.lockMenuItemFile((Item) savable);
            }
            else {
                itemManager.lockItemFile((Item) savable);
            }
        }
        else if(savable instanceof Order2){
            orderManager.lockOrderFile((Order2) savable);
        }
        fileController.notifyChange((Observable) savable);
        ((Observable)savable).update();
    }

    /**
     * Unlock the file by removing the lock file so that
     * other program can also edit this file.
     * @param savable the file that need to be unlocked
     */
    public void unlockFile(Savable savable){
        if(savable instanceof Item){
            if(statusChecker.isMenuItem((Item)savable)){
                menuItemManager.unlockMenuItemFile((Item) savable);
            }
            else {
                itemManager.unlockItemFile((Item) savable);
            }
        }
        else if(savable instanceof Order2){
            orderManager.unlockOrderFile((Order2) savable);
        }
        fileController.notifyChange((Observable) savable);
        ((Observable)savable).update();
    }



}
