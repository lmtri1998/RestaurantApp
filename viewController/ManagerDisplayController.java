package viewController;

import Main.main;

/**
 * ManagerDisplayController class. Controller for the MangerDisplay. A manager can manage menu item or mange the supply.
 */
public class ManagerDisplayController extends ControlledScene{

    /**
     * Switch to the MenuController.fxml scene for managing menu items.
     */
    public void manageMenu_Clicked() {
        sceneController.switchScene(main.ManageMenuItemDisplayID);
    }

    /**
     * Switch back to the previous scene.
     */
    public void exit() {
        sceneController.switchToPrevScene();
    }

    /**
     * Switch to the ReceiveSupply.fxml scene for managing supply.
     */
    public void ReceiveSupply_Clicked() {
        sceneController.switchScene(main.ReceiveSupplyDisplayID);
    }
}
