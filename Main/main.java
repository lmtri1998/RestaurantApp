package Main;

import control.ControlConfig;
import control.FileController;
import control.UpdateController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.configs.Config;
import model.interfaces.Observer;
import viewController.SceneController;

import java.util.Timer;
import java.util.TimerTask;

/**
 * The Main program for this Restaurant program. This program is an
 * ordering program where it allows multiple screen to be opened at once.
 *
 */
public class main extends Application{
    private final int HEIGHT = 720;
    private final int WIDTH = 1280;
    public static final String MainMenuDisplay = "MainDisplay";
    public static final String ServerDisplayID = "ServerDisplay";
    public static final String MakeOrderSceneID = "MakeAnOrderDisplay";
    public static final String ManagerDisplayID = "ManagerDisplay";
    public static final String ManageMenuItemDisplayID = "ManageMenuItemDisplay";
    public static final String AddMenuItemDisplayID = "AddMenuItemDisplay";
    public static final String EditMenuItemDisplayID = "EditMenuItemDisplay";
    public static final String ChooseOrderDisplayID = "ChooseOrderDisplay";
    public static final String KitchenDisplayID = "KitchenDisplay";
    public static final String ReceiveSupplyDisplayID = "ReceiveSupplyDisplay";
    public static final String ItemDeliveryDisplayID = "ItemDeliveryDisplay";
    public static final String EditOrderDisplayID = "EditOrderDisplay";
    public static final String PaymentDisplayID = "PaymentDisplay";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage){

        Config.init();
        ControlConfig.init();

        // add the scene to the scene controller so it can be switch.
        SceneController s = new SceneController();
        s.loadScenes(ServerDisplayID, "/uiFxml/ServerDisplay.fxml");
        s.loadScenes(MakeOrderSceneID, "/uiFxml/MakeAnOrder.fxml");
        s.loadScenes(ManagerDisplayID, "/uiFxml/ManagerDisplay.fxml");
        s.loadScenes(ManageMenuItemDisplayID, "/uiFxml/Menu.fxml");
        s.loadScenes(AddMenuItemDisplayID, "/uiFxml/AddNewMenuItem.fxml");
        s.loadScenes(EditMenuItemDisplayID, "/uiFxml/ManageMenuItem.fxml");
        s.loadScenes(MainMenuDisplay, "/uiFxml/MainDisplay.fxml");
        s.loadScenes(ChooseOrderDisplayID, "/uiFxml/ManageOrder1.fxml");
        s.loadScenes(KitchenDisplayID, "/uiFxml/KitchenDisplay.fxml");
        s.loadScenes(ReceiveSupplyDisplayID, "/uiFxml/ReceiveSupply.fxml");
        s.loadScenes(ItemDeliveryDisplayID, "/uiFxml/DeliverItem.fxml");
        s.loadScenes(EditOrderDisplayID, "/uiFxml/ManageOrder2.fxml");
        s.loadScenes(PaymentDisplayID, "/uiFxml/PaymentDisplay.fxml");
        s.switchScene(MainMenuDisplay);


        // add the controllers to the correspond observer so they
        // can be notified when something was changed.
        UpdateController updateController = new UpdateController();

        updateController.addOrderObserver((Observer) s.getController(ChooseOrderDisplayID));
        updateController.addOrderObserver((Observer) s.getController(EditOrderDisplayID));
        updateController.addOrderObserver((Observer) s.getController(KitchenDisplayID));

        updateController.addItemObserver((Observer) s.getController(EditOrderDisplayID));
        updateController.addItemObserver((Observer) s.getController(ItemDeliveryDisplayID));
        updateController.addItemObserver((Observer) s.getController(ServerDisplayID));

        updateController.addMenuItemObserver((Observer) s.getController(ManageMenuItemDisplayID));
        updateController.addMenuItemObserver((Observer) s.getController(MakeOrderSceneID));
        updateController.addMenuItemObserver((Observer) s.getController(EditOrderDisplayID));

        updateController.addSupplyObserver((Observer) s.getController(ReceiveSupplyDisplayID));
        updateController.addSupplyObserver((Observer) s.getController(AddMenuItemDisplayID));
        updateController.addSupplyObserver((Observer) s.getController(ManageMenuItemDisplayID));

        primaryStage.initStyle(StageStyle.UTILITY);
        primaryStage.setHeight(HEIGHT);
        primaryStage.setWidth(WIDTH);

        primaryStage.setTitle("Restaurant");
        primaryStage.setScene(new Scene(s));
        primaryStage.setResizable(false);


        FileController fileController = FileController.getFileController();
        fileController.setUpdateController(updateController);

        Timer timer = new java.util.Timer();

        // https://stackoverflow.com/questions/16764549/timers-and-javafx/18654916#18654916
        //The timer schedule will call updateChange every 2 seconds.
        timer.schedule(new TimerTask() {
            public void run() {
                Platform.runLater(() -> fileController.updateChange());
            }
        }, 1000, 1000);
        primaryStage.setOnCloseRequest(Event::consume);


        primaryStage.show();

        //Make sure everything is processed properly when the program finished.
        primaryStage.setOnHidden(e -> {
            timer.cancel();
            fileController.unregister();
        });

    }
}
