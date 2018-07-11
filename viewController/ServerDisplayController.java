package viewController;

import Main.main;
import control.DataGetter;
import control.StatusChecker;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.data.Item;
import model.data.ObservableItem;
import model.interfaces.Observable;
import model.interfaces.Observer;

import java.util.ArrayList;

/**
 * SeverDisplayController class. Controller for the ServerDisplay.fxml. It has option to make and order, manage order,
 * delete order.
 */
public class ServerDisplayController  extends ControlledScene implements Observer {

    @FXML
    Label newItemsAlert;

    private ArrayList<Item> items;

    private DataGetter dataGetter;
    private StatusChecker statusChecker;

    /**
     * Initialize this scene and appropriate controller. Add all ready items into items list.
     */
    @FXML
    public  void initialize() {
        statusChecker = new StatusChecker();
        dataGetter = new DataGetter();
        items = new ArrayList<>();
        items = dataGetter.getReadyItems();
        newItemsAlert.setText(items.size() + " items is ready to be deliver!");
    }

    /**
     * Switch to MakeAndOrder.fxml scene when Make An Order button is clicked.
     */
    public void makeAnOrder_Clicked() {
        sceneController.switchScene(main.MakeOrderSceneID);
    }

    /**
     * Switch back to the previous scene.
     */
    public void exit() {
        sceneController.switchToPrevScene();
    }

    /**
     * Switch to DeliverItem.fxml scene when Deliver An Item button is clicked.
     */
    public void deliverItemButton_Clicked() {
        sceneController.switchScene(main.ItemDeliveryDisplayID);
    }

    /**
     * Switch to ManageOrder1.fxml scene when Manage An Order button is clicked.
     */
    public void manageOrderButton_Clicked() {
        sceneController.switchScene(main.ChooseOrderDisplayID);
    }

    /**
     * Update the ready items list, if and item is delete or served, remove it from the list. Otherwise add it to the
     * list.
     * @param observable an Observable object
     */
    public void update(Observable observable) {
        if(observable instanceof ObservableItem) {
            if(items.contains(observable)) {
                if(statusChecker.isDeleteItem((ObservableItem) observable))
                    items.remove(observable);
                else if(((ObservableItem) observable).getItemInfo().isServed())
                    items.remove(observable);
            }
            else if(!statusChecker.isDeleteItem((ObservableItem) observable))
                items.add((ObservableItem) observable);
        }
        newItemsAlert.setText(items.size() + " items is ready to be deliver!");
    }

}
