package viewController;

import control.DataGetter;
import control.MenuItemController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import model.data.Supply2;
import model.interfaces.Observable;
import model.interfaces.Observer;

import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * AddMenuItemController class. This class controls the FXML scene for adding new Menu Item. It the ability to set the
 * to set the new MenuItem name, price, description base ingredients, additions, subtractions and their quantities.
 * The chosen ingredients will be added to the box on the right after one choose a supply and input the appropriate
 * quantity and press enter. Clicking on the buttons that has the ingredient name and quantity will remove it from the
 * list of chosen ingredients for base ingredient, additions, and subtractions.
 */
public class AddMenuItemController extends ControlledScene  implements Observer{
    @FXML
    TextField itemName;
    @FXML
    TextField itemPrice;
    @FXML
    Label warning;
    @FXML
    TextField description;
    @FXML
    ComboBox<String> chooseIngredients;
    @FXML
    ComboBox<String> chooseAvailableAddition;
    @FXML
    ComboBox<String> chooseAvailableSubtraction;
    @FXML
    TextField quantity;
    @FXML
    TextField additionQuantity;
    @FXML
    FlowPane ingredientFlow;
    @FXML
    FlowPane additionsFlow;
    @FXML
    FlowPane subtractionsFlow;
    @FXML
    Button addSubtraction;
    @FXML
    Button done;
    @FXML
    Button back;

    private final double BUTTON_PADDING   = 6;

    private HashMap<String, Integer> mealIngredient;

    private DataGetter dataGetter;

    private MenuItemController menuItemController;

    private boolean isNumber;


    /**
     * Initialize the AddNewMenuItem.fxml scene. Set the listener for quantity and additionQuantity TextField.
     */
    @FXML
    public void initialize() {
        dataGetter = new DataGetter();
        menuItemController = new MenuItemController();
        quantity.textProperty().addListener(new CheckNumberChangeListener());
        additionQuantity.textProperty().addListener(new CheckNumberChangeListener());
        createLayout();
    }


    /**
     *  Update the available ingredients for base ingredients and additions when a new ingredient got added.
     * @param observable an Observable object.
     */
    @Override
    public void update(Observable observable) {
        if(observable instanceof Supply2) {
            chooseIngredients.getItems().add(((Supply2) observable).getName());
            chooseAvailableAddition.getItems().add(((Supply2) observable).getName());
        }
    }

    /**
     * Setting the layout for the scene. Add available ingredients into the chooseIngredients and chooseAvailableAddtion
     * ComboBox.
     */
    private void createLayout() {
        itemName.setPromptText("Enter the name");
        itemPrice.setPromptText("Enter the price");
        description.setPromptText("Enter the description for this item");
        quantity.setPromptText("Enter the quantity");
        additionQuantity.setPromptText("Enter the quantity");
        ingredientFlow.setPadding(new Insets(BUTTON_PADDING));
        additionsFlow.setPadding(new Insets(BUTTON_PADDING));
        subtractionsFlow.setPadding(new Insets(BUTTON_PADDING));
        ingredientFlow.setHgap(BUTTON_PADDING);
        ingredientFlow.setVgap(BUTTON_PADDING);
        additionsFlow.setHgap(BUTTON_PADDING);
        additionsFlow.setVgap(BUTTON_PADDING);
        subtractionsFlow.setHgap(BUTTON_PADDING);
        subtractionsFlow.setVgap(BUTTON_PADDING);

        for(Supply2 supply: dataGetter.getCurrentSupplies()) {
            chooseIngredients.getItems().add(supply.getName());
            chooseAvailableAddition.getItems().add(supply.getName());
        }

        mealIngredient = new HashMap<>();
    }

    /**
     * Create a button that has the name and quantity of the ingredients and add it to the ingredientFlow which contains
     * all base ingredient for this MenuItem. Add the ingredient to the chooseAvailableSubtraction ComboBox that contain
     * all available ingredients that can be subtract from the MenuItem.
     */
    @FXML
    private void addIngredient(){
        if(chooseIngredients.getSelectionModel().getSelectedItem() != null && isNumber) {
            String name = chooseIngredients.getSelectionModel().getSelectedItem();
            String quantityOfIngredient = quantity.getText();
            Button newButton = new Button(name + " " + quantityOfIngredient);
            newButton.setPrefWidth(100);
            newButton.setOnAction(e -> buttonClick(e));
            boolean found = false;
            for (int i = 0; i < ingredientFlow.getChildren().size(); i++) {
                if (((Button) ingredientFlow.getChildren().get(i)).getText().split(" [\\d+]")[0].trim().equals(name)) {
                    ingredientFlow.getChildren().remove((ingredientFlow.getChildren().get(i)));
                    ingredientFlow.getChildren().add(newButton);
                    mealIngredient.put(name, Integer.parseInt(quantityOfIngredient));
                    if(!chooseAvailableSubtraction.getItems().contains(name))
                        chooseAvailableSubtraction.getItems().add(name);
                    found = true;
                }
            }
            if (!found) {
                mealIngredient.put(name, Integer.parseInt(quantityOfIngredient));
                chooseAvailableSubtraction.getItems().add(name);
                ingredientFlow.getChildren().add(newButton);
            }
            quantity.clear();

        }
        isNumber = false;
        quantity.clear();
    }

    /**
     * Create a button that has the name and quantity of the ingredients and add it to the additionsFlow which contains
     * all additional ingredient for this MenuItem.
     */
    @FXML
    private void addAddition(){
        if(chooseAvailableAddition.getSelectionModel().getSelectedItem() != null && isNumber) {
            String name = chooseAvailableAddition.getSelectionModel().getSelectedItem();
            String quantityOfIngredient = additionQuantity.getText();
            Button newButton = new javafx.scene.control.Button(name + " " + quantityOfIngredient);
            newButton.setPrefWidth(100);
            newButton.setOnAction(e -> buttonClick(e));
            boolean found = false;
            for (int i = 0; i < additionsFlow.getChildren().size(); i++) {
                if (((Button) additionsFlow.getChildren().get(i)).getText().split(" [\\d+]")[0].trim().equals(name)) {
                    additionsFlow.getChildren().remove((additionsFlow.getChildren().get(i)));
                    additionsFlow.getChildren().add(newButton);
                    found = true;
                }
            }
            if (!found)
                additionsFlow.getChildren().add(newButton);
            additionQuantity.clear();
        }
        isNumber = false;
        additionQuantity.clear();
    }

    /**
     * Create a button that has the name and quantity of the ingredients and add it to the subtractionsFlow which contains
     * all subtraction ingredients for this MenuItem.
     */
    @FXML
    private void addSubtraction(){
        if(chooseAvailableSubtraction.getSelectionModel().getSelectedItem() != null) {
            String name = chooseAvailableSubtraction.getSelectionModel().getSelectedItem();
            int quantityOfIngredient = mealIngredient.get(name);
            javafx.scene.control.Button newButton = new Button(name + " " + quantityOfIngredient);
            newButton.setPrefWidth(100);
            newButton.setOnAction(e -> buttonClick(e));
            subtractionsFlow.getChildren().add(newButton);
            chooseAvailableSubtraction.getItems().remove(name);
        }
    }

    /**
     * Remove the button that represent an ingredient from the flow.
     * @param e an onAction event, happens when the button is clicked.
     */
    private void buttonClick(ActionEvent e) {
        Button button = (Button) e.getSource();
        if(ingredientFlow.getChildren().contains(button)){
            ingredientFlow.getChildren().remove(button);
            chooseAvailableSubtraction.getItems().remove(button.getText().split("\\d+")[0].trim());
            for(int i = 0; i < subtractionsFlow.getChildren().size(); i++) {
                if(((Button) subtractionsFlow.getChildren().get(i)).getText().equals(button.getText())) {
                    subtractionsFlow.getChildren().remove(subtractionsFlow.getChildren().get(i));
                }
            }
        }else if(additionsFlow.getChildren().contains(button)){
            additionsFlow.getChildren().remove(button);
        }else{
            subtractionsFlow.getChildren().remove(button);
            chooseAvailableSubtraction.getItems().add(button.getText().split("\\d+")[0].trim());
        }
    }

    /**
     * Save the new MenuItem with all the user input name, price, the base ingredient, additions, and subtractions.
     */
    @FXML
    private void done() {
        if(validInput()) {
            HashMap<String, Integer> ingredients = new HashMap<>();
            HashMap<String, Integer> additions = new HashMap<>();
            HashMap<String, Integer> subtractions = new HashMap<>();
            String name = itemName.getText().trim();
            Double price = Double.parseDouble(itemPrice.getText());
            String newDescription = description.getText();
            addToHashMap(ingredientFlow, ingredients);
            addToHashMap(additionsFlow, additions);
            addToHashMap(subtractionsFlow, subtractions);


            menuItemController.createMeal(name, price, newDescription, ingredients, additions, subtractions);

            back();
        }
    }

    /**
     * Check if a string is a number
     * @param s a String object
     */
    private void checkNumber(String s) {
        isNumber = false;
        if(s.matches("\\d+")) {
            if(!s.matches("0\\d*"))
                isNumber = true;
        }

    }

    /**
     * Check if all the input are valid. Name, price, description and base ingredients cannot be empty.
     * @return true if valid is true; false otherwise.
     */
    private boolean validInput(){
        boolean valid = true;
        String decimalPattern = "([0-9]*)\\.?([0-9]*)";
        if (itemName.getText().isEmpty()){
            itemName.setPromptText("Please fill this");
            valid = false;
        }
        if (itemPrice.getText().isEmpty() || !(Pattern.matches(decimalPattern, itemPrice.getText()))) {
            itemPrice.clear();
            itemPrice.setPromptText("Please enter a number");
            valid = false;
        }
        if(description.getText().isEmpty()) {
            description.setPromptText("Please fill this");
            valid = false;
        }
        if(ingredientFlow.getChildren().isEmpty()) {
            warning.setText("Ingredient cannot be empty!");
            valid = false;
        }
        return valid;
    }

    /**
     * Return to the previous scene. Clear all the fields.
     */
    @FXML
    public void back() {
        sceneController.switchToPrevScene();
        itemName.clear();
        itemPrice.clear();
        description.clear();
        warning.setText("");
        ingredientFlow.getChildren().clear();
        additionsFlow.getChildren().clear();
        subtractionsFlow.getChildren().clear();
        quantity.clear();
        additionQuantity.clear();
    }

    /**
     * Take information about ingredients from the buttons inside the flows and add it to the given HashMap.
     * @param flow a FlowPane that contain buttons
     * @param ingredients a HashMap that information from the FlowPane buttons will be added to.
     */
    private void addToHashMap(FlowPane flow, HashMap<String, Integer> ingredients) {
        for(int i = 0; i < flow.getChildren().size(); i++) {
            Button button = (Button) flow.getChildren().get(i);
            String[] name = button.getText().split("[\\d]");
            String[] quantity = button.getText().split(" ");
            ingredients.put(name[0].trim(), Integer.parseInt(quantity[quantity.length - 1]));
        }
    }

    /**
     * Use for quantity and additionQuantity TextField.
     */
    private class CheckNumberChangeListener implements ChangeListener<String> {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            checkNumber(newValue);
        }
    }
}
