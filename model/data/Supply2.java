package model.data;

import model.interfaces.Observable;
import model.interfaces.Observer;
import model.interfaces.Savable;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * The Supply class. It can add/ remove quantity.
 */
public class Supply2 implements Savable , Observable, Comparable<Supply2>{
    private int quantityInStock;
    private int totalQuantityNeeded;
    private String name;
    private int requestAmount;

    private ArrayList<Observer> observers;

    /**
     * Constructs a Supply given its name, total quantity needed, and quantity in stock.
     * @param name of the Supply
     * @param totalQuantityNeeded minimum amount of quantity needed for this Supply.
     * @param quantityInStock quantity this Supply currently has.
     */
    public Supply2(String name, int totalQuantityNeeded, int quantityInStock) {
        this.name = name;
        this.totalQuantityNeeded = totalQuantityNeeded;
        this.quantityInStock = quantityInStock;
        this.requestAmount = 20;
        this.observers = new ArrayList<>();
    }

    /**
     * Add more quantity to this supply.
     * @param supply the additional quantity
     */
    public void addQuantity(int supply) {
        quantityInStock += supply;
    }

    /**
     * Remove some quantity from this supply.
     * @param supply the amount to subtract from this supply's quantity.
     */
    public void removeQuantity(int supply) {
        quantityInStock -= supply;
    }

    /**
     * Set the amount to request for restock.
     * @param requestAmount the amount to request when restock
     */
    public void setRequestAmount(int requestAmount) { this.requestAmount = requestAmount; }

    /**
     * Get current quantity in stock of this supply.
     * @return the quantity in stock of the Supply
     */
    public int getQuantity() {
        return this.quantityInStock;
    }

    /**
     * Get total quantity needed for this supply.
     * @return the total quantity needed for this Supply
     */
    public int getTotalQuantityNeeded() {return this.totalQuantityNeeded;}

    /**
     * Get the name of this Supply.
     * @return the name of this Supply.
     */
    public String getName () {return this.name;}

    /**
     * Get the amount to request for restock.
     * @return the request amount of this Supply.
     */
    public int getRequestAmount() {return this.requestAmount;}

    /**
     * Get this supply's file name.
     * @return the name of this supply's file.
     */
    public String getFileName(){
        return name;
    }

    /**
     * Attach an observer to this supply.
     * @param observer the observer we want to attach.
     */
    @Override
    public void attachObserver(Observer observer) {
        observers.add(observer);
    }

    /**
     * Remove an observer from this supply.
     * @param observer the observer we want to remove.
     */
    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    /**
     * Update the observers of this supply.
     */
    @Override
    public void update() {
        for(Observer observer : observers){
            observer.update(this);
        }
    }

    /**
     * Compare two supplies base on name.
     * @param o the supply we're comparing.
     * @return int -1 (less than), 0 (equals to), 1 (greater than)
     */
    @Override
    public int compareTo(Supply2 o) {
        return name.compareTo(o.getName());
    }

    /**
     * Return True if the object is the same as this Supply.
     * @param o the other Object we're comparing.
     * @return boolean
     */
    @Override
    public boolean equals(Object o){
        if (o instanceof Supply2 && ((Supply2)o).name.equals(name)){
            return true;
        }
        return false;
    }

    /**
     * String representation of this supply.
     * @return String
     */
    @Override
    public String toString(){
        return "Supply " + name;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeInt(quantityInStock);
        out.writeInt(totalQuantityNeeded);
        out.writeInt(requestAmount);
        out.writeObject(name);
    }

    /**
     * Deserialize object.
     * @param in input
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException{
        quantityInStock = in.readInt();
        totalQuantityNeeded = in.readInt();
        requestAmount = in.readInt();
        name = (String) in.readObject();

        observers = new ArrayList<>();
    }
}

