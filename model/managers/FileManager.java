package model.managers;

import model.configs.Config;
import model.data.Item;
import model.interfaces.Savable;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * The FileManager class.
 *
 * The FileManager is responsible for dealing with saving and reading files as well as checking if a file exists
 * or not during the program. It also contains the serializing and de-serializing code of a Savable object which
 * has stores file name and implements the Serializable interface.
 * @see Savable
 */
public class FileManager {
    private static Logger logger;

    public void writeLockFile(String fileName){
        try(PrintWriter file = new PrintWriter(Config.getLockFilesPath() + fileName)) {
            file.println();
        }catch(Exception e){
            logger.log(Level.SEVERE, "Lock file cannot be created: " + fileName);
        }
    }

    public void deleteLockFile(String fileName){
        File file = new File(Config.getLockFilesPath() + fileName);
        if (!file.delete()){
            logger.log(Level.SEVERE,"Fail to delete lock file: " + fileName);
        }
    }


    public boolean checkLockFile(Savable savable){
        if(new File(Config.getLockFilesPath() + savable.getFileName()).exists()) {
            return true;
        }
        return false;
    }

    /**
     * Checks if a Savable object has file existing in a target directory or not.
     * @param savable the Savable object
     * @param folderName the target folder name
     * @return true if the file exists
     */
    public boolean isExisted(Savable savable, String folderName){
        return new File(folderName + savable.getFileName()).exists();
    }


    /**
     * Serializes the Savable object into target folder.
     * @param object the object that needs to be saved
     * @param folderName the folder directory
     */
    public void saveToFile(Savable object, String folderName){
        String path = folderName + object.getFileName();
        try {
            OutputStream file = new FileOutputStream(path);
            OutputStream buffer = new BufferedOutputStream(file);
            ObjectOutput output = new ObjectOutputStream(buffer);

            // serialize the Object
            output.writeObject(object);
            output.close();
        }
        catch (IOException e){
//            System.err.println(e.getMessage());
//            System.err.println("Cannot save the file at target directory: " + path);
        }
    }

    /**
     * Deletes the Savable object's serialized file from target folder directory.
     * @param savable the Savable object
     * @param folderName the folder directory
     */
    public void deleteFile(Savable savable, String folderName){
        String path = folderName + savable.getFileName();
        File file = new File(path);
        if (!file.delete()){
//            System.err.println("Fail to delete file: " + path);
        }
    }

    /**
     * Writes contents into a file given a file path. Over write the file if the file exists.
     * @param content the String content
     * @param filePath the path
     */
    public void writeFile(String content, String filePath){
        try(PrintWriter file = new PrintWriter(filePath)) {
            file.print(content);
        }catch(Exception e){
//            System.err.println("File not written: " + filePath);
        }
    }

    /**
     * Reads content from a file.
     * @param filePath the path
     * @return the String content that is in this file
     */
    public String readFileContent(String filePath){
        ArrayList<String> content = new ArrayList<>();
        try(Scanner scan = new Scanner(new File(filePath))){
            while (scan.hasNext()){
                content.add(scan.nextLine());
            }
        } catch (FileNotFoundException e){
//            System.err.println("cannot read the file content: " + filePath);
            return null;
        }
        return content.size() == 0 ? "" : String.join("\n", content);
    }

    /**
     * De-serializes the object from a serializing file.
     * @param path the serializing path
     * @return the Savable object
     */
    public Savable readFromFile(String path){
        Object object = null;
        try {
            InputStream file = new FileInputStream(path);
            InputStream buffer = new BufferedInputStream(file);
            ObjectInput input = new ObjectInputStream(buffer);

            //deserialize the Object
            object = input.readObject();
            input.close();
        }
        catch (IOException ex) {
//            System.err.println("Cannot read from file: " + path);
        }
        catch (ClassNotFoundException e){
//            System.err.println("Class File not Found or Corrupted");
        }
        return (Savable)object;
    }

    /**
     * Serializes the Savable in another path and deletes the old serializing file.
     * @param object the Savable object
     * @param oldFolder the old path
     * @param newFolder the new path
     */
    public void changeFilePath(Savable object, String oldFolder, String newFolder){
        File oldFile = new File(oldFolder + object.getFileName());

        if (!oldFile.delete()){
//            System.err.println("Fail to delete old file..");
        }
        saveToFile(object, newFolder);
    }

    /**
     * returns the list of files in a given folder path.
     * @param path the folder path
     * @return the list of files
     */
    public File[] getFileList(String path){
        return new File(path).listFiles();
    }
}
