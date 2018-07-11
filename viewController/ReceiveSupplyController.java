package viewController;

import control.DataGetter;
import control.SupplyModifier;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.beans.value.ChangeListener;

import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Supply2;
import model.interfaces.Observable;
import model.interfaces.Observer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * ReceiveSupplyController class. Controller ReceiveSupply.fxml. It allow user to add the quantity to existing supply and
 * create new supply.
 */
public class ReceiveSupplyController extends ControlledScene implements Observer{

    @FXML
    ListView<String> supplyListView;
    @FXML
    TextField supplyNameTextField;
    @FXML
    TextField quantityTextField;
    @FXML
    Label userMsgLabel;
    @FXML
    Label quantityNoteLabel;

    private boolean state;
    private boolean validText;
    private int quantity;

    private DataGetter dataGetter;
    private SupplyModifier supplyModifier;
    private ArrayList<Supply2> currentSupplies;

    private ListViewMouseEvent listViewMouseEvent;

    /**
     * Initialize the scene and appropriate controllers. Display all the supplies.
     */
    @FXML
    public void initialize() {
        dataGetter = new DataGetter();
        supplyModifier = new SupplyModifier();
        state = true;
        validText = false;
        currentSupplies = dataGetter.getCurrentSupplies();
        quantity = 0; // 0 means not a valid input
        listViewMouseEvent = new ListViewMouseEvent(supplyListView);

        supplyNameTextField.textProperty().addListener(new checkSupplyNameChangeListener());
        quantityTextField.textProperty().addListener(new checkSupplyQuantityChangeListener());
        userMsgLabel.setText("");
        quantityNoteLabel.setText("");
        supplyListView.setOnMouseClicked(listViewMouseEvent);

        updateSupplyListView();
    }

    /**
     * Change the state.
     */
    public void changeState(){
        state = !state;
        if(!state) {
            supplyNameTextField.setText("");
        }
        validText = false;
    }

    /**
     * Switch to the Add New Supply tab to add new supply.
     */
    public void switchToAddNewSupplyTab(){
        refreshSupplyListViewSelection();

    }

    /**
     * Add quantity to supply or add new supply base on the state.
     */
    public void doneButtonClick(){
        if(state){
            addCurrentSupply();
            refreshSupplyListViewSelection();
        }
        else{
            addNewSupply();
        }
    }

    /**
     * Save the new supply if the input is valid.
     */
    @FXML
    private void addNewSupply(){
        if(validText){
            if(quantity>0) {
                String name = String.join(" ", supplyNameTextField.getText().trim().split("\\s+"));
                supplyModifier.createSupply(name, quantity);
                quantityNoteLabel.setText("Added: " + supplyNameTextField.getText() + " Quantity: " + quantity);

                supplyNameTextField.setText("");
                quantityTextField.setText("");

            }
            else {
                quantityNoteLabel.setText("Please enter a valid number.");
            }
        }
        else{
            if(supplyNameTextField.getText().equals("")){
                userMsgLabel.setText("Please enter a supply name.");
            }
        }


    }

    /**
     * Save the change to the quantity of a current supply.
     */
    @FXML
    private void addCurrentSupply(){
        String supplyName = supplyListView.getSelectionModel().getSelectedItem();
        if(quantity > 0 && supplyName != null){
            supplyName = getSupplyName(supplyName);
            supplyModifier.addQuantityToSupply(supplyName, quantity);
            quantityNoteLabel.setText(supplyName + " added " + quantity);
            quantityTextField.setText("");
            updateSupplyListView();
        }
        else if(supplyName == null) {
            quantityNoteLabel.setText("Please select an Item to continue..");
        }
        else {
            quantityNoteLabel.setText("Please enter a valid number.");
        }
    }

    /**
     *
     * @param listViewItem a list view item
     * @return a Supply name.
     */
    private String getSupplyName(String listViewItem){
        int index = listViewItem.indexOf("*");
        return listViewItem.substring(0, index-4);

    }

    /**
     * Update the display to display new Supply and their new quantities.
     */
    private void updateSupplyListView(){
        Collections.sort(currentSupplies);
        supplyListView.getItems().clear();
        for(Supply2 supply : currentSupplies){
            if(supply != null) {
                String s = supply.getName() + "\n\t\t\t" +
                        "*Quantity: " + supply.getQuantity() + "\n\t\t\t" +
                        "*Quantity Needed: " + supply.getTotalQuantityNeeded() + "\n\t\t\t" +
                        "*Request Amount: " + supply.getRequestAmount();
                supplyListView.getItems().add(s);
            }
        }
    }

    /**
     * Refresh the selection of the a Supply in the list view.
     */
    private void refreshSupplyListViewSelection(){
        supplyListView.getSelectionModel().clearSelection();
        ((ListViewMouseEvent)supplyListView.getOnMouseClicked()).updateSelectedIndicesArray();
    }

    /**
     * Check if the Supply name is valid.
     * @param name an input from the user
     */
    private void checkSupplyName(String name){
        if(name.equals("")){
            userMsgLabel.setText("");
            validText = false;
        }
        else if(!name.matches("[A-Za-z][A-Za-z ]*")){
            userMsgLabel.setText("Non-valid Name.");
            validText = false;
        }
        else {
            name = String.join(" ", name.split("\\s+"));
            for (Supply2 s : currentSupplies) {
                if (s.getName().equalsIgnoreCase(name.trim())) {
                    userMsgLabel.setText("Supply already exists. ("+ s +")\nPlease add to current supply.");
                    validText = false;
                    return;
                }
            }
            userMsgLabel.setText("");
            validText = true;
        }
    }

    /**
     * Check if the quantity input is valid.
     * @param s an input from the user
     */
    private void checkSupplyQuantity(String s){
        if(s.equals("")){
            quantity = 0;
        }
        else if(s.matches("\\d+")){
            quantity = Integer.parseInt(s);
            quantityNoteLabel.setText("");
        }

    }

    /**
     * Update the supply list view when a new Supply is added or changed.
     * @param observable an Observable object
     */
    @Override
    public void update(Observable observable) {
        if(observable instanceof Supply2){
            currentSupplies.remove(observable);
            currentSupplies.add((Supply2)observable);
            updateSupplyListView();
        }
    }

    /**
     * Switch back to the previous scene.
     */
    public void backButton() {
        sceneController.switchToPrevScene();
    }

    /**
     * Open the pop when the Edit button is pressed to edit the request amount.
     */
    public void edit(){
        if(supplyListView.getSelectionModel().getSelectedItem() != null) {
            String name = getSupplyName(supplyListView.getSelectionModel().getSelectedItem());
            try {
                FXMLLoader myLoader = new FXMLLoader(ItemCustomizer.class.getResource("/uiFxml/ManageSupply.fxml"));
                Parent loadScreen = myLoader.load();
                ManageSupplyController manageSupplyController = myLoader.getController();
                Scene scene = new Scene(loadScreen);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setTitle("Edit the Request Amount");
                manageSupplyController.setSupplyToEdit(name);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
            } catch (IOException e) {
//                System.err.println("Pop up doesn't load..");
            }
        }
    }

    /**
     * Use for supplyNameTextField.
     */
    private class checkSupplyNameChangeListener implements ChangeListener<String>{
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            checkSupplyName(newValue);
        }
    }

    /**
     * Use for quantityTextField.
     */
    private class checkSupplyQuantityChangeListener implements ChangeListener<String>{
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            checkSupplyQuantity(newValue);
        }
    }

    /**
     * Use for the supplyListView mouse event.
     */
    private class ListViewMouseEvent implements EventHandler<MouseEvent> {
        ListView<String> listView;
        ArrayList<Integer> prevSelected;

        ListViewMouseEvent(ListView<String> listView){
            this.listView = listView;
            this.prevSelected = new ArrayList<>();
            updateSelectedIndicesArray();
        }

        @Override
        public void handle(MouseEvent event) {
            Integer focused = listView.getFocusModel().getFocusedIndex();

            if(prevSelected.contains(focused)){
                listView.getSelectionModel().clearSelection(focused);
            }

            updateSelectedIndicesArray();
        }

        private void updateSelectedIndicesArray(){
            prevSelected.clear();
            prevSelected.addAll(listView.getSelectionModel().getSelectedIndices());
        }
    }
}
