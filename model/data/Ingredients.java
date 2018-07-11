package model.data;

import java.io.Serializable;
import java.util.HashMap;

/**
 * The Ingredients class.
 *
 * This class handles all the ingredients in items (menu items and order items). It keeps track and modify all
 * of the item's addition and subtraction of ingredients.
 */
public class Ingredients implements Serializable, Cloneable{
    private HashMap<String, Integer> addition;
    private HashMap<String, Integer> subtraction;
    private HashMap<String, Integer> baseIngredient;
    private HashMap<String, Integer> availableAddition;
    private HashMap<String, Integer> availableSubtraction;

    /**
     * Constructs Ingredients with additions and subtractions.
     */
    public Ingredients(){
        addition = new HashMap<>(); //additional ingredients modified when server create an item in order
        subtraction = new HashMap<>(); //subtraction ingredients modified when server create an item in order
        baseIngredient = new HashMap<>(); //set ingredients for the menu/ base item.
        availableAddition = new HashMap<>(); //available ingredients that user can add
        availableSubtraction = new HashMap<>();//available ingredients that user can subtract
    }

    /**
     * Get the additions of the item.
     * @return HashMap with the addition's name and quantity.
     */
    @SuppressWarnings("unchecked")
    public HashMap<String, Integer> getAddition() {
        return (HashMap<String, Integer>)addition.clone();
    }

    /**
     * Get the available additions of the menu item.
     * @return HashMap with the addition's name and quantity.
     */
    @SuppressWarnings("unchecked")
    public HashMap<String, Integer> getSubtraction() {
        return (HashMap<String, Integer>)subtraction.clone();
    }

    /**
     * Get the base ingredient of the menu item.
     * @return HashMap with the base ingredients.
     */
    @SuppressWarnings("unchecked")
    public HashMap<String, Integer> getBaseIngredient() {
        return (HashMap<String, Integer>)baseIngredient.clone();
    }

    /**
     * Get the available additions of the menu item.
     * @return HashMap with available additional ingredients' name and quantity.
     */
    @SuppressWarnings("unchecked")
    public HashMap<String, Integer> getAvailableAddition() {
        return (HashMap<String, Integer>)availableAddition.clone();
    }

    /**
     * Get the available subtractions of the menu item.
     * @return HashMap with available subtraction ingredients' name and quantity.
     */
    @SuppressWarnings("unchecked")
    public HashMap<String, Integer> getAvailableSubtraction() {
        return (HashMap<String, Integer>)availableSubtraction.clone();
    }

    /**
     * Calculates all needed ingredients of this Item and returns the ingredients with quantities as a HashMap.
     * @return needed ingredients
     */
    public HashMap<String, Integer> getNeededIngredients() {
        HashMap<String, Integer> newIngredients = new HashMap<>();
        // merge subtraction HashMap
        for (String name : baseIngredient.keySet()) {
            if (subtraction.containsKey(name)) {
                newIngredients.put(name, baseIngredient.get(name) - subtraction.get(name));
            } else {
                newIngredients.put(name, baseIngredient.get(name));
            }
        }
        // merge addition HashMap
        for (String name : addition.keySet()) {
            if (newIngredients.containsKey(name)) {
                newIngredients.put(name, baseIngredient.get(name) + addition.get(name));
            } else {
                newIngredients.put(name, addition.get(name));
            }
        }
        return newIngredients;
    }


    /**
     * Set the available additions to the menu item.
     * @param availableAddition available ingredients that we can add to item.
     */
    @SuppressWarnings("unchecked")
    public void setAvailableAddition(HashMap<String, Integer> availableAddition){
        this.availableAddition = (HashMap<String, Integer>)availableAddition.clone();
    }

    /**
     * Set the available subtractions to the menu item.
     * @param availableSubtraction available ingredients that we can subtract from item.
     */
    @SuppressWarnings("unchecked")
    public void setAvailableSubtraction(HashMap<String, Integer> availableSubtraction){
        this.availableSubtraction = (HashMap<String, Integer>)availableSubtraction.clone();
    }

    /**
     * Set the addition ingredients to the item being created.
     * @param addition ingredients server choose to add to this item.
     */
    @SuppressWarnings("unchecked")
    public void setAddition(HashMap<String, Integer> addition) {
        this.addition = (HashMap<String, Integer>)addition.clone();
    }

    /**
     * Set the subtraction ingredients to the item being created.
     * @param subtraction ingredients server choose to delete from this item.
     */
    @SuppressWarnings("unchecked")
    public void setSubtraction(HashMap<String, Integer> subtraction) {
        this.subtraction = (HashMap<String, Integer>)subtraction.clone();
    }

    /**
     * Set the base ingredients for the menu item being created.
     * @param baseIngredient set ingredients to make this menu item.
     */
    @SuppressWarnings("unchecked")
    public void setBaseIngredient(HashMap<String, Integer> baseIngredient) {
        this.baseIngredient = (HashMap<String, Integer>)baseIngredient.clone();
    }

    /**
     * Records that a addition has been added to this item.
     * The ingredient name parameter has to be one of the available additions.
     *
     * Do nothing if the addition has been added previously.
     *
     * @param name the addition ingredient
     */
    public void addAddition(String name) {
        int quantity = availableAddition.get(name);
        if(addition.containsKey(name)) {
            addition.put(name, addition.get(name) + quantity);
        }
        else {
            addition.put(name, quantity);
        }
    }

    /**
     * Records that a subtraction has been added to this item.
     * The ingredient name parameter has to be one of the available subtractions.
     *
     * Do nothing if the subtraction has been added previously.
     *
     * @param name the subtraction ingredient
     */
    public void addSubtraction(String name){
        subtraction.put(name, availableSubtraction.get(name));
    }

    /**
     * Cloning the ingredients.
     * @return Ingredients
     */
    @Override
    public Ingredients clone(){
        Ingredients ingredients;
        try {
            ingredients= (Ingredients) super.clone();
            ingredients.setAddition(getAddition());
            ingredients.setSubtraction(getSubtraction());
            ingredients.setAvailableAddition(getAvailableAddition());
            ingredients.setAvailableSubtraction(getAvailableSubtraction());
            ingredients.setBaseIngredient(getBaseIngredient());
        }
        // Not going to happen since we are cloneable
        catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
        return ingredients;
    }

}
