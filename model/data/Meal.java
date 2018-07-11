package model.data;

import java.util.HashMap;

/**
 * The Meal class represents the Meal.
 *
 * If the item number is -1, then this Meal represents the base Menu Meal. Other wise it represents a customer item
 * that is being pass in the restaurant.
 */
public class Meal extends ObservableItem {

    /**
     * Constructs a menu Meal with given name and price
     * @param name the name
     * @param price the price
     */
    public Meal(String name, double price){
        super(name, price);
    }

    /**
     * Constructs a Meal with given base Meal from the Menu
     * @param baseMeal the base menu item
     * @param itemNumber the item number
     * @param orderNumber the order number
     * @param tableNumber the table number
     */
    public Meal(Meal baseMeal, int itemNumber, int orderNumber, int tableNumber){
        super(baseMeal);
        itemInfo = new ItemInfo(itemNumber, orderNumber, tableNumber);
    }

    @Override
    public String toString() {
        String s;
        if(itemInfo.getItemNumber() < 0){
            s = "Menu Item:" + name;
        }else{
            s = "Item#" + String.valueOf(itemInfo.getItemNumber()) + ":  "+ name;
        }
        return s;
    }
}
