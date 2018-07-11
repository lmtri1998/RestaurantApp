package model.managers;

import model.configs.Config;
import model.data.Supply2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The SupplyManager class
 *
 * The SupplyManager class provides methods that deals with the supply. It manages the supply amounts and reduces
 * supply in stock when given the ingredients. It also loads and returns supply lists from the data base.
 */
public class SupplyManager {
    private FileManager fileManager;

    /**
     * Constructs a SupplyManager
     */
    public SupplyManager(){
        fileManager = new FileManager();
    }

    /**
     * Saves the supply file to the folder.
     * @param supply the supply
     */
    public void saveSupplyFile(Supply2 supply){
        fileManager.saveToFile(supply, Config.getStockPath());
    }

    /**
     * Returns a supply given the name. Return null if supply not found
     * @param name the name of the supply
     * @return the supply
     */
    public Supply2 getSupplyByName(String name){
        return (Supply2) fileManager.readFromFile(Config.getStockPath() + name);
    }

    /**
     * Returns a list of supply names.
     * @return a list of supply names
     */
    public ArrayList<String> getSupplyNameList(){
        ArrayList<String> names = new ArrayList<>();
        for(File file: fileManager.getFileList(Config.getStockPath())) {
            names.add(file.getName());
        }
        return names;
    }


    /**
     * Returns a list of supplies in the data base
     * @return a list of supplies
     */
    private ArrayList<Supply2> getSupplyList(){
        ArrayList<Supply2> supplies = new ArrayList<>();
        File[] supplyFiles = fileManager.getFileList(Config.getStockPath());
        for(File file: supplyFiles) {
            supplies.add((Supply2) fileManager.readFromFile(Config.getStockPath() + file.getName()));
        }
        return supplies;
    }

    /**
     * Deduct the quantity of the supply that is used to make an Item.
     * @param ingredients that is used to make an Item.
     */
    public void deductIngredientUsage(HashMap<String, Integer> ingredients){
        deductIngredientsFromReservedSupply(ingredients);
        for (String name: ingredients.keySet()) {
            Supply2 supply = getSupplyByName(name);
            supply.removeQuantity(ingredients.get(name));
            saveSupplyFile(supply);
        }
    }
    /**
     * Deduct the quantity of the supply that reserved when the item is finished.
     * @param ingredients that needs to deduct from reserved supplies
     */
    public void deductIngredientsFromReservedSupply(HashMap<String, Integer> ingredients){
        HashMap<String, Integer> reservedSupplies = getReservedSupply();
        for (String name : ingredients.keySet()){
            int quantity = ingredients.get(name);
            int reservedQuantity = (!reservedSupplies.containsKey(name)) ? 0 : reservedSupplies.get(name) - quantity;
            if (reservedQuantity < 0){
//                System.err.println("There is an error in reservedSupply!");
                reservedQuantity = 0;
            }
            reservedSupplies.put(name, reservedQuantity);
        }
        writeReservedSupply(reservedSupplies);
    }
    /**
     * Update the supply request text file
     */
    public void updateSupplyRequests() {
        ArrayList<Supply2> supplies= getSupplyList();
        try(PrintWriter file = new PrintWriter("Requests.txt")) {
            for (Supply2 s : supplies) {
                int need = 0;
                while (s.getQuantity() + need < s.getTotalQuantityNeeded()) {
                    need += s.getRequestAmount();
                }
                if (need != 0) {
                    file.println(s.getName() + ": " + Integer.toString(need));
                }
            }
        }
        catch (FileNotFoundException e) {
//            e.printStackTrace();
        }
    }

    /**
     *
     * @param neededSupplies the supplies of an item that needs to check to see if it is in stock
     * @return boolean
     */
    public boolean checkNeededSupply(HashMap<String, Integer> neededSupplies, boolean reserve) {
        HashMap<String, Integer> reservedSupplies = getReservedSupply();
        boolean hasEnough = true;

        for (String name : neededSupplies.keySet()) {
            Supply2 s = getSupplyByName(name);
            int quantity = neededSupplies.get(name);
            if (reservedSupplies.containsKey(name))
                quantity += reservedSupplies.get(name);
            if (s.getQuantity() < quantity) {
                hasEnough = false;
                break;
            }
        }

        if (hasEnough && reserve){
            writeReservedSupply(mergeHashMaps(neededSupplies, reservedSupplies));
        }
        return hasEnough;
    }
    /**
     * Merge two hash maps with values added.
     * @param a the first hash map
     * @param b the second hash map
     * @return a merged hash maps
     */
    private HashMap<String, Integer> mergeHashMaps(HashMap<String, Integer> a, HashMap<String, Integer> b){
        HashMap<String, Integer> newHash = new HashMap<>(a);
        for(String key : b.keySet()){
            int value = b.get(key);
            if (newHash.containsKey(key)) {
                value += newHash.get(key);
            }
            newHash.put(key, value);
        }
        return newHash;
    }
    /**
     *
     * @return Hash map that contains details of the reserved supplies
     */
    private HashMap<String, Integer> getReservedSupply(){
        String content = fileManager.readFileContent(Config.getReservedSupplyFile());
        HashMap<String, Integer> reservedSupplies = new HashMap<>();

        for(String line : content.split("\n")){
            if(!line.trim().isEmpty()) {
                String[] supplyLine = line.split(",");
                reservedSupplies.put(supplyLine[0], Integer.parseInt(supplyLine[1]));
            }
        }
        return reservedSupplies;
    }
    /**
     * Write a file that keeps track of reserved supplies in directory "Extras"
     * @param supplies that is needed to reserve
     */
    private void writeReservedSupply(HashMap<String, Integer> supplies){
        ArrayList<String> content = new ArrayList<>();
        for (String supply: supplies.keySet()){
            int quantity = supplies.get(supply);
            // if the quantity is less than zero, then we don't need to keep track
            if (quantity > 0){
                content.add(supply + "," + quantity );
            }
        }
        fileManager.writeFile(String.join("\n", content), Config.getReservedSupplyFile());
    }

}
