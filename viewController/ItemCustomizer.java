package viewController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Item;
import model.interfaces.Observer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This will create the pop up that will allow the user to customize the
 * dish they orded. This will allow them to have additions or subtractions
 * to there dish and let them to have a special request on the dish.
 * This class will stores the information the userinput and the info
 * can later on be readed.
 */
public class ItemCustomizer {
    private final String SCENE_FILE = "/uiFxml/ItemSelectionUI.fxml";
    private ArrayList<Observer> observers;
    private String description;
    private HashMap<String, Integer> ingredients;
    private String itemName;
    public ItemCustomizer(){
        observers = new ArrayList<>();
        description = new String();
        ingredients = new HashMap<>();
    }

    /**
     * This display the pop up and create all the ingredientBox
     * that allow the user to choose their preference on ingredients.
     * The return value represent weather the user clicked cancel
     * or done.
     * @param item - the item that needed to be display for the pop-up
     * @return boolean of  weather user clicked done or cancel.
     */
    public boolean display(Item item){
        try {
            FXMLLoader myLoader = new FXMLLoader(ItemCustomizer.class.getResource(SCENE_FILE));
            Parent loadScreen = myLoader.load();
            ItemSelectionUIController controller = myLoader.getController();
            controller.update(item);
            Scene scene = new Scene(loadScreen);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Customize Item");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            description = controller.getAdditionalRequest();
            ingredients = controller.getIngredients();
            itemName = item.getName();
            return controller.needSaving();
        }catch(IOException e){
        }
        return false;
    }

    /**
     * Get the additional request the user input
     * @return the user input for additional request.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Return how the user customize their ingredients.
     * When the ingredient value = -1, the ingredient is needed to be subtracted
     * When the ingredient value =0, there nothing need to be changed.
     * When the ingredient value = 1, extra ingredient on the item
     * When the ingredient value = 2, 2x extra ingredient on the item.
     * When the ingredient value = 3, 3x extra ingredient on the item
     * @return the hashmap of user's choice.
     */
    public HashMap<String, Integer> getIngredients() {
        return ingredients;
    }

    /**
     * Get the item name that was displayed on the pop-up
     * @return
     */
    public String getItemName(){
        return itemName;
    }

}
