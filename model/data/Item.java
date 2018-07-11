package model.data;

import model.interfaces.Savable;

/**
 * The Item class.
 *
 * This abstract class represents an Item that is a menu item made by the manager,
 * and an order item once an order is created.
 *
 * When this Item acts as a menu item, it stores all the base information such as name, price, description, base
 * ingredients and itemInfo. An order item uses the information from menu item (base item) to create itself once
 * an order is made, and is managed throughout the program by ItemManager.
 *
 * An Item can be a Meal or Beverage (open for other classes in the future).
 *
 * This Item can also set additional requests and descriptions.
 *
 */
abstract public class Item implements Savable, Comparable<Item> {
    protected String name;
    protected String description;
    protected double price;
    protected Ingredients ingredients;
    protected ItemInfo itemInfo;

    /**
     * Constructs an Item (base/ menu item) with name and price.
     * @param name the name of the Item
     * @param price the price of the Item
     */
    public Item(String name, double price){
        this.name = name;
        this.price = price;
        this.description = "";
        this.ingredients = new Ingredients();
        this.itemInfo = new ItemInfo(-1, -1, -1);

    }

    /**
     * Constructs an Item (order item) base on the baseItem (created menu item)
     * @param baseItem menu item that this Item is basing off.
     */
    public Item(Item baseItem){
        name = baseItem.getName();
        price = baseItem.getPrice();
        description = baseItem.getDescription();
        ingredients = baseItem.getIngredients().clone();
    }

    /**
     * Returns name of the item.
     * @return the name of the item
     */
    public String getName(){
        return name;
    }

    /**
     * Returns the description of this Item.
     * @return the description of this item
     */
    public String getDescription(){
        return description;
    }

    /**
     * Returns the price of this Item.
     * @return the price of this item
     */
    public double getPrice() {
        return price;
    }

    /**
     * Returns the ItemInfo of this Item.
     * @return the ItemInfo of this item
     */
    public ItemInfo getItemInfo() {
        return itemInfo;
    }

    /**
     * Returns the ingredients of this Item.
     * @return the ingredients of this item
     */
    public Ingredients getIngredients(){
        return ingredients;
    }

    /**
     * Returns String representation of this Item's file name.
     * @return the file's name of this Item
     */
    @Override
    public String getFileName() {
        int itemNum = itemInfo.getItemNumber();
        return itemNum == -1 ? name : Integer.toString(itemInfo.getItemNumber());
    }

    /** Set the description.*/
    public void setDescription(String description){
        this.description = description;
    }

    /** Compare two items base on name.
     * @param o the item we're comparing.
     * @return int -1 (less than), 0 (equals to), 1 (greater than) */
    @Override
    public int compareTo(Item o) {
        return name.compareTo(o.name);
    }

    /** Return True if the object is the same as this Item.
     * @param o the other Object we're comparing.
     * @return boolean */
    @Override
    public boolean equals(Object o){
        if (o instanceof Item){
            // MenuItem situation
            if(itemInfo.getItemNumber() == -1){
                return ((Item)o).getName().equals(name);
            }
            // Custom Item situation
            return ((Item)o).itemInfo.getItemNumber() == itemInfo.getItemNumber();
        }
        return false;
    }
}
