package viewController;

import control.DataGetter;
import control.OrderModifier;
import control.StatusChecker;
import control.SupplyModifier;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Item;
import model.data.Meal;
import model.data.ObservableItem;
import model.interfaces.Observable;
import model.interfaces.Observer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is an UI controller for the scene MakeAnOrder.fxml.
 * The MakeAnOrder UI is a user interface that allows the user to
 * order dishes that is on the menu and pass in all the user information
 * to the controllers.
 */
public class MakeAnOrderController extends ControlledScene implements Observer{
    @FXML
    private FlowPane foodMenuPane;
    @FXML
    private VBox orderView;
    @FXML
    private TextField tableNumberTextBox;
    @FXML
    private  Label errorLabel;

    private DataGetter dataGetter;
    private SupplyModifier supplyModifier;
    private StatusChecker statusChecker;
    private OrderModifier orderModifier;
    boolean validInput = true;
    private final int BUTTON_SIZE = 125;

    private HashMap<String, Item> menuItems;


    /**
     * This initialize method will initialize the menu screen
     * to the most up-to-date menu.
     */
    @FXML
    public void initialize(){
        dataGetter = new DataGetter();
        menuItems = new HashMap<>();
        statusChecker = new StatusChecker();
        supplyModifier = new SupplyModifier();
        orderModifier = new OrderModifier();
        for(Meal meal : dataGetter.getMealMenuItems()){
            menuItems.put(meal.getName(), meal);
        }
        refreshMenuView();
    }

    /**
     * This method will create the menu view and create a button
     * for each menu item. This method will create the list
     * of menu from the menuItem list.
     */
    private void refreshMenuView(){
        foodMenuPane.getChildren().clear();
        for(String itemName: menuItems.keySet()){
            Button button = new Button(itemName);
            button.setWrapText(true);
            button.setTextAlignment(TextAlignment.CENTER);
            button.setPrefSize(BUTTON_SIZE, BUTTON_SIZE);
            button.setOnAction(e -> itemSelected(e));
            foodMenuPane.getChildren().add(button);
        }
    }

    /**
     * This method is called when a menu item is being selected.
     * When the item is being selected, this method will display
     * a pop up for the user to choose additional information. If there
     * is not enough supply in the stock, the user will not be able
     * to order this item.
     * @param e - the button that the user clicked on.
     */
    private void itemSelected(ActionEvent e) {
        Button button = (Button)e.getSource();
        Item i = menuItems.get(button.getText());
        if(supplyModifier.haveEnoughSupply(i, false)){
            ItemCustomizer itemCustomizer = new ItemCustomizer();
            if(itemCustomizer.display(i)) {
                String additionalRequest = itemCustomizer.getDescription();
                HashMap<String, Integer> ingredients = itemCustomizer.getIngredients();
                String itemName = itemCustomizer.getItemName();
                String itemInfo = itemInfoToString(itemName, ingredients, additionalRequest);
                if(supplyModifier.haveEnoughSupply(itemInfo, true))
                    addItemToOrderView(itemInfo);
                else
                    messageBox("Don't have enough supplies for addOns!");

            }
        } else{
            messageBox("We ran out of the supply!");
        }
    }

    /**
     * Add a item information the the order display so that the
     * user can check check what they ordered. Also create an option
     * for the user to remove the stuff they just orderd.
     * @param itemInfoText - the iteminfo that the user ordered.
     */
    private void addItemToOrderView(String itemInfoText){
        Label itemInfo = new Label();
        itemInfo.setMinWidth(300);
        itemInfo.setMaxWidth(300);

        itemInfo.setText(itemInfoText);

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> deleteItem(e));

        HBox item = new HBox();
        item.setAlignment(Pos.CENTER_LEFT);
        item.getChildren().addAll(itemInfo, deleteButton);

        orderView.getChildren().add(item);
    }

    /**
     * Convert the info the user input when choosing the menu item to a
     * info string that satisfy the format for ItemConverter.
     * When the ingredient value = -1, the ingredient is needed to be subtracted
     * When the ingredient value =0, there nothing need to be changed.
     * When the ingredient value = 1, extra ingredient on the item
     * When the ingredient value = 2, 2x extra ingredient on the item.
     * When the ingredient value = 3, 3x extra ingredient on the item
     * @param name - name of the menu item
     * @param ingredients - hashmap of ingredients
     * @param additionalRequest - additional request for the item.
     * @return String of item informatino
     */
    private String itemInfoToString(String name, HashMap<String, Integer> ingredients, String additionalRequest){
        String text = name;
        for (String ingredient : ingredients.keySet()){
            int quantity = ingredients.get(ingredient);
            if (quantity < 0){
                text = text + "\n \t -" + ingredient;
            }
            else{
                for(int i = 0; i < quantity; i++){
                    text = text + "\n \t +" + ingredient;
                }
            }
        }
        return additionalRequest.isEmpty() ? text : text + "\n \t *" + additionalRequest;
    }

    /**
     * Delete the item from the orderView and remove the supply that
     * was reserved.
     * @param e -> the item HBox that is needed to be removed from the screen.
     */
    private void deleteItem(ActionEvent e) {
        HBox itemInfo = (HBox) ((Button) e.getSource()).getParent();
        String item = ((Label) itemInfo.getChildren().get(0)).getText();
        supplyModifier.cancelSupplyReserved(item);
        orderView.getChildren().remove(itemInfo);

    }

    /**
     * When the OK button  is being clicked, all the item that is
     * saved to the orderView will be created as a order.
     */
    @FXML
    private void okButton_Clicked() {
        checkUserInput();
        if(validInput) {
            ArrayList<String> itemInfos = new ArrayList<>();
            int tableNumber = Integer.parseInt(tableNumberTextBox.getText());
            for (Node n : orderView.getChildren()) {
                HBox box = (HBox) n;
                itemInfos.add(((Label) box.getChildren().get(0)).getText());
            }
            orderModifier.makeNewOrder(tableNumber, itemInfos);
            back();
        }
    }

    /**
     * This method is called when the user clicked on the done button.
     * Check if the user input on for the table number is correct.
     */
    private void checkUserInput(){
        if (tableNumberTextBox.getText().trim().isEmpty()){
            errorLabel.setText("Enter!");
            validInput = false;
        }else
            checkInput();
    }

    /**
     * This checks if the user's input are correct when the are typing.
     * If the input is not an int, it will show the error.
     */
    @FXML
    private void checkInput(){
        if(tableNumberTextBox.getText().trim().isEmpty()){
            validInput = false;
            errorLabel.setText("");
        }else {
            try {
                Integer.parseInt(tableNumberTextBox.getText().trim());
                validInput = true;
            } catch (NumberFormatException e) {
                validInput = false;
                errorLabel.setText("Number!");
            }
        }
    }

    /**
     * This will exit the scene and get rid of all the reserved
     * supply and exit the scene.
     */
    @FXML
    private void exit(){
        ArrayList<String> itemInfos = new ArrayList<>();
        for (Node n : orderView.getChildren()) {
            HBox box = (HBox) n;
            itemInfos.add(((Label) box.getChildren().get(0)).getText());
        }
        supplyModifier.cancelSupplyReserved(itemInfos);
        back();
    }

    /**exit the scene and clear all user input on the screen
     */
    private void back(){
        orderView.getChildren().clear();
        errorLabel.setText("");
        tableNumberTextBox.setText("");
        sceneController.switchToPrevScene();
    }

    /**
     * A pop up message box that will show the message
     * @param msg - message to show.
     */
    private void messageBox(String msg){
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinWidth(250);

        Label lbl = new Label();
        lbl.setText(msg);

        Button btnOK = new Button();
        btnOK.setText("OK");
        btnOK.setOnAction(e -> stage.close());

        VBox pane = new VBox(20);
        pane.getChildren().addAll(lbl, btnOK);
        pane.setAlignment(Pos.CENTER);

        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.showAndWait();
    }

    /**
     * The override class from observable. If the instance is a menu item,
     * then refresh the menu.
     * @param observable
     */
    @Override
    public void update(Observable observable) {
        if(observable instanceof Item){
            if(statusChecker.isDeleteItem((ObservableItem) observable))
                menuItems.remove(((ObservableItem) observable).getName());
            else
                menuItems.put(((Item) observable).getName(), (Item) observable);
            refreshMenuView();
        }
    }
}
