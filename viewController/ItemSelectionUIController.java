package viewController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import model.data.Item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * This is the controller for the pop-up that was created in the ItemCustomizer
 * that control the scene ItemSelectionUI.fxml
 */
public class ItemSelectionUIController {
    @FXML
    private Label itemNameLabel;
    @FXML
    private Label itemDescriptionLabel;
    @FXML
    private TextField additionalRequestText;
    @FXML
    private FlowPane ingredientsPane;
    @FXML
    private Label priceLabel;

    private boolean needSave;

    private ArrayList<IngredientBox> ingredientBoxes ;

    @FXML
    public void initialize(){
        ingredientBoxes = new ArrayList<>();
        needSave = false;
    }

    /**
     * when the done button is clicked, it will exit the pop-up
     * and change the save status to true.
     */
    public void doneBtn_Clicked() {
        needSave = true;
        exit();
    }

    /**
     * the boolean of weather the stuff user input
     * is needed to be save.
     * @return weather the information is needed to be saved.
     */
    public boolean needSaving(){
        return needSave;
    }

    /**
     * Exit the pop-up
     */
    public void exit(){
        Stage stage = (Stage) itemDescriptionLabel.getScene().getWindow();
        stage.close();
    }

    public HashMap<String, Integer> getIngredients(){
        HashMap<String, Integer> lst = new HashMap<>();
        for(IngredientBox box: ingredientBoxes){
            lst.put(box.getName(), box.getStatus());
        }
        return lst;
    }
    public String getAdditionalRequest(){
        return additionalRequestText.getText();
    }

    /**
     * Update the view from the given item. The view will
     * display the item's name, price, description, and
     * choices for user to have additions and subtraction.
     * @param item - the item that is needed to be display.
     */
    public void update(Item item) {
        itemNameLabel.setText(item.getName());
        itemDescriptionLabel.setText(item.getDescription());
        priceLabel.setText("$" + String.format("%.2f", item.getPrice()));
        String additionalRequest = item.getItemInfo().getAdditionalRequest();
        if(!additionalRequest.isEmpty()){
            additionalRequestText.setText(additionalRequest);
        }
        Set<String> additionsLst = item.getIngredients().getAvailableAddition().keySet();
        Set<String> subtractLst = item.getIngredients().getAvailableSubtraction().keySet();
        ArrayList<String> lst = new ArrayList<>();
        for(String ingredientName : additionsLst){
            lst.add(ingredientName);
            boolean hasSubtraction = subtractLst.contains(ingredientName);
            try{
                IngredientBox box = new IngredientBox(true, hasSubtraction, ingredientName);
                ingredientBoxes.add(box);
                ingredientsPane.getChildren().add(box.getScene());
            }catch (IOException e){}
        }
        for(String ingredientName : subtractLst){
            if (!lst.contains(ingredientName)){
                try {
                    IngredientBox box = new IngredientBox(false, true, ingredientName);
                    ingredientBoxes.add(box);
                    ingredientsPane.getChildren().add(box.getScene());
                }catch (IOException e){}
            }
        }
    }
}

