package viewController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

/**
 * IngredientBox class. Represent an ingredient as a box with plus and minus buttons for increasing and decreasing
 * the quantity. Allow user to customize the quantity of this ingredient for the item.
 */
public class IngredientBox {

    private Parent scene;
    private String name;
    private final String SCENE_FILE = "/uiFxml/IngredientChoicePanel.fxml";
    IngredientChoicePanelController controller;

    /**
     * A constructor for IngredientBox. Create the scene to contain this box.
     * @param hasAddition true if item has additions, false otherwise.
     * @param hasSubtraction true if item has subtractions, false otherwise.
     * @param text name of the ingredient.
     * @throws IOException
     */
    public IngredientBox(boolean hasAddition, boolean hasSubtraction, String text) throws IOException{
            FXMLLoader myLoader = new FXMLLoader(getClass().getResource(SCENE_FILE));
            scene = myLoader.load();
            controller = myLoader.getController();
            controller.update(hasAddition, hasSubtraction, text);
            this.name = text;
    }

    /**
     *
     * @return the root's scene.
     */
    public Parent getScene() {
        return scene;
    }

    /**
     *
     * @return the name of the ingredient.
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return the status of this ingredient. -1 is subtraction, 0 is regular, 1 is extra, 2 is extra x2, 3 is extra x3.
     */
    public int getStatus() {
        return controller.getStatus();
    }
}
