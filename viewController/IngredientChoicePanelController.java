package viewController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * IngredientChoicePanelController class. The controller for the scene that IngredientBox created.
 */
public class IngredientChoicePanelController {
    private boolean hasSubtraction = false;
    private boolean hasAddition = false;
    private final String[] STATUS_TEXT = {"None", "Normal", "Extra", "Extra x2", "Extra x3"};
    private int currentStatus = 1;
    @FXML
    Label itemName;
    @FXML
    Button addBtn;
    @FXML
    Button subtractBtn;
    @FXML
    Label status;

    /**
     * Initialize the controller. Set the current status of the IngredientBox.
     */
    @FXML
    public void initialize() {
        status.setText(STATUS_TEXT[currentStatus]);
    }

    /**
     * Update the scene.
     * @param hasAddition true if item has additions, false otherwise.
     * @param hasSubtraction true if item has subtractions, false otherwise.
     * @param text name of the ingredient.
     */
    public void update(boolean hasAddition, boolean hasSubtraction, String text){
        this.hasSubtraction = hasSubtraction;
        this.hasAddition = hasAddition;
        subtractBtn.setDisable(!hasSubtraction);
        addBtn.setDisable(!hasAddition);
        itemName.setText(text);
    }

    /**
     * Change the status when the add button is clicked.
     */
    @FXML
    public void addBtn_Clicked() {
        changeStatusValue(1);
        if(!hasAddition && STATUS_TEXT[currentStatus].equals("Normal")){
            addBtn.setDisable(true);
        }else if(currentStatus == STATUS_TEXT.length -1){
            addBtn.setDisable(true);
        }
        if (subtractBtn.isDisable()){
            subtractBtn.setDisable(false);
        }
        status.setText(STATUS_TEXT[currentStatus]);
    }

    /**
     * Change the status when the subtraction button is clicked.
     */
    @FXML
    public void subtractBtn_Clicked(){
        changeStatusValue(-1);
        if(!hasSubtraction && STATUS_TEXT[currentStatus].equals("Normal")){
            subtractBtn.setDisable(true);
        }else if(currentStatus == 0){
            subtractBtn.setDisable(true);
        }
        if (addBtn.isDisable()){
            addBtn.setDisable(false);
        }
        status.setText(STATUS_TEXT[currentStatus]);
    }

    /**
     *
     * @return the current status for this IngredientBox
     */
    public int getStatus(){
        return currentStatus-1;
    }

    /**
     * Change that status by 1.
     * @param i either 1 or -1, 1 means got to next status, -1 means go to the previous status.
     */
    private void changeStatusValue(int i){
        if (i>0 && currentStatus < STATUS_TEXT.length-1){
            currentStatus++;
        }
        else if (i<=0 && currentStatus > 0){
            currentStatus--;
        }
    }
}
