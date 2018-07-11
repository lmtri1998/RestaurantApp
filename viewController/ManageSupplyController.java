package viewController;

import control.SupplyModifier;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * ManageSupplyController class. Controller for the ManageSupply.fxml. It is a pop-up that can
 * modify a chosen supply request amount.
 */
public class ManageSupplyController extends ControlledScene {

    @FXML
    TextField requestAmountField;

    private String supplyName;

    private boolean isNumber;

    private SupplyModifier supplyModifier;


    /**
     * Initialize the scene with the appropriate controllers. Add listener to the requestAmountField TextField.
     */
    @FXML
    public void initialize() {
        supplyModifier = new SupplyModifier();
        requestAmountField.textProperty().addListener(new CheckNumberChangeListener());
    }

    /**
     * Set the chosen supplyName.
     * @param supplyName the name of the chosen supply.
     */
    public void setSupplyToEdit(String supplyName) {
        this.supplyName = supplyName;
    }

    /**
     * Save the new request amount if the input is valid.
     */
    @FXML
    private void save() {
        if(validInput() && isNumber) {
            int requestAmount = Integer.parseInt(requestAmountField.getText());
            supplyModifier.editSupply(supplyName, requestAmount);
            Stage stage = (Stage)requestAmountField.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * Close the pop-up window.
     */
    @FXML
    public void back() {
        Stage stage = (Stage) requestAmountField.getScene().getWindow();
        stage.close();
    }

    /**
     * Check if the input is valid. requestAmountField cannot be empty.
     * @return true if valid is true; false otherwise.
     */
    private boolean validInput(){
        boolean valid = true;
        if (requestAmountField.getText().isEmpty()) {
            requestAmountField.setPromptText("Please fill this");
            valid = false;
        }
        return valid;
    }

    /**
     * Check if a string is a number
     * @param value a String object
     */
    private void checkNumber(String value) {
        isNumber = false;
        if(value.matches("\\d+")) {
            if(!value.matches("0\\d*"))
                isNumber = true;
        }

    }

    /**
     * Use for requestAmountField TextField.
     */
    private class CheckNumberChangeListener implements ChangeListener<String> {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            checkNumber(newValue);
        }
    }
}
