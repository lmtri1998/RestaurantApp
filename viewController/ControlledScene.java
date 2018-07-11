package viewController;

/**
 * ControlledScene class. This is an abstract class that allow a scene to be controlled by a sceneController.
 */
public abstract class ControlledScene {
    protected SceneController sceneController;
    public void setParent(SceneController controller){
        this.sceneController = controller;
    }

}
