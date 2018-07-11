package control;

import model.data.Item;
import model.data.Meal;
import model.data.ObservableItem;
import model.managers.MenuItemManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is a class that controls the Menu Items.
 */
public class MenuItemController {
    MenuItemManager menuItemManager;
    FileController fileController;
    public MenuItemController(){
        menuItemManager = new MenuItemManager();
        fileController = FileController.getFileController();
    }

    /**
     * Create the new menu-items from the given information.
     *
     * Precondition: the key in subtractions must exists in ingredients.
     * @param name - name of the new item
     * @param price - price of the item.
     * @param description - description of the item
     * @param ingredients - hashmaps of ingredients of items
     * @param additions - the hashmap of addition for the items.
     * @param subtractions - the hashmap of subtractions for the items
     */
    public void createMeal(String name, Double price, String description, HashMap<String, Integer> ingredients,
                           HashMap<String, Integer> additions, HashMap<String, Integer> subtractions) {
        Meal item = new Meal(name, price);
        item.setDescription(description);
        item.getIngredients().setBaseIngredient(ingredients);
        item.getIngredients().setAvailableAddition(additions);
        item.getIngredients().setAvailableSubtraction(subtractions);
        menuItemManager.saveMealItemFile(item);
        fileController.notifyChange(item);
    }

    /**
     * Edit the menu item from the existing menu to the description
     * given.
     *
     * Precondition: item name must already exists in the menu and
     *              subtraction must exists in ingredients.
     * @param name - name of the menu item.
     * @param description - description of the new modified item
     * @param ingredients - the ingredients for new modified item
     * @param additions - the additions for new modified item
     * @param subtractions - the subtraction for new modified item
     */
    public void editMeal(String name, String description, HashMap<String, Integer> ingredients,
                         HashMap<String, Integer> additions, HashMap<String, Integer> subtractions) {
        Meal item = menuItemManager.getMealItem(name);
        item.setDescription(description);
        item.getIngredients().setBaseIngredient(ingredients);
        item.getIngredients().setAvailableAddition(additions);
        item.getIngredients().setAvailableSubtraction(subtractions);
        menuItemManager.saveMealItemFile(item);
        fileController.notifyChange(item);
    }

    // Right now we only have meal

    /**
     * Delete the menu item from the data base. If the
     * menu does not exists, do nothing.
     * @param name - name of the menu Item.
     */
    public void deleteMenuItem(String name) {
        Item item = menuItemManager.getMealItem(name);
        if(item != null){
            menuItemManager.deleteMealItem(item);
            fileController.notifyChange((ObservableItem) item);
        }
    }


}
