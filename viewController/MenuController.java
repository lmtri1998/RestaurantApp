package viewController;

import Main.main;
import control.DataGetter;
import control.MenuItemController;
import control.StatusChecker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import model.data.Meal;
import model.data.ObservableItem;
import model.interfaces.Observable;
import model.interfaces.Observer;

import java.util.ArrayList;
import java.util.Collections;

/**
 * MenuController class. Controller for Menu.fxml. it display all the menu item and have buttons that allow actions on
 * a chosen Menu Item.
 */
public class MenuController extends ControlledScene implements Observer {

    @FXML
    ListView<String> menuList;
    @FXML
    Button create;
    @FXML
    Button edit;
    @FXML
    Button delete;
    @FXML
    Button back;

    private ArrayList<Meal> items;
    private DataGetter dataGetter;
    private MenuItemController menuItemController;
    private StatusChecker statusChecker;

    /**
     * Initialize the scene and appropriate controllers. Display all MenuItems.
     */
    @FXML
    public void initialize() {
        statusChecker = new StatusChecker();
        dataGetter = new DataGetter();
        menuItemController = new MenuItemController();
        items = dataGetter.getMealMenuItems();
        refresh();
    }

    /**
     * Refresh the scene.
     */
    private void refresh() {
        menuList.getItems().clear();
        Collections.sort(items);
        for(Meal itemName: items) {
            menuList.getItems().add(itemName.getName());
        }
    }

    /**
     * Switch to AddNewMenuItem.fxml to create a new MenuItem.
     */
    public void create() {
        sceneController.switchScene(main.AddMenuItemDisplayID);

    }

    /**
     * Switch to the ManageMenuItem.fxml scene. Give ManageMenuItemController a Menu Item to edit.
     */
    public void edit() {
        if(!menuList.getSelectionModel().getSelectedItems().isEmpty()) {
            int index = menuList.getSelectionModel().getSelectedIndex();
            ManageMenuItemController controller =
                    (ManageMenuItemController) sceneController.getController(main.EditMenuItemDisplayID);
            controller.updateMenuItem(items.get(index));
            sceneController.switchScene(main.EditMenuItemDisplayID);
        }
    }

    /**
     * Delete the chosen MenuItem.
     */
    public void delete() {
        if(menuList.getSelectionModel().getSelectedItem() != null) {
            String chosenItemName = menuList.getSelectionModel().getSelectedItem();
            menuList.getItems().remove(chosenItemName);
            menuItemController.deleteMenuItem(menuList.getSelectionModel().getSelectedItem());
        }
    }

    /**
     * Update the items list. If an item is deleted, remove it from the list, otherwise add it to the list. Refresh the
     * screen to display the change.
     * @param observable an Observable object.
     */
    public void update(Observable observable) {
        if(observable instanceof  Meal) {
            if(statusChecker.isDeleteItem((ObservableItem) observable))
                items.remove(observable);
            else {
                items.remove(observable);
                items.add((Meal) observable);
            }
            refresh();
        }
    }

    public void exit() {
        sceneController.switchToPrevScene();
    }

}
