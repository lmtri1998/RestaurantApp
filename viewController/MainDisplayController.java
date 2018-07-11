package viewController;

import Main.main;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * MainDisplayController class. The controller for the MainDisplay.fxml. It switch to different scene depends on the
 * button clicked.
 */
public class MainDisplayController extends ControlledScene{
    @FXML
    Label restaurantName;

    /**
     * Switch to SeverDisplay.fxml when Server button is clicked.
     */
    public void serverButton_Clicked() {
        sceneController.switchScene(main.ServerDisplayID);
    }

    /**
     * Switch to ManagerDisplay.fxml when Manager button is clicked.
     */
    public void managerButton_Clicked() {
        sceneController.switchScene(main.ManagerDisplayID);
    }

    /**
     * Switch to KitchenDisplay.fxml when Kitchen button is clicked.
     */
    public void kitchenButton_Clicked() {
        sceneController.switchScene(main.KitchenDisplayID);
    }

    /**
     * Quit the program when the quit button is clicked.
     */
    public void quit() {
        Stage stage = (Stage) restaurantName.getScene().getWindow();
        stage.close();
    }
}
