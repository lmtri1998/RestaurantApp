package viewController;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;

/**
 * This is the controller that can switch scenes without having
 * pop-up or having a new stage. This controller can control
 * the scene that has been implemented controlledScene. The scene
 * are switch similar to stack, so there is option to add
 * a scene. The idea for this controller was from Oracle Website
 */
public class SceneController extends StackPane{
    HashMap<String, Node>scenes = new HashMap<>();
    Stack<String> sceneStack = new Stack<>();
    HashMap<String, Object> controllers = new HashMap<>();

    /**
     * Switch the scene to the scene that is correspond to the
     * given sceneID in the hashmap. The scene will be added to
     * the stack. Do nothing if the ID is not recorded.
     * @param sceneID - the scene that you want to switch to.
     */
    public void switchScene(String sceneID){
        if (scenes.get(sceneID) != null) {   //screen loaded
            final DoubleProperty opacity = opacityProperty();
            sceneStack.push(sceneID);
            if (!getChildren().isEmpty()) {    //if there is more than one screen
                Timeline fade = new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(opacity, 1.0)),
                        new KeyFrame(new Duration(250), e ->{
                                getChildren().remove(0);                    //remove the displayed screen
                                getChildren().add(0, scenes.get(sceneID));     //add the screen
                                Timeline fadeIn = new Timeline(
                                        new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                                        new KeyFrame(new Duration(250), new KeyValue(opacity, 1.0)));
                                fadeIn.play();
                        }, new KeyValue(opacity, 0.0)));
                fade.play();

            } else {
                setOpacity(0.0);
                getChildren().add(scenes.get(sceneID));       //no one else been displayed, then just show
                Timeline fadeIn = new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                        new KeyFrame(new Duration(250), new KeyValue(opacity, 1.0)));
                fadeIn.play();
            }
        } else {
//            System.out.println("screen hasn't been loaded!!! \n" + sceneID);
//            this.getChildren().setAll(scenes.get(sceneID));
        }
    }

    /**
     * Switch to the previous scene on the stack and remove the top scene
     * If the stack only have size one, do nothing.
     */
    public void switchToPrevScene(){
        if(sceneStack.size() > 1){
            sceneStack.pop();

            switchScene(sceneStack.peek());
            //When switch scene, we add the scene to the stack. So we need to pop it again
            sceneStack.pop();
        }
    }

    /**
     * Add/register the scene to the hashmap
     * @param sceneID - the scene Id for the scene
     * @param scene - the scene that you want to load
     */
    public void addScene(String sceneID, Node scene){
        scenes.put(sceneID, scene);
    }

    /**
     * Add/register the scene to the controller so that it can
     * be latter on to be switched to. If the scene cannot be
     * loaded, do nothing
     * @param sceneID - scene id for the scene
     * @param sceneFile - the fxml file for the scene.
     */
    public void loadScenes(String sceneID, String sceneFile){
        try {
            FXMLLoader myLoader = new FXMLLoader(getClass().getResource(sceneFile));
            Parent loadScreen =  myLoader.load();
            ControlledScene myScreenController = myLoader.getController();
            myScreenController.setParent(this);
            addScene(sceneID, loadScreen);
            controllers.put(sceneID, myLoader.getController());
        }catch(IOException e){
//            System.out.println(sceneFile);
        }
    }

    /**
     * Get the scene Controller for a given sceneID.
     * @param sceneID
     * @return controller that is correspond to the scnenID
     */
    public Object getController(String sceneID){
        return controllers.getOrDefault(sceneID, null);
    }
}
