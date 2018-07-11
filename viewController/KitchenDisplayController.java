package viewController;

import control.CustomerItemController;
import control.DataGetter;
import control.StatusChecker;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.*;

import javafx.scene.input.MouseEvent;
import model.data.*;
import model.interfaces.Observable;
import model.interfaces.Observer;

/**
 * The kitchen display class.
 *
 * This represents the kitchen display. It can prepare items and finish cooking the items.
 */
public class KitchenDisplayController extends  ControlledScene implements Observer {

    private DataGetter dataGetter;
    private StatusChecker statusChecker;
    private CustomerItemController customerItemController;

    @FXML
    Label firstLabel;
    @FXML
    Label secondLabel;
    @FXML
    Label thirdLabel;
    @FXML
    Label fourthLabel;
    @FXML
    Label msgLabel;
    @FXML
    Label warningLabel;

    // Widgets used in here
    @FXML
    ListView<String> firstListView;
    @FXML
    ListView<String> secondListView;
    @FXML
    ListView<String> thirdListView;
    @FXML
    ListView<String> fourthListView;

    private Label[] labels;
    private ListView<String>[] listViews;
    private Order2[] orders;
    private HashMap<Integer, Order2> pendingOrders;


    /**
     * Initializes the view of the kitchen display. Initialize all the instance variable and get a list of
     * active orders from the date base.
     */
    @FXML
    public void initialize() {
        pendingOrders = new HashMap<>();
        dataGetter = new DataGetter();
        statusChecker = new StatusChecker();
        customerItemController = new CustomerItemController();
        orders  = new Order2[4];
        for(Order2 order : dataGetter.getCurrentActiveOrders()){
            if(!statusChecker.isReadyOrder(order.getOrderNumber())) {
                pendingOrders.put(order.getOrderNumber(), order);
            }
        }
        labels = new Label[]{firstLabel ,secondLabel, thirdLabel, fourthLabel};
        listViews = new ListView[]{firstListView ,secondListView, thirdListView, fourthListView}; // I ASSIGN THIS SO I KNOW
        for(int i = 0 ; i < listViews.length ; i++){
            listViews[i].getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            listViews[i].setOnMouseClicked(new ListViewMouseEvent(listViews[i]));
        }
        warningLabel.setText("");
        refresh();
    }

    /**
     * Acknowledges all selected item using the controller. Updates the labels and listViews when the button is
     * clicked.
     */
    public void seenItemAction(){
        warningLabel.setText("");
        for(int i = 0 ; i < 4 ; i++){
            if(orders[i] == null || !statusChecker.isEditableOrder(orders[i].getOrderNumber())){
                break;
            }
            ArrayList<Item> orderItems = getUncookedItems(orders[i].getItems());
            for(int k : listViews[i].getSelectionModel().getSelectedIndices()){
                customerItemController.acknowledgeItem(orders[i].getOrderNumber(),
                                                        orderItems.get(k).getItemInfo().getItemNumber());
            }
        }
        clearListViewSelection();
        refreshListViewHandler();
    }

    /**
     * Finishes items that are selected. An item can be finished without being acknowledged, however, it cannot be
     * finished if the previous order hasn't been finished already. Refresh the view upon clicking the button
     */
    public void finishItemAction(){
        int checkIndex = -1;
        int notCheckedItemsNum = 0; // number of items that cannot be prepared right now
        warningLabel.setText("");
        for(int i = 0 ; i < 4 ; i++){
            if(orders[i] == null) {
                break;
            }
            else if(i != 0 && (!statusChecker.isSeenOrder(orders[i-1].getOrderNumber()) || checkIndex != - 1) ){
                // only set it when first found a unseen order
                if(checkIndex == -1){
                    checkIndex = i-1;
                }
                notCheckedItemsNum += listViews[i].getSelectionModel().getSelectedItems().size();
                // skip the rest of the selected items
                continue;
            }
            // if the order is locked, don't finish anything yet
            if(statusChecker.isEditableOrder(orders[i].getOrderNumber())) {
                ArrayList<Item> orderItems = getUncookedItems(orders[i].getItems());
                for (int k : listViews[i].getSelectionModel().getSelectedIndices()) {
                    customerItemController.readyItem(orders[i].getOrderNumber(),
                                                     orderItems.get(k).getItemInfo().getItemNumber());
                }
            }
        }
        if(notCheckedItemsNum != 0 && checkIndex != -1){
            warningLabel.setText("Order not prepared: #" + orders[checkIndex].getOrderNumber() + ". Please prepare it first");
        }
        clearListViewSelection();
        refreshListViewHandler();
    }

    /**
     * Refresh the list view and the screen.
     */
    private void refresh(){
        if(pendingOrders.size() == 0){
            msgLabel.setText("No Active Orders....");
        }
        else{
            msgLabel.setText("Total Number of Order: " + pendingOrders.size());
        }

        Arrays.fill(orders, null);
        int counter = pendingOrders.size() >= 4 ? 4 : pendingOrders.size();
        Integer[] orderNumbers = pendingOrders.keySet().toArray(new Integer[pendingOrders.size()]);
        Arrays.sort(orderNumbers);
        for(int i = 0 ; i < counter ; i++){
            orders[i] = pendingOrders.get(orderNumbers[i]);
        }
        for(int i = 0 ; i < 4 ; i++){
            updateOrderView(orders[i], listViews[i], labels[i]);
        }
    }

    /**
     * Updates the screen according to the notifying object type. If the notifying order is a deleted order or
     * the notify order is a ready order, do nothing. Other wise append to the pending order list and update
     * @param observable the Observable that notifies this update
     */
    @Override
    public void update(Observable observable) {
        if(observable instanceof Order2){
            Order2 newOrder = (Order2)observable;
            if(statusChecker.isDeleteOrder(newOrder) || statusChecker.isReadyOrder(newOrder.getOrderNumber())){
                pendingOrders.remove(newOrder.getOrderNumber());
            }
            else{
                pendingOrders.put(newOrder.getOrderNumber(), newOrder);
            }
            refresh();
        }
    }


    /**
     * Refresh the list view controls
     */
    private void refreshListViewHandler(){
        for(ListView<String> listView : listViews ){
            ((ListViewMouseEvent)listView.getOnMouseClicked()).updateSelectedIndicesArray();
        }
    }

    /**
     * Updates the list view
     * @param order the given order
     * @param listView the given list view
     * @param orderLabel the order label
     */
    private void updateOrderView(Order2 order, ListView<String> listView, Label orderLabel){
        listView.getItems().clear();
        if(order == null || statusChecker.isReadyOrder(order.getOrderNumber())){
            orderLabel.setText("");
        }
        else{
            ArrayList<Item> items = getUncookedItems(order.getItems());
            for(Item item : items){
                String itemString = item.getName() + (item.getItemInfo().isSeen() ? " (preparing..)" : "");
                String addon = "";
                String subtraction = "";
                for(String supply : item.getIngredients().getAddition().keySet()){
                    addon += supply + "  ";
                }
                for(String supply : item.getIngredients().getSubtraction().keySet()){
                    subtraction += supply + "  ";
                }
                // not appended if addon or subtraction is empty
                itemString += !addon.equals("")?("\n\t+" + addon) : "";
                itemString += !subtraction.equals("")?("\n\t-" + subtraction):"";
                itemString += !item.getItemInfo().getAdditionalRequest().equals("")? "\n\t*" +
                        item.getItemInfo().getAdditionalRequest() : "";
                listView.getItems().add(itemString);
            }
            String text = "#"+Integer.toString(order.getOrderNumber());
            orderLabel.setText( !statusChecker.isEditableOrder(order.getOrderNumber())?"Editing.." : text);
        }
        refreshListViewHandler();
    }

    /**
     * Returns a list of uncooked items for display
     * @param items the list of all items
     * @return the list of uncooked items
     */
    private ArrayList<Item> getUncookedItems(ArrayList<Item> items){
        ArrayList<Item> newItems = new ArrayList<>();
        for(Item item : items){
            if(!item.getItemInfo().isReady()){
                newItems.add(item);
            }
        }
        Collections.sort(newItems);
        return newItems;
    }

    /**
     * Clear the list view selection for smooth control.
     */
    private void clearListViewSelection(){
        for (ListView<String> listView : listViews){
                listView.getSelectionModel().clearSelection();
                ((ListViewMouseEvent)listView.getOnMouseClicked()).updateSelectedIndicesArray();
        }
    }

    /**
     * Go back one scene
     */
    public void backButton_Clicked() {
        refreshListViewHandler();
        sceneController.switchToPrevScene();
    }

    /**
     * This class is an inner class used for list view mouse handling.
     */
    private class ListViewMouseEvent implements EventHandler<MouseEvent>{
        ListView<String> listView;
        ArrayList<Integer> prevSelected;

        /**
         * Constructs a handler with given list view
         * @param listView the listView
         */
        ListViewMouseEvent(ListView<String> listView){
            this.listView = listView;
            this.prevSelected = new ArrayList<>();
            updateSelectedIndicesArray();
        }

        /**
         * Handles the mouse click. Clear all other list view selection on the screen.
         * @param event the event
         */
        @Override
        public void handle(MouseEvent event) {
            Integer focused = listView.getFocusModel().getFocusedIndex();

            if(prevSelected.contains(focused)){
                listView.getSelectionModel().clearSelection(focused);
            }

            // clear all other listView selection
            for (ListView<String> listView : listViews){
                if(!listView.equals(this.listView)){
                    listView.getSelectionModel().clearSelection();
                    ((ListViewMouseEvent)listView.getOnMouseClicked()).updateSelectedIndicesArray();
                }
            }

            updateSelectedIndicesArray();
        }

        /**
         * Update the selected indices array
         */
        private void updateSelectedIndicesArray(){
            prevSelected.clear();
            prevSelected.addAll(listView.getSelectionModel().getSelectedIndices());
        }
    }
}

