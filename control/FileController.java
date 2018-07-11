package control;

import model.data.Meal;
import model.interfaces.Observable;
import model.interfaces.Savable;
import model.managers.FileManager;

import java.io.File;

/**
 * FileController class. It attaches Observable to all of the observers and send the update request to all
 * of the programs that is currently running.
 *
 * Upon receiving changes from the other program, it will uses the updateManager to attach observables to the
 * observers and call update and notify changes to all observers.
 */
public class FileController {

    private static String updatePath = "UpdateFiles/";
    private static FileController fileController = new FileController();
    private long id;
    private String folderPath;
    private FileManager fileManager;

    private UpdateManager updateManager;

    /**
     * Constructs a FileController.
     */
    private FileController(){
        folderPath = ControlConfig.getUpdateFilesPath();
        fileManager = new FileManager();
        id = System.currentTimeMillis();
        folderPath += Long.toString(id) + "/";
        createSaveFolder();

    }

    /**
     * Send the notifying files to all of other sub folders in the updateFiles folder. Call observable.update()
     * upon receiving a new observable.
     * @param observable the observable
     */
    public void notifyChange(Observable observable){
        updateManager.hookObserver(observable);
        for(File folder : fileManager.getFileList(updatePath)){
            if(!folder.getName().equals(Long.toString(id))){
                fileManager.saveToFile((Savable) observable, updatePath + folder.getName() + "/");
            }
        }
        observable.update();
    }

    /**
     * Attaches the updateManager to this FileController.
     * @param updateController
     */
    public void setUpdateController(UpdateController updateController){
        this.updateManager = updateController;
    }

    /**
     * Loads update files from its update folder, hook observers to the observables using the
     * updateManager and notify all current observers.
     */
    public void updateChange(){
        for(File file : fileManager.getFileList(folderPath)){
           Savable savable = fileManager.readFromFile(folderPath + file.getName());
           updateManager.hookObserver(((Observable) savable));
            ((Observable) savable).update();
            file.delete();
        }
    }

    /**
     * Unregister this fileController. This will delete the associated folder from the updateFiles and stop updating.
     */
    public void unregister(){
        File dir = new File(folderPath);
        dir.delete();
    }

    /**
     * Helper method to create the folder to receive update from other program
     */
    private void createSaveFolder(){
        File dir = new File(folderPath);
        if(!dir.mkdir()){
        }
    }

    /**
     * Returns this singleton file controller instance
     * @return
     */
    public static FileController getFileController(){
        return fileController;
    }

}
