package control;

import model.data.ObservableItem;
import model.data.Order2;
import model.data.Supply2;
import model.interfaces.Observable;
import model.interfaces.Observer;

import java.util.ArrayList;

/**
 * The UpdateController class.
 *
 * It is responsible for attaching all observables to the observers by its type, which the update notification
 * of each observables are passed to the correct observer.
 */
public class UpdateController implements UpdateManager {

    ArrayList<Observer> itemObservers;
    ArrayList<Observer> menuItemObservers;
    ArrayList<Observer> supplyObservers;
    ArrayList<Observer> orderObservers;

    /**
     * Constructs a UpdateController
     */
    public UpdateController(){
        itemObservers = new ArrayList<>();
        menuItemObservers = new ArrayList<>();
        supplyObservers = new ArrayList<>();
        orderObservers = new ArrayList<>();
    }

    /**
     * Hook the observable to its Observers. There are four different type of observers. The Item observers which
     * will listen to item changes, the Menu Item observers which listen to the menu items, the supply observers
     * which listen to the supply part and the order observers which listen to the order part.
     * @param observable the observable
     */
    @Override
    public void hookObserver(Observable observable) {
        if(observable instanceof ObservableItem){
            if((((ObservableItem) observable).getItemInfo().getItemNumber() == -1)){
                attachObserverHelper(observable, menuItemObservers);
            }
            else{
                attachObserverHelper(observable, itemObservers);
            }
        }
        else if(observable instanceof Supply2){
            attachObserverHelper(observable, supplyObservers);
        }
        else if(observable instanceof Order2){
            attachObserverHelper(observable, orderObservers);
        }
    }

    /**
     * Adds the item observer.
     * @param observer the item observer
     */
    public void addItemObserver(Observer observer){
        itemObservers.add(observer);
    }

    /**
     * Adds the menu item observer.
     * @param observer the menu item observer
     */
    public void addMenuItemObserver(Observer observer){
        menuItemObservers.add(observer);
    }

    /**
     * Adds the supply observer.
     * @param observer the supply observer
     */
    public void addSupplyObserver(Observer observer){
        supplyObservers.add(observer);
    }

    /**
     * Adds the order observer.
     * @param observer the order observer
     */
    public void addOrderObserver(Observer observer){
        orderObservers.add(observer);
    }

    /**
     * Helper to hook the observable to all of its corresponding observers
     * @param observable the observable
     * @param observers corresponding observers
     */
    private void attachObserverHelper(Observable observable, ArrayList<Observer> observers){
        for(Observer observer : observers){
            observable.attachObserver(observer);
        }
    }
}
