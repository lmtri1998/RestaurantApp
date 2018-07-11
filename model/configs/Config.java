package model.configs;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * The model.configs.Config class.
 *
 * This class is going to initiate all directories and static constants for the directories.
 */
public class Config {
    // This is used in oldClasses.Item.load() so that it will work for every subclasses of MenuItems
    // The load method is static for all MenuItems and its subclasses
    private static final int defaultOrderNumber = 1000;
    private static final int defaultItemNumber = 1000;

    private static String itemNumberFile = "currentItemNumber";
    private static String orderNumberFile = "currentOrderNumber";
    private static String reservedSupply = "reservedSupply";
    private static String loggerFile = "log";

    private static String finishedOrders = "FinishedOrders/";
    private static String finishedItems = "FinishedItems/";
    private static String menuItems = "MenuItems/";
    private static String beverage = "MenuItems/Beverage/";
    private static String food = "MenuItems/Food/";
    private static String items = "Items/";
    private static String orders = "Orders/";
    private static String stock = "Stock/";
    private static String extras = "Extras/";
    private static String bills = "Bills/";
    private static String lockFile = "LockFiles/";
    private static String initOrderNumber = Integer.toString(defaultOrderNumber);
    private static String initItemNumber = Integer.toString(defaultItemNumber);

    private static String[] properties = {"served order directory",
            "served item directory",
            "menu item directory",
            "beverage directory",
            "food directory" ,
            "items directory",
            "orders directory",
            "stock directory",
            "extras directory",
            "bills directory",
            "lock file directory",
            "initial order number", "initial item number"};
    // This list saves all the default values for directories.
    // It will be modified when loading the config file
    private static String[] configValues = {finishedOrders, finishedItems, menuItems, beverage, food, items, orders, stock, extras, bills, lockFile, initOrderNumber, initItemNumber};

    /**
     * Initiates all directories and string instances for the directory and file directory.
     *
     * It will try to read the config file and load the properties there. If the file doesn't exit or the format of the
     * config file is wrong, create a default config file instead.
     */
    public static void init(){
        File config = new File("config");
        if(config.exists()){
            readConfig();
        }
        else{
            makeDefaultConfig();
        }
        makeDirectory();

    }

    /**
     * Reads the config file and assign all values to the static instances. If the format of the file is wrong,
     * create a default config file and assign static instances to default values.
     */
    private static void readConfig(){
        HashMap<String, String> values = new HashMap<>();
        try(Scanner fileOutput = new Scanner(new File("config"))){
            for(int i = 0; i < properties.length ; i++){
                String[] split = fileOutput.nextLine().split("=");
                if(split[0].trim().equals(properties[i])){
                    values.put(properties[i], split[1].trim());
                }
                else {
                    throw new WrongConfigFormatException("Wrong config name in line " + i);
                }
            }
            setNewConfigValue(values);
            String regex = "\\d+";
            if(!initOrderNumber.matches(regex)) {
//                System.err.println("Not an integer for initial order number.");
//                System.err.println("Set initial order number to " + defaultOrderNumber);
                initOrderNumber = Integer.toString(defaultOrderNumber);
            }
            if(!initItemNumber.matches(regex)) {
//                System.err.println("Not an integer for initial item number.");
//                System.err.println("Set initial item number to " + defaultItemNumber);
                initOrderNumber = Integer.toString(defaultItemNumber);
            }
        }
        catch (WrongConfigFormatException e){
//            System.err.println("Spawn default config file instead");
            makeDefaultConfig();
        }
        catch (IOException e){
//            System.err.println("File removed during the process...");
        }
        catch (NoSuchElementException e){
//            System.err.println("Missing properties, reset all to default properties......");
            makeDefaultConfig();
        }
    }

    /**
     * Creates default config file using the default config value.
     */
    private static void makeDefaultConfig(){
        try(PrintWriter fileInput = new PrintWriter("config")){
            for(int i = 0 ; i < properties.length ; i++){
                fileInput.println(properties[i] + " = " + configValues[i]);
            }
        }
        // exit the program for unknown error
        catch (IOException e){
//            System.err.println("Unknown Error when making default file....");
            System.exit(1);
        }
    }

    /**
     * Creates the directory using the static instance. If the directory or files already exits, do not changed or
     * create them.
     */
    private static void makeDirectory(){
        // goes from 0 to 9
        for(int i = 0 ; i < properties.length - 1 ; i++) {
            File directory = new File(configValues[i]);
            if ( directory.exists()) {
//                System.out.println(properties[i] + " exits, use the directory");
            } else {
                if (!directory.mkdir()) {
//                    System.out.println("Error, " + properties[i] + " cannot be created..");
                }
            }
        }
        // order number file
        if(!new File(getOrderNumberFile()).exists()) {
            createTextFile(getOrderNumberFile(), initOrderNumber);
        }
        // item number file
        if(!new File(getItemNumberFile()).exists()) {
            createTextFile(getItemNumberFile(), initItemNumber);
        }
        // reserved supply file
        if(!new File(getReservedSupplyFile()).exists()) {
            createTextFile(getReservedSupplyFile(), "");
        }
//        // log file
//        if(!new File(getLoggerFile()).exists()) {
//            createTextFile(getLoggerFile(), "");
//        }
    }

    /**
     * Assigns all static instance values using the values store in the HashMap. The HasMap should include all the
     * instances as keys and has their corresponding values.
     * @param values values as a HashMap
     */
    private static void setNewConfigValue(HashMap<String, String> values){
        // Hard code alert!!!!
        finishedOrders = values.get(properties[0]);
        configValues[0] = values.get(properties[0]);
        finishedItems = values.get(properties[1]);
        configValues[1] = values.get(properties[1]);
        menuItems = values.get(properties[2]);
        configValues[2] = values.get(properties[2]);
        beverage = values.get(properties[3]);
        configValues[3] = values.get(properties[3]);
        food = values.get(properties[4]);
        configValues[4] = values.get(properties[4]);
        items = values.get(properties[5]);
        configValues[5] = values.get(properties[5]);
        orders = values.get(properties[6]);
        configValues[6] = values.get(properties[6]);
        stock = values.get(properties[7]);
        configValues[7] = values.get(properties[7]);
        extras = values.get(properties[8]);
        configValues[8] = values.get(properties[8]);
        bills = values.get(properties[9]);
        configValues[9] = values.get(properties[9]);
        lockFile = values.get(properties[10]);
        configValues[10] = values.get(properties[10]);
        initOrderNumber = values.get(properties[11]);
        configValues[11] = values.get(properties[11]);
        initItemNumber = values.get(properties[12]);
        configValues[12] = values.get(properties[12]);
    }

    private static void createTextFile(String fileName, String content){
        try (PrintWriter fileInput = new PrintWriter(fileName)) {
            fileInput.println(content);
        } catch (IOException e) {
//            System.err.println("Error, cannot create " + fileName + " file");
        }
    }

    /**
     * Returns the Items path.
     * @return the Items path.
     */
    public static String getItemsPath(){
        return items;
    }

    /**
     * Returns the FinishedOrders path.
     * @return the FinishedOrders path.
     */
    public static String getFinishedOrdersPath(){
        return finishedOrders;
    }

    /**
     * Returns the FinishedItems path.
     * @return the FinishedItems path.
     */
    public static String getFinishedItemsPath(){
        return finishedItems;
    }

    /**
     * Returns the MenuItems path.
     * @return the MenuItems path.
     */
    public static String getMenuItemsPath(){
        return menuItems;
    }

    /**
     * Returns the Food path.
     * @return the Food path.
     */
    public static String getFoodPath(){
        return food;
    }

    public static String getBeveragePath(){
        return beverage;
    }

    /**
     * Returns the bill path.
     * @return the bill path.
     */
    public static String getBillsPath(){return bills;}

    /**
     * Returns the Orders path.
     * @return the Orders path.
     */
    public static String getOrdersPath(){
        return orders;
    }

    /**
     * Returns the Stocks path.
     * @return the Stocks path.
     */
    public static String getStockPath(){
        return stock;
    }

    /**
     * Returns the Extras path.
     * @return the Extras path.
     */
    public static String getExtrasPath(){
        return extras;
    }

    public static String getLockFilesPath(){
        return lockFile;
    }

    /**
     * Returns the reserved supply file directory.
     * @return the reserved supply file directory.
     */
    public static String getReservedSupplyFile(){
        return extras + reservedSupply;
    }

    /**
     * Returns the order number file directory.
     * @return the order number file directory
     */
    public static String getOrderNumberFile(){
        return extras + orderNumberFile;
    }

    /**
     * Returns the item number file directory.
     * @return the item number file directory
     */
    public static String getItemNumberFile(){
        return extras + itemNumberFile;
    }

    /**
     * Returns the logger file directory
     * @return the logger file directory
     */
    public static String getLoggerFile(){
        return extras + loggerFile;
    }

    /**
     * The WrongConfigFormatException class.
     *
     * Thrown when the format of the model.configs.Config file is wrong.
     */
    public static class WrongConfigFormatException extends Exception{
        /**
         * Constructs a WrongConfigFormatException with Error message.
         * @param msg the error message
         */
        public WrongConfigFormatException(String msg){
            super(msg);
        }
    }
}