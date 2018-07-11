package model.managers;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * The logger class.
 *
 * The logger class is responsible to print log to a file for the program.
 */
public class MyLogger {
    private static Logger logger;
    private static FileHandler fileHandler;
    private static Formatter formatter;

    /**
     * Creates a logger that prints log to a given file location
     * @param filePath the given file location
     */
    public  MyLogger(String filePath){
        if (logger == null){
            try{
                fileHandler = new FileHandler(filePath);

            }catch (IOException e){

            }
            formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            logger = Logger.getLogger("Restaurant");
            logger.addHandler(fileHandler);
        }
    }

    /**
     * Returns the logger
     * @return the logger
     */
    public Logger getLogger(){
        return logger;
    }
}
