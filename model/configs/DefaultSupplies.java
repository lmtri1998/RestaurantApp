package model.configs;

import model.data.Supply2;
import model.managers.SupplyManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * The DefaultMenuItems class contains a main method that is able to generate the default supplies from the
 * "DefaultItems/Supply" folder
 */
public class DefaultSupplies {
    ArrayList<Supply2> supplies;
    SupplyManager supplyManager;

    /**
     * Constructs a DefaultSupplies
     */
    public DefaultSupplies() {
        supplies= new ArrayList<>();
        supplyManager = new SupplyManager();
    }

    /**
     * Saves all file generate from txt file to the data base folder
     */
    public void saveAllFile() {
        for(Supply2 supply : supplies) {
            supplyManager.saveSupplyFile(supply);
        }
    }

    /**
     * Loads txt files and convert them into menu item objects
     */
    public void loadAllFile() {
        File[] files = new File("DefaultItems/Supply").listFiles();
        for (File file : files) {
            if (file.isFile()) {
                Supply2 supply = load(file.getPath());
                supplies.add(supply);
            }
        }
    }

    /**
     * Load the supply from a txt file
     * @param reference the path of the txt file.
     */
    private Supply2 load(String reference) {
        String name = "";
        int quantityInStock, quantityNeeded, requestAmount;
        quantityInStock = quantityNeeded = requestAmount = 0;

        try (Scanner scan = new Scanner(new File(reference))) {
            name = scan.nextLine();
            quantityInStock = Integer.parseInt(scan.nextLine());
            quantityNeeded = Integer.parseInt(scan.nextLine());
            requestAmount = Integer.parseInt(scan.nextLine());
        }
        catch (FileNotFoundException e) {
//            e.printStackTrace();
        }
        Supply2 supply = new Supply2(name, quantityNeeded, quantityInStock);
        supply.setRequestAmount(requestAmount);
        return supply;
    }

    public static void main(String[] args) {
        DefaultSupplies s = new DefaultSupplies();
        s.loadAllFile();
        s.saveAllFile();
    }
}
