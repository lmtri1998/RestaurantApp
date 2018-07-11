package viewController;

import control.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Item;
import model.data.Order2;

import java.util.ArrayList;

/**
 * PaymentDisplayController class. Controller for the PaymentDisplay.fxml. It handle grouping items into different bills
 * and print the receipt.
 */
public class PaymentDisplayController extends ControlledScene {
    @FXML
    ListView<String> itemsList;
    @FXML
    VBox chosenItemsList;
    @FXML
    CheckBox checkNumPeople;
    @FXML
    Button printBill;

    private boolean isNumber = false;
    private Order2 currentOrder;

    private ReceiptController receiptController;
    private LockFileController fileController;
    private OrderModifier orderModifier;

    /**
     * Initialize the scene and appropriate controllers. Display all delivered items in the chosen current order.
     */
    @FXML
    public void initialize() {
        receiptController = new ReceiptController();
        fileController = new LockFileController();
        orderModifier = new OrderModifier();
        itemsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /**
     * Set the current order and add its delivered items to the itemList to be displayed.
     * @param order a chosen Order to have the bill printed.
     */
    public void serOrder(Order2 order) {
        currentOrder = order;
        for(Item item: order.getItems()) {
            if(item.getItemInfo().isServed()) {
                String s = item.getItemInfo().getItemNumber() + " --- Name: " + item.getName() + "\n\t Price: " +
                        item.getPrice() + "\n\t Additions: " + item.getIngredients().getAddition() + "\n\t Subtractions" +
                        item.getIngredients().getSubtraction();
                itemsList.getItems().add(s);
            }
        }
        printBill.setDisable(false);
    }

    /**
     * Group items together to form a bill. Add them to the chosenItemList.
     * @param items list of items in the itemList ListView.
     */
    private void groupItems(ObservableList<String> items){
        GridPane gridPane = new GridPane();
        for(int i = 0; i < items.size(); i++) {
            String item = items.get(i);
            Label itemInfo = new Label();
            itemInfo.setMinWidth(300);
            itemInfo.setMaxWidth(300);
            itemInfo.setText(item);
            gridPane.add(itemInfo, 0, i);
        }
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> removeItemGroup(e));
        GridPane.setRowSpan(deleteButton, items.size());
        gridPane.add(deleteButton, 1, 0);
        gridPane.setStyle("-fx-border-color: black;");
        chosenItemsList.getChildren().add(gridPane);

    }

    /**
     * Break up the group and add the items back to the itemList.
     * @param e a onAction event
     */
    private void removeItemGroup(ActionEvent e) {
        GridPane gridPane = (GridPane) ((Button)e.getSource()).getParent();
        for(Node n :gridPane.getChildren()){
            if (n instanceof Label){
                itemsList.getItems().add((((Label)n).getText()));
            }
        }
        chosenItemsList.getChildren().remove(gridPane);
    }

    /**
     * Group all available items as a bill.
     */
    @FXML
    private void payTogether() {
        groupItems(itemsList.getItems());
        itemsList.getItems().clear();
    }

    /**
     * Select item(s) to form a bill.
     */
    @FXML
    private void selectItem() {
        if(itemsList.getSelectionModel().getSelectedItems() != null) {
            ObservableList<String> items = itemsList.getSelectionModel().getSelectedItems();
            groupItems(items);
            itemsList.getItems().removeAll(items);
        }
    }

    /**
     * Print the receipt. Then prompt the user for payment amount and print the final receipt. Switch back to the previous
     * scene after finishing all the actions. the final receipt will be printed in to a text file.
     */
    @FXML
    private void print() {
        if(!(chosenItemsList.getChildren().isEmpty())) {
            printBill.setDisable(true);
            for (Node node : chosenItemsList.getChildren()) {
                ArrayList<Integer> itemNumbers = new ArrayList<>();
                for (Node n : ((GridPane) node).getChildren()) {
                    if (n instanceof Label) {
                        Label label = (Label) n;
                        String itemNumber = label.getText().split("\n")[0].split(" --- ")[0];
                        itemNumbers.add(Integer.parseInt(itemNumber));
                    }
                }
                receiptController.printReceipt(itemNumbers, checkNumPeople.isSelected());
                double amount = receiptController.calculatePaymentPrice(itemNumbers, checkNumPeople.isSelected());
                double paidAmount = paidAmountPopUp(amount);
                receiptController.printPaymentReceipt(itemNumbers, checkNumPeople.isSelected(), paidAmount);
            }
            orderModifier.finishOrder(currentOrder.getOrderNumber());
            cancel();
        }
    }

    /**
     * A pop-up that request for the payment amount from the user.
     * @param amount the total price of the bill after taxes was calculated.
     * @return the payment amount.
     */
    public double paidAmountPopUp(double amount){
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Enter Paid Amount");
        stage.setMinWidth(500);

        Label lbl = new Label();
        lbl.setText("$ Total: " + amount + " Enter the paid amount");

        TextField input = new TextField();
        input.setPromptText("Enter the amount ... ");
        input.textProperty().addListener(new CheckNumberChangeListener());

        Label err = new Label();

        Button btnOK = new Button();
        btnOK.setText("OK");
        btnOK.setOnAction(e -> {
            if(input.getText().trim().isEmpty()){
                err.setText("Please Enter the Amount");
            }else if(!isNumber) {
                err.setText("Please Enter a Number");
            } else if(Double.parseDouble(input.getText()) < amount){
                err.setText("Payment value cannot be smaller than the total price");
            } else{
                stage.close();
            }
        });

        VBox pane = new VBox(5);
        pane.getChildren().addAll(lbl, input, err, btnOK);
        pane.setAlignment(Pos.CENTER);

        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.showAndWait();
        return Double.parseDouble(input.getText());
    }

    /**
     * Clear all fields and return to the previous scene.
     */
    @FXML
    private void cancel() {
        itemsList.getItems().clear();
        chosenItemsList.getChildren().clear();
        checkNumPeople.selectedProperty().setValue(false);
        fileController.unlockFile(currentOrder);
        currentOrder = null;
        sceneController.switchToPrevScene();
    }

    /**
     * Check if a string is a number.
     * @param value a String object
     */
    private void checkNumber(String value) {
        isNumber = false;
        if(value.matches("\\d+") || value.matches("\\d+\\.\\d+")) {
            isNumber = true;
        }

    }

    /**
     * Use input TextField in the pop-up.
     */
    private class CheckNumberChangeListener implements ChangeListener<String> {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            checkNumber(newValue);
        }
    }
}
