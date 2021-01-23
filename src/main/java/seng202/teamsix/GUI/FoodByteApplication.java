package seng202.teamsix.GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import seng202.teamsix.managers.OrderManager;

import java.io.IOException;

public class FoodByteApplication extends Application {
    private Stage primaryStage;
    private Scene orderScene;
    private Scene managementScene;
    private OrderScreenController orderController = new OrderScreenController();
    public StockScreenController stockController = new StockScreenController();
    private OrderManager orderManager = new OrderManager();

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);




        FXMLLoader loaderOrder = new FXMLLoader(getClass().getResource("main_order_screen.fxml"));
        orderController.setOrderManager(orderManager);
        loaderOrder.setController(orderController);
        Parent orderParent = loaderOrder.load();
        orderScene = new Scene(orderParent, 1300, 800);
        orderScene.setFill(Color.TRANSPARENT);

        FXMLLoader loaderOptions = new FXMLLoader(getClass().getResource("options_screen.fxml"));
        loaderOptions.setController(orderController);
        Parent pop = loaderOptions.load();
        orderController.optionPopup.getContent().add(pop);
        orderController.optionPopup.setAutoHide(true);

        FXMLLoader loadManagement = new FXMLLoader(getClass().getResource("stock_screen.fxml"));
        stockController.setOrderManager(orderManager);
        loadManagement.setController(stockController);
        Parent managementParent = loadManagement.load();
        managementScene = new Scene(managementParent, 1300, 800);


        orderController.preSet(primaryStage, this);
        stockController.preSet(primaryStage, this);

        primaryStage.setTitle("FoodByte");
        primaryStage.setScene(orderScene);
        primaryStage.getIcons().add(new Image("file:assets/icons/icon.png"));
        primaryStage.show();

    }

    /**
     * Switched scene to order screen
     */
    public void switchToOrderScreen() {
        primaryStage.setScene(orderScene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        orderController.populateGrid();
    }

    /**
     * Switches scene to management screen
     */
    public void switchToManagementScreen() {
        primaryStage.setScene(managementScene);
        primaryStage.setMinWidth(1120);
        primaryStage.setMinHeight(600);
        stockController.refreshData();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public OrderManager getOrderManager() {
        return orderManager;
    }
}