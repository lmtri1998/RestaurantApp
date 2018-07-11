package viewController;

import control.DataGetter;
import control.MenuItemController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import model.data.*;
import model.interfaces.Observable;
import model.interfaces.Observer;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * ManageMenuItemController class. Controller for ManageMenuItem.fxml which is a scene for editing a MenuItem. It allow
 * user to change description, base ingredient, additions, and subtractions and save it.
 */
public class ManageMenuItemController extends ControlledScene implements Observer{
    @FXML
    Label nameField;
    @FXML
    Label priceField;
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


    private DataGetter dataGetter;

    private MenuItemController menuItemController;
    private final double BUTTON_PADDING   = 6;

    private HashMap<String, Integer> mealIngredient;

    private boolean isNumber;

    /**
     * Initialize this scene with appropriate controllers. Set the listener for quantity and additionQuantity TextField.
     */
    @FXML
    public void initialize() {
        dataGetter = new DataGetter();
        menuItemController = new MenuItemController();
        quantity.textProperty().addListener(new CheckNumberChangeListener());
        additionQuantity.textProperty().addListener(new CheckNumberChangeListener());
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
     * Setting the layout for the scene. Add available ingredients into the chooseIngredients and chooseAvailableAddition
     * ComboBox. Add existing base ingredients, additions and subtractions of the meal to the FlowPanes. Set the nameField,
     *  priceField, and description using the name, price, and description of the meal.
     * @param meal the chosen MenuItem to be edited
     */
    public void updateMenuItem(Item meal) {
        nameField.setText(meal.getName());
        priceField.setText("$ " + meal.getPrice());
        description.setText(meal.getDescription());
        ingredientFlow.setPadding(new Insets(BUTTON_PADDING));
        additionsFlow.setPadding(new Insets(BUTTON_PADDING));
        subtractionsFlow.setPadding(new Insets(BUTTON_PADDING));
        ingredientFlow.setHgap(BUTTON_PADDING);
        ingredientFlow.setVgap(BUTTON_PADDING);
        additionsFlow.setHgap(BUTTON_PADDING);
        additionsFlow.setVgap(BUTTON_PADDING);
        subtractionsFlow.setHgap(BUTTON_PADDING);
        subtractionsFlow.setVgap(BUTTON_PADDING);

        addItemToBoxes(meal);
        mealIngredient = new HashMap<>(meal.getIngredients().getBaseIngredient());

        ArrayList<Button> ingredientList = createButtons(meal.getIngredients().getBaseIngredient());
        ArrayList<Button> additionList = createButtons(meal.getIngredients().getAvailableAddition());
        ArrayList<Button> subtractionList = createButtons(meal.getIngredients().getAvailableSubtraction());

        addButtonToFlowPane(ingredientList, ingredientFlow);
        addButtonToFlowPane(additionList, additionsFlow);
        addButtonToFlowPane(subtractionList,subtractionsFlow);
    }

    /**
     * Add the ingredients from the meal to the appropriate ComboBoxes.
     * @param meal the chosen meal to be edited
     */
    private void addItemToBoxes(Item meal) {
        ArrayList<String> supplyNameList = new ArrayList<>();
        for(Supply2 supply: dataGetter.getCurrentSupplies()) {
            supplyNameList.add(supply.getName());
        }
        ObservableList<String> ingredientNames = FXCollections.observableArrayList(supplyNameList);
        chooseIngredients.setItems(ingredientNames);
        ArrayList<String> remainingIngredient = new ArrayList<>();
        for(String name: ingredientNames) {
            if(!(meal.getIngredients().getAvailableAddition().containsKey(name))) {
                remainingIngredient.add(name);
            }
        }
        ObservableList<String> additionNames = FXCollections.observableArrayList(remainingIngredient);
        chooseAvailableAddition.setItems(additionNames);
        ArrayList<String> remainingSubtractions = new ArrayList<>();
        for(String name: meal.getIngredients().getBaseIngredient().keySet()) {
            if(!(meal.getIngredients().getAvailableSubtraction().containsKey(name))) {
                remainingSubtractions.add(name);
            }
        }
        ObservableList<String> subtractionNames = FXCollections.observableArrayList(remainingSubtractions);
        chooseAvailableSubtraction.setItems(subtractionNames);
    }

    /**
     * Add buttons into a FlowPane.
     * @param buttonList a list of buttons
     * @param flow a FlowPane
     */
    private void addButtonToFlowPane(ArrayList<Button> buttonList, FlowPane flow) {
        for(Button button: buttonList) {
            flow.getChildren().add(button);
        }
    }

    /**
     * Creeat buttons from the ingredients HashMap, each button text contains an ingredient name and its value.
     * @param ingredients the HashMap of ingredient with their names as key and quantity as value.
     * @return a list of buttons
     */
    private ArrayList<Button> createButtons(HashMap<String, Integer> ingredients) {
        ArrayList<Button> buttonList = new ArrayList<>();
        for(String ingredient: ingredients.keySet()) {
            Button button = new Button(ingredient +" "+ ingredients.get(ingredient));
            button.setPrefWidth(100);
            buttonList.add(button);
            button.setOnAction(e -> buttonClick(e));
        }
        return buttonList;
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
            mealIngredient.remove(button.getText().split("\\d+")[0].trim());
        }else if(additionsFlow.getChildren().contains(button)){
            additionsFlow.getChildren().remove(button);
        }else{
            subtractionsFlow.getChildren().remove(button);
            chooseAvailableSubtraction.getItems().add(button.getText().split("\\d+")[0].trim());
        }
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

            Button newButton = new Button(name + " " + quantityOfIngredient);
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
            Button newButton = new Button(name + " " + quantityOfIngredient);
            newButton.setPrefWidth(100);
            newButton.setOnAction(e -> buttonClick(e));
            subtractionsFlow.getChildren().add(newButton);
            chooseAvailableSubtraction.getItems().remove(name);
        }
    }

    /**
     * Save the edited MenuItem with all the new description, base ingredient, additions, and subtractions.
     */
    @FXML
    private void done() {
        if(validInput()) {
            HashMap<String, Integer> ingredients = new HashMap<>();
            HashMap<String, Integer> additions = new HashMap<>();
            HashMap<String, Integer> subtractions = new HashMap<>();
            String name = nameField.getText();
            String newDescription = description.getText();
            addToHashMap(ingredientFlow, ingredients);
            addToHashMap(additionsFlow, additions);
            addToHashMap(subtractionsFlow, subtractions);

            menuItemController.editMeal(name, newDescription, ingredients, additions, subtractions);

            back();
        }
    }

    /**
     * Clear everything and return to the previous scene
     */
    @FXML
    public void back() {
        ingredientFlow.getChildren().clear();
        additionsFlow.getChildren().clear();
        subtractionsFlow.getChildren().clear();
        quantity.clear();
        additionQuantity.clear();
        sceneController.switchToPrevScene();
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
     * Check if all the input are valid. Description and base ingredients cannot be empty.
     * @return true if valid is true; false otherwise.
     */
    private boolean validInput(){
        boolean valid = true;
        if (ingredientFlow.getChildren().isEmpty()){
            warning.setText("Ingredient cannot be empty!");
            valid = false;
        }
        if(description.getText().isEmpty()) {
            description.setPromptText("Please fill this");
            valid = false;
        }
        return valid;
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
     * Use for quantity and additionQuantity TextField.
     */
    private class CheckNumberChangeListener implements ChangeListener<String> {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            checkNumber(newValue);
        }
    }

}
