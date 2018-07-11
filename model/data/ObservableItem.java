package model.data;

import java.io.IOException;
import java.util.ArrayList;

import model.interfaces.Observable;
import model.interfaces.Observer;

/**
 * The ObservableItem class
 *
 * The ObservableItem class represents an Observable Item that can be attached to the observers. When the object or
 * the status of this Item is modified, it will be able to update all Observers who is attached to this ObservableItem.
 */
public abstract class ObservableItem extends Item implements Observable {
    private ArrayList<Observer> observers;

    /**
     * Constructs a ObservableItem given name and price
     * @param name the name
     * @param price the price
     */
    public ObservableItem(String name, double price){
        super(name, price);
        observers = new ArrayList<>();
    }

    /**
     * Constructs a ObservableItem given a base Item
     * @param baseDish the base Item
     */
    public ObservableItem(ObservableItem baseDish){
        super(baseDish);
        observers = new ArrayList<>();
    }

    /**
     * Notify the update to all the Observers that is attached to this Observable Item
     */
    @Override
    public void update() {
        for(Observer observer : observers){
            observer.update(this);
        }
    }

    /**
     * Serializes the object except the Observers.
     * @param out the output stream
     * @throws IOException if the output stream fails
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(itemInfo);
        out.writeObject(name);
        out.writeObject(ingredients);
        out.writeDouble(price);
        out.writeObject(description);
    }

    /**
     * De-serializes the object except the observers
     * @param in the input stream
     * @throws IOException if the input stream fails
     * @throws ClassNotFoundException if the class doesn't exist
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException{
        itemInfo = (ItemInfo) in.readObject();
        name = (String) in.readObject();
        ingredients = (Ingredients) in.readObject();
        price = in.readDouble();
        description = (String) in.readObject();
        observers = new ArrayList<>();
    }

    /**
     * Adds Observers to this Item
     * @param observer the target Observer
     */
    @Override
    public void attachObserver(Observer observer) {
        observers.add(observer);
    }

    /**
     * Removes Observers from this Item
     * @param observer the target Observer
     */
    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }


}
