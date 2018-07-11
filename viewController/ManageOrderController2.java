package viewController;

import control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Item;
import model.data.Meal;
import model.data.Order2;
import model.interfaces.Observable;
import model.interfaces.Observer;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is a view controller for the ManageOrder2.fxml. ManageOrder2.fxml
 * is a scene where it allow the user to edit an order that was previously been
 * placed.
 */
public class ManageOrderController2 extends ControlledScene implements Observer{
    @FXML
    private FlowPane foodMenuPane;
    @FXML
    private Label orderNumberLabel;
    @FXML
    private Label warningLabel;
    @FXML
    private ListView<String> deliveredItemView;
    @FXML
    private ListView<String> pendingItemsView;

    private ArrayList<Item> deliverItemList;
    private ArrayList<Item> pendingItemList;

    private DataGetter dataGetter;
    private OrderModifier orderModifier;
    private StatusChecker statusChecker;
    private LockFileController lockFileController;
    private SupplyModifier supplyModifier;
    private Order2 currentOrder;
    private final int BUTTON_SIZE = 125;

    private HashMap<String, Item> menuItems;


    /**
     * Initialize the view with.
     */
    @FXML
    public void initialize(){
        dataGetter = new DataGetter();
        orderModifier = new OrderModifier();
        statusChecker = new StatusChecker();
        supplyModifier = new SupplyModifier();
        lockFileController = new LockFileController();
        menuItems = new HashMap<>();
        for(Meal meal : dataGetter.getMealMenuItems()){
            menuItems.put(meal.getName(), meal);
        }
        deliveredItemView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        pendingItemsView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        refreshMenuView();
        deliverItemList = new ArrayList<>();
        pendingItemList = new ArrayList<>();
        orderNumberLabel.setText("");
        currentOrder = null;
    }

    /**
     * Exit the scene and unlock the file so other people can edit this order.
     */
    @FXML
    public void back(){
        if(statusChecker.isEmptyOrder(currentOrder))
            orderModifier.deleteOrder(currentOrder.getOrderNumber());
        lockFileController.unlockFile(currentOrder);
        currentOrder = null;
        sceneController.switchToPrevScene();
    }

    /**
     * Refresh the menu view by re-creating all the buttons so
     * that everything is up-to-date
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
     * to order this item. Save the item to the order when the user
     * is finished
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
                if(supplyModifier.haveEnoughSupply(itemInfo, true)) {
                    orderModifier.addItemToOrder(currentOrder.getOrderNumber(),
                            itemInfoToString(itemName, ingredients, additionalRequest));
                }else
                    messageBox("Don't have enough supplies for addOns!");

            }
        } else{
            messageBox("We ran out of the supply!");
        }
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
     * Return the item to the kitchen to remake. The returned item must first be served first.
     * If there is not enough supply to remake the item, then the item will
     * not be able to be return.
     */
    public void returnItem() {
        int index = deliveredItemView.getSelectionModel().getSelectedIndex();
        if(index >= 0) {
            Item item = deliverItemList.get(index);
            if(supplyModifier.haveEnoughSupply(item, true)){
                String reason = textPopUp().toUpperCase();
                orderModifier.returnItem(item.getItemInfo().getItemNumber(), reason);
            }
            else{
                messageBox("There are not enough supply!");
            }
        }
    }

    /**
     * Delete the selected item from the order.
     */
    public void deleteItem() {
        int index = pendingItemsView.getSelectionModel().getSelectedIndex();
        if(index >= 0) {
            Item item = pendingItemList.get(index);
            pendingItemsView.getItems().remove(index);
            orderModifier.deleteItem(item.getItemInfo().getItemNumber());
        }
    }

    /**
     * Edit the selected item that is in the order. If the
     * edited item cannot be made because of not enough supply,
     * the edit will not be saved.
     */
    public void editItem(){
        int index = pendingItemsView.getSelectionModel().getSelectedIndex();
        if(index >= 0) {
            Item item = pendingItemList.get(index);
            if (!item.getItemInfo().isSeen()) {
                ItemCustomizer itemCustomizer = new ItemCustomizer();
                if (itemCustomizer.display(item)) {
                    String additionalRequest = itemCustomizer.getDescription();
                    HashMap<String, Integer> ingredients = itemCustomizer.getIngredients();
                    String oldItem = itemCustomizer.getItemName();
                    String newItem = itemInfoToString(oldItem, ingredients, additionalRequest);
                    supplyModifier.cancelSupplyReserved(oldItem);
                    if(supplyModifier.haveEnoughSupply(newItem, true)) {
                        orderModifier.editItem(item.getItemInfo().getItemNumber()
                                , itemInfoToString(oldItem, ingredients, additionalRequest));
                    }else{
                        supplyModifier.haveEnoughSupply(oldItem, true);
                        messageBox("Not enough supply for your edit!");
                    }
                }
            } else {
                warningLabel.setText("They are preparing the item Already!");
            }
        }
    }

    /**
     * A pop up message box that will show the message
     * @param msg - message to show.
     */
    private void messageBox(String msg){
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Message");
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
     * A pop up message box for the user to enter the reason
     * why the item is being returned.
     * @return the user's input
     */
    private String textPopUp(){
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Reason For Return");
        stage.setMinWidth(500);

        Label lbl = new Label();
        lbl.setText("What is the reason for return?");

        TextField input = new TextField();
        input.setPromptText("Enter the reason ...");

        Label err = new Label();

        Button btnOK = new Button();
        btnOK.setText("OK");
        btnOK.setOnAction(e -> {
            if(input.getText().trim().isEmpty()){
                err.setText("Please Enter the Reason");
            }else{
            stage.close();
            }
        });

        VBox pane = new VBox(5);
        pane.getChildren().addAll(lbl, input, err, btnOK);
        pane.setAlignment(Pos.CENTER);

        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.showAndWait();
        return input.getText();
    }

    /**
     * Initialize the view so that the boxes on the right side will
     * show the informations of the order.
     * @param order - the order that needed to be displayed
     */
    public void intiView(Order2 order){
        ItemConverter itemConverter = new ItemConverter();
        currentOrder = order;
        orderNumberLabel.setText(Integer.toString(order.getOrderNumber()));
        deliveredItemView.getItems().clear();
        pendingItemsView.getItems().clear();
        deliverItemList.clear();
        pendingItemList.clear();
        warningLabel.setText("");
        for(Item item :order.getItems()){
            if(item.getItemInfo().isServed()){
                deliverItemList.add(item);
                deliveredItemView.getItems().add(itemConverter.itemToInfoString(item));
            }
            else{
                pendingItemList.add(item);
                pendingItemsView.getItems().add(itemConverter.itemToInfoString(item));
            }
        }

    }

    /**
     * When a order that is the same as
     * the current folder, update the view. If the observable is
     * an menuItem, update the item selection.
     * @param observable
     */
    @Override
    public void update(Observable observable) {
        if(observable instanceof Order2 && currentOrder != null){
            Order2 order = (Order2) observable;
            if (order.equals(currentOrder))
                intiView(order);
        }else if(observable instanceof Item){
            if(statusChecker.isMenuItem((Item) observable)){
                refreshMenuView();
            }
        }
    }

}
