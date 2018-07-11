package viewController;

import Main.main;
import control.DataGetter;
import control.LockFileController;
import control.OrderModifier;
import control.StatusChecker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import model.data.Order2;
import model.interfaces.Observable;
import model.interfaces.Observer;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * ManageOrderController class. Controller for ManageOrder1.fxml. It display all available order and it allow user to
 * make payment, edit, delete for a chosen order.
 */
public class ManageOrderController extends ControlledScene implements Observer{
    @FXML
    ListView orderList;
    @FXML
    Button delete;

    private HashMap<Integer,Order2> orders;

    private DataGetter dataGetter;
    private StatusChecker statusChecker;
    private LockFileController lockFileController;
    private OrderModifier orderModifier;

    /**
     * Initialize the scene and necessary controllers. Display the orders.
     */
    public void initialize(){
        dataGetter = new DataGetter();
        statusChecker = new StatusChecker();
        orders = new HashMap<>();
        orderModifier = new OrderModifier();
        lockFileController = new LockFileController();
        for(Order2 order: dataGetter.getCurrentActiveOrders()){
            orders.put(order.getOrderNumber(), order);
        }

        updateOrderView();
    }

    /**
     * Update the orderList base on the order status. When someone else is editing the order, it cannot be modify. Add
     * (Editing...) next to the order to reflect this.
     */
    private void updateOrderView() {
        orderList.getItems().clear();
        ArrayList<String> orderNames  = new ArrayList<>();
        for(Integer orderNumber: orders.keySet()) {
            if(!(statusChecker.isEditableOrder(orderNumber)))
                orderNames.add("Table Number: " + orders.get(orderNumber).getTableNumber()
                        + " ----- Order Number: " + orderNumber + " (Editing...)");
            else
                orderNames.add("Table Number: " + orders.get(orderNumber).getTableNumber()
                        + " ----- Order Number: " + orderNumber);
        }
        for(String orderName: orderNames) {
            orderList.getItems().add(orderName);
        }
    }

    /**
     * Switch to PaymentDisplay.fxml for make payment for the chosen order if the chosen order is editable.
     */
    public void pay() {
        if(orderList.getSelectionModel().getSelectedItem() != null) {
            String nameOnList = (String) orderList.getSelectionModel().getSelectedItem();
            int orderNumber = Integer.parseInt(nameOnList.split("\\D+")[2]);
            if(statusChecker.isEditableOrder(orderNumber)) {
                Order2 chosenOrder = orders.get(orderNumber);
                PaymentDisplayController paymentDisplayController = (PaymentDisplayController)
                        sceneController.getController(main.PaymentDisplayID);
                paymentDisplayController.serOrder(chosenOrder);
                sceneController.switchScene(main.PaymentDisplayID);
                lockFileController.lockFile(chosenOrder);
            }
        }
    }

    /**
     *  Switch to ManageOrderController2.fxml for editing the order if the chosen order is editable.
     */
    public void edit() {
        if(orderList.getSelectionModel().getSelectedItem() != null) {
            String nameOnList = (String) orderList.getSelectionModel().getSelectedItem();
            int orderNumber = Integer.parseInt(nameOnList.split("\\D+")[2]);
            if(statusChecker.isEditableOrder(orderNumber)) {
                Order2 chosenOrder = orders.get(orderNumber);
                ManageOrderController2 controller =
                        (ManageOrderController2) sceneController.getController(main.EditOrderDisplayID);
                controller.intiView(chosenOrder);
                sceneController.switchScene(main.EditOrderDisplayID);
                lockFileController.lockFile(chosenOrder);
            }
        }
    }

    /**
     *  Delete the chosen order if the chosen order is editable.
     */
    public void delete() {
        if(orderList.getSelectionModel().getSelectedItem() != null) {
            String nameOnList = (String) orderList.getSelectionModel().getSelectedItem();
            int orderNumber = Integer.parseInt(nameOnList.split("\\D+")[2]);
            if(statusChecker.isEditableOrder(orderNumber)) {
                orderList.getItems().remove(nameOnList);
                orderModifier.deleteOrder(orderNumber);
            }
        }
    }

    /**
     * Switch back to the previous scene.
     */
    public void back() {
        sceneController.switchToPrevScene();
    }

    /**
     * Update the orders HashMap if an order is deleted, remove it from the list, else add it to the HashMap.
     * @param observable an Observable object.
     */
    @Override
    public void update(Observable observable) {
        if(observable instanceof Order2){
            if(statusChecker.isDeleteOrder((Order2)observable))
                orders.remove(((Order2)observable).getOrderNumber());
            else
                orders.put(((Order2)observable).getOrderNumber(), (Order2)observable);
            updateOrderView();
        }
    }
}
