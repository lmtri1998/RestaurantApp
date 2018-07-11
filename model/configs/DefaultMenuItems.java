package model.configs;

import model.data.Meal;
import model.managers.FileManager;
import model.managers.MenuItemManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * The DefaultMenuItems class contains a main method that is able to generate the default menu items from the
 * "DefaultItems/Food" folder
 */
public class DefaultMenuItems {

    private ArrayList<Meal> meals;
    private MenuItemManager menuItemManager;

    /**
     * Constructs a DefaultMenuItems
     */
    public DefaultMenuItems() {
        meals = new ArrayList<>();
        menuItemManager = new MenuItemManager();
    }

    /**
     * Saves all file generate from txt file to the data base folder
     */
    public void saveAllFile() {
        for(Meal meal: meals)
            menuItemManager.saveMealItemFile(meal);
    }

    /**
     * Loads txt files and convert them into menu item objects
     */
    public void loadAllFile() {
        File[] files = new File("DefaultItems/Food").listFiles();
        for (File file : files) {
            if (file.isFile()) {
                Meal meal = loadBasic(file.getPath());
                meals.add(meal);
            }
        }
    }
    /**
     * Load the MenuItem from a txt file
     * @param reference the path of the txt file.
     */
    public Meal loadBasic(String reference) {
        HashMap<String, Integer> ingredients = new HashMap<>();
        HashMap<String, Integer> additions = new HashMap<>();
        HashMap<String, Integer> subtractions = new HashMap<>();
        String name = "";
        Float price = 0.0f;
        String description = "";
        try(Scanner scan = new Scanner(new File(reference))) {
            name = scan.nextLine();
            price = Float.parseFloat(scan.nextLine());
            String ingredientRef = scan.nextLine();
            while(!(ingredientRef.equals("####"))) {
                String[] parts = ingredientRef.split(":");
                ingredients.put(parts[0], Integer.parseInt(parts[1]));
                ingredientRef = scan.nextLine();
            }
            String ingredientAdditionsRef = scan.nextLine();
            while(!(ingredientAdditionsRef.equals("####"))) {
                String[] parts = ingredientAdditionsRef.split(":");
                additions.put(parts[0], Integer.parseInt(parts[1]));
                ingredientAdditionsRef = scan.nextLine();
            }
            String ingredientSubtractionRef = scan.nextLine();
            while(!(ingredientSubtractionRef.equals("####"))) {
                String[] parts = ingredientSubtractionRef.split(":");
                subtractions.put(parts[0], Integer.parseInt(parts[1]));
                ingredientSubtractionRef = scan.nextLine();
            }
            while(scan.hasNextLine()) {
                description += scan.nextLine() + "\n";
            }
        } catch(FileNotFoundException e) {
//            e.printStackTrace();
        }
        Meal newMeal = new Meal(name, price);
        newMeal.setDescription(description);
        newMeal.getIngredients().setBaseIngredient(ingredients);
        newMeal.getIngredients().setAvailableAddition(additions);
        newMeal.getIngredients().setAvailableSubtraction(subtractions);
        return newMeal;
    }

    public static void main(String[] args) {
        DefaultMenuItems d = new DefaultMenuItems();
        d.loadAllFile();
        d.saveAllFile();
    }
}
