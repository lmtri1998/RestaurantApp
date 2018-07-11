package viewController;

import control.CustomerItemController;
import control.DataGetter;
import control.StatusChecker;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import model.data.Item;
import model.data.ObservableItem;
import model.interfaces.Observable;
import model.interfaces.Observer;

import java.util.ArrayList;

/**
 * ConfirmDeliveredItemController class. It controls the DeliverItem.fxml scene which handle the confirmation of
 * delivery of items.
 */
public class ConfirmDeliveredItemController extends ControlledScene implements Observer{
    @FXML
    private ListView<String> itemList;

    private DataGetter dataGetter;
    private StatusChecker statusChecker;
    private CustomerItemController customerItemController;
    private ArrayList<Item> items;

    /**
     * Initialize the DeliverItem.fxml scene and all the controllers needed. Display the items that is ready to be
     * deliver.
     */
    @FXML
    public void initialize() {
        dataGetter = new DataGetter();
        statusChecker = new StatusChecker();
        customerItemController = new CustomerItemController();
        items = dataGetter.getReadyItems();
        updateListView();
    }

    /**
     * Update the items list. If item has been deleted or served , remove it from the list. If it ready but not serve
     * add it to the items list.
     * @param observable an Observable object.
     */
    public void update(Observable observable) {
        if(observable instanceof ObservableItem) {
            if(statusChecker.isDeleteItem((ObservableItem)observable))
                items.remove(observable);
            else if(((ObservableItem) observable).getItemInfo().isReady() &&
                    !((ObservableItem) observable).getItemInfo().isServed())
                items.add((ObservableItem) observable);
            else if (((ObservableItem) observable).getItemInfo().isServed())
                items.remove(observable);
        }
        updateListView();
    }

    /**
     * Return an array of string that has the item name and its table number in each string.
     * @param items list of all items that is ready to be delivered.
     * @return an Array of String that has the item name and table number.
     */
    private ArrayList<String> displayItems(ArrayList<Item> items) {
        ArrayList<String> result = new ArrayList<>();
        for (Item i : items) {
            result.add(i.getName()+ " (Table number: " + Integer.toString(i.getItemInfo().getTableNumber()) + ")");
        }
        return result;
    }

    /**
     * Update and Display all ready items and their table number to the scene.
     */
    private void updateListView() {
        itemList.getItems().clear();
        ArrayList<String> displayItemsToConfirm = displayItems(items);
        for (String s : displayItemsToConfirm) {
            itemList.getItems().add(s);
        }
    }

    /**
     * Return the the previous scene upon the click on the back button.
     */
    @FXML
    private void backButton() {
        sceneController.switchToPrevScene();
    }

    /**
     * Confirm the delivery of a chosen item upon the click on the done button.
     */
    @FXML
    private void doneButton() {
        ObservableList<Integer> selectedItemsIndices;
        if(itemList.getSelectionModel().getSelectedIndices() != null) {
            selectedItemsIndices = itemList.getSelectionModel().getSelectedIndices();
            for (Integer i : selectedItemsIndices) {
                customerItemController.confirmDelivery(items.get(i).getItemInfo().getItemNumber());
            }
        }
    }
}
