package control;

import java.io.File;

/**
 * The config class for the controller.
 */
public class ControlConfig {
    private static String updateFilesPath = "UpdateFiles/";

    /**
     * Initializes the folder if it doesn't exist.
     */
    public static void init(){
        File updatePath = new File(updateFilesPath);
        if(!updatePath.exists()){
            if(!updatePath.mkdir()){
            }
        }
    }

    /**
     * Returns the update files directory
     * @return the update files directory
     */
    public static String getUpdateFilesPath(){
        return updateFilesPath;
    }
}
