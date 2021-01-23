package seng202.teamsix.GUI;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import seng202.teamsix.data.*;
import seng202.teamsix.data.MenuItem;
import seng202.teamsix.managers.OrderManager;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Name: OrderScreenController.java
 * Authors: Taran Jennison, Andy Clifford
 * Date: 07/09/2019
 * Last Updated: 22/09/2019, Andy
 */


/**
 * OrderScreen Controller, used to manage the main order screen of the GUI. This includes
 * a TabPane for each Menu, and a GridPane for each MenuItem within that Menu. a TableView is used
 * to display the current order. Methods for adding and removing items from the OrderManager's cart
 * are provided.
 */
public class OrderScreenController implements Initializable {

    /**
     * The OrderManager will mainly need to be used in the OrderScreenController.
     */
    private OrderManager orderManager;
    public Popup optionPopup = new Popup();
    private Stage window;
    private FoodByteApplication parent;
    private boolean isPopupInit = false;
    private boolean isInit = false;

    @FXML
    private Label date_time;
    @FXML
    private TabPane menu_tabs;
    @FXML
    private Label cost_field;
    @FXML
    private Label currentCash;
    @FXML
    private TableView<OrderTableEntry> order_list_display;
    @FXML
    private Button confirmButton;


    /**
     * Initializes the OrderScreenController i.e. creates a Tab for each MenuItem using helper methods
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (!isInit) {
            cost_field.setText("Cost: " + orderManager.getCart().getTotalCost().toString());
            Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy   HH:mm");
                date_time.setText(LocalDateTime.now().format(formatter));
            }), new KeyFrame(Duration.seconds(1)));
            clock.setCycleCount(Animation.INDEFINITE);
            clock.play();

            initializeOrderDisplayTable();
            populateGrid();

            isInit = true;
        }
    }

    /**
     * Initializes the current order TableView i.e. setups the columns, their widths and cell values,
     * called from the OrderScreenController initializer
     */
    public void initializeOrderDisplayTable() {
        TableColumn itemCol = new TableColumn<MenuItem, String>("Item");
        TableColumn priceCol = new TableColumn<MenuItem, String>("Price");
        TableColumn buttonCol = new TableColumn<MenuItem, Button>("");
        itemCol.setMinWidth(190);
        priceCol.setMinWidth(92);
        buttonCol.setMaxWidth(75);
        itemCol.setCellValueFactory(new PropertyValueFactory("name"));
        priceCol.setCellValueFactory(new PropertyValueFactory("price"));
        buttonCol.setCellValueFactory(new PropertyValueFactory("buttonHBox"));

        itemCol.setSortable(false);
        priceCol.setSortable(false);
        buttonCol.setSortable(false);

        order_list_display.setEditable(false);
        order_list_display.setSelectionModel(null);
        order_list_display.getColumns().clear();
        order_list_display.getColumns().addAll(itemCol, priceCol, buttonCol);
    }

    /**
     * Populates the main order screen with Tabs for each menu, and populates each tab with a
     * GridPane of all the MenuItems within that menu, called from the OrderScreenController initializer.
     */
    public void populateGrid() {
        menu_tabs.getTabs().clear();
        Set<Menu_Ref> menu_refSet = StorageAccess.instance().getAllMenus(); //retrieve uuid of all menus
        for (Menu_Ref menu_ref : menu_refSet) {

            //Create Tab pane and add it to the list of Tab's (menu_tabs)
            Tab tab = createTab(menu_ref, "-fx-background-color: #576574; -fx-pref-width: 175; -fx-pref-height: 50; -fx-font-size: 20;");
            ScrollPane scrollpane = new ScrollPane();
            scrollpane.setFitToWidth(true);
            scrollpane.setFitToHeight(true);
            tab.setContent(scrollpane);
            menu_tabs.getTabs().add(tab);

            //Create GridPane and set it as the content of the Tab
            FlowPane flowPane = createFlowPane();
            scrollpane.setContent(flowPane);

            /* Populate buttons in groups of colours */
            ArrayList<MenuItem> menu_items = StorageAccess.instance().getMenu(menu_ref).getMenuItems();
            menu_items.sort(new Comparator<MenuItem>() {
                @Override
                public int compare(MenuItem o1, MenuItem o2) {
                    return Integer.compare(o1.getColour(), o2.getColour());
                }
            });
            for (MenuItem menu_item: menu_items) {

                    Button button = createButton(menu_item);
                    button.setMinHeight(100);
                flowPane.getChildren().add(button);
            }
        }
        update_current_cash();
    }

    /**
     * Creates a Button object with text associated with MenuItem reference, called from the
     * OrderScreenController initializer.
     * @param menu_item a reference to a MenuItem (uuid)
     * @return a Button object with text set to the name of the MenuItem and userData set to the reference to that item (uuid)
     */
    private Button createButton(MenuItem menu_item) {
        Button button = new Button();
        String buttonText = menu_item.getName();
        button.setText(buttonText);
        button.setUserData(menu_item); //sets the user data of the button to the item reference (uuid)

        //Setup onAction event fort the button i.e. add_to_order
        EventHandler<ActionEvent> actionEvent = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                Button button = (Button)e.getSource(); //gets the button that was clicked
                MenuItem menu_item = (MenuItem)button.getUserData(); //cast the userData of the button to a menuItem
                add_to_order(menu_item, button);
            }
        };
        button.setOnAction(actionEvent);

        //layout options etc.
        button.setMnemonicParsing(false);
        button.setPrefHeight(150.0);
        button.setPrefWidth(200.0);
        button.setWrapText(true);
        button.setContentDisplay(ContentDisplay.CENTER);
        button.setTextAlignment(TextAlignment.CENTER);
        button.setTextOverrun(OverrunStyle.CENTER_ELLIPSIS);
        button.setStyle(String.format("-fx-background-color: #%06X; -fx-font-size: 20; -fx-background-radius: 10;", menu_item.getColour()));

        //Decide on the colour of the text based on the colour of the button
        int background = menu_item.getColour();

        if (menu_item.getColour() != 0x000000) {
            button.setTextFill(Paint.valueOf("#FFFFFF"));
        } else {
            button.setTextFill(Paint.valueOf("#FFFFFF"));
        }

        return button;
    }

    /**
     * Creates a Tab pane with text set to the name associated with the Menu Reference, called
     * from the OrderScreenController initializer
     * @param menu_ref a reference to a Menu
     * @param style the colour and background options etc. from scenebuilder
     * @return a Tab pane with text set to the name associated with the Menu Reference
     */
    private Tab createTab(Menu_Ref menu_ref, String style) {
        Tab tab = new Tab();
        tab.setStyle(style);
        String tabText = StorageAccess.instance().getMenu(menu_ref).getName();
        tab.setText(tabText);
        tab.setClosable(false);
        return tab;
    }

    /**
     * Creates a GridPane and sets options equal to those in SceneBuilder, called from the
     * OrderScreenController initializer
     * @return GridPane object
     */
    private FlowPane createFlowPane() {
        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(10.0);
        flowPane.setVgap(10.0);
        flowPane.setLayoutX(-1.0);
        flowPane.setLayoutY(1.0);
        flowPane.setPrefHeight(611.0);
        flowPane.setPrefWidth(910.0);
        flowPane.setStyle("-fx-background-color: #c8d6e5;");
        flowPane.setMinWidth(10.0);
        flowPane.setPadding(new Insets(10, 10, 10, 10));
        return flowPane;
    }

    /**
     * Sets the row constraints of a GridPane object so that it has rowMax rows.
     * @param grid a GridPane object
     * @param rowMax integer number of rows that the GridPane should have
     */
    private void setRowConstraints(GridPane grid, int rowMax) {
        for (int i = 0; i < rowMax; i++) {
            RowConstraints row = new RowConstraints();
            row.setMinHeight(10.0);
            row.setPrefHeight(30.0);
            row.setValignment(VPos.CENTER);
            row.setVgrow(Priority.ALWAYS);
            grid.getRowConstraints().add(row);
        }
    }

    /**
     * Sets the column constraints of a GridPane object so that it has colMax columns.
     * @param grid a GridPane object
     * @param colMax integer number of columns that the GridPane should have
     */
    private void setColumnConstraints(GridPane grid, int colMax) {
        for (int i = 0; i < colMax; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setMinWidth(10.0);
            column.setPrefWidth(100.0);
            column.setHalignment(HPos.CENTER);
            column.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(column);
        }
    }

    /**
     * Adds a MenuItem to the current order and adds it to the display, called when a specific
     * MenuItem button is clicked in the main screen.
     * @param menu_item MenuItem object to be added to the current order
     */
    public void add_to_order(MenuItem menu_item, Button button) {
        //OrderManager will add the specified item to cart #backend
        OrderItem order_item = orderManager.addToCart(menu_item, true);
        if (order_item != null) {
            OrderTableEntry entry = new OrderTableEntry(menu_item, order_item, this);
            order_list_display.getItems().add(entry); //add the order_item to the table
            cost_field.setText("Cost: " + orderManager.getCart().getTotalCost());
            confirmButton.setDisable(false);
        } else {
            button.setText(menu_item.getName() + " (Out of Stock)");
            button.setDisable(true);
        }

    }

    /**
     * Removes a MenuItem to the current order and removes it from the display, called when the delete
     * button is clicked for a specific item in the order_list_display
     * @param menu_item the MenuItem object to be removed from the current order
     * @param entry the OrderTableEntry to be removed from the display
     */
    public void remove_from_order(MenuItem menu_item, OrderTableEntry entry) {
        order_list_display.getItems().remove(entry);
        orderManager.removeFromCart(menu_item, true);

        cost_field.setText("Cost: " + orderManager.getCart().getTotalCost());
        if (orderManager.getCart().isEmpty()) {
            confirmButton.setDisable(true);
        }
    }

    /**
     * Loads the OrderConfirmation GUI so the current order can be confirmed, called when the Confirm
     * button is clicked
     * @throws IOException
     */
    public void confirm_order() throws IOException {
        FXMLLoader loadConfirmOrder = new FXMLLoader(getClass().getResource("confirm_order.fxml"));

        /* Retrieve all the OrderTableEntry's from the order_list_display so they can
           be passed to the OrderConfirmController */
        ArrayList<OrderTableEntry> orderList = new ArrayList<>();
        for (OrderTableEntry entry: order_list_display.getItems()) {
            orderList.add(entry);
        }
        OrderConfirmController orderConfirmController = new OrderConfirmController(orderList);
        loadConfirmOrder.setController(orderConfirmController);
        Parent confirmOrder = loadConfirmOrder.load();
        orderConfirmController.setOrderManager(orderManager, this);
        Scene confirmOrderScene = new Scene(confirmOrder, 750, 610);
        Stage confirmWindow = new Stage();
        confirmWindow.initModality(Modality.WINDOW_MODAL);
        confirmWindow.initOwner(window);
        confirmWindow.setScene(confirmOrderScene);
        Double centreWidth = window.getWidth()/2 - 380;
        Double centreHeight = window.getHeight()/2 - 320;
        confirmWindow.setX(window.getX() + centreWidth);
        confirmWindow.setY(window.getY() + centreHeight);
        confirmWindow.show();
    }

    /**
     * Clears the current order display table, doesn't remove the items from the order cart however
     */
    public void clear_order() {
        order_list_display.getItems().clear();
        cost_field.setText("Cost: " + orderManager.getCart().getTotalCost());
        confirmButton.setDisable(true);

    }

    /**
     * Clears all items from the current order display table and removes all items from the order cart,
     * called when the cancel button is clicked from the main order screen
     */
    public void cancel_order() {
        orderManager.resetCart();
        clear_order();
        confirmButton.setDisable(true);
    }

    /**
     * Opens the management screen, called when the Management button is clicked
     */
    public void open_management() {
        optionPopup.hide();
        parent.switchToManagementScreen();
    }

    public void set_cash_float() {
        optionPopup.hide();
        SetCashFloat cash = new SetCashFloat(orderManager.getCashRegister(), window, this);
        cash.createNewWindow();
    }

    public void update_current_cash() {
        currentCash.setText("Current cash on hand: " + orderManager.getCashRegister().getRegisterCurrency().toString());
    }

    /**
     * Opens the options popup, called when the Options button is clicked
     */
    public void toggle_options() {
        if (optionPopup.isShowing()) {
            optionPopup.hide();
        } else {
            Double centreHeight = window.getHeight()/2 - 250;
            Double centreWidth = window.getWidth()/2 - 270;
            optionPopup.show(window, window.getX()+centreWidth, window.getY()+centreHeight);
        }
    }

    /**
     * Opens the filters screen, called when the filter button is clicked from the main order screen
     */
    public void open_filters() {}

    public void preSet(Stage primaryStage, FoodByteApplication parent) {
        window = primaryStage;
        this.parent = parent;
    }

    public void setOrderManager(OrderManager orderManager1) {
        orderManager = orderManager1;
    }

    /**
     * Class OrderTableEntry, used to store items in a TableView that displays the current order
     */
    public static class OrderTableEntry {
        private OrderItem order_item;
        private final MenuItem menu_item;
        private final SimpleStringProperty name;
        private final SimpleStringProperty price;
        private final Button deleteButton;
        private final Button editButton;
        private final HBox   buttonHBox;

        private OrderTableEntry(MenuItem menu_item, OrderItem order_item, OrderScreenController parent) {
            final ImageView deleteIcon = new ImageView(getClass().getResource("icons/trash.png").toString());
            deleteIcon.setFitWidth(16);
            deleteIcon.setFitHeight(16);

            final ImageView editIcon = new ImageView(getClass().getResource("icons/editpen.png").toString());
            editIcon.setFitWidth(16);
            editIcon.setFitHeight(16);

            this.order_item = order_item;
            this.menu_item = menu_item;
            this.name = new SimpleStringProperty(menu_item.getName());
            this.price = new SimpleStringProperty(order_item.getPrice().toString());

            this.buttonHBox = new HBox(5);
            this.buttonHBox.setAlignment(Pos.CENTER);

            // Delete Button
            this.deleteButton = new Button("");
            this.buttonHBox.getChildren().add(this.deleteButton);

            this.deleteButton.setGraphic(deleteIcon);
            this.deleteButton.setPadding(new Insets(4,4,4,4));
            this.deleteButton.setStyle("-fx-background-color: #FF0000");

            this.deleteButton.setUserData(this);
            this.deleteButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    parent.remove_from_order(menu_item, (OrderTableEntry) deleteButton.getUserData());
                }
            });

            // Edit button
            this.editButton = new Button("");
            this.buttonHBox.getChildren().add(0, this.editButton);

            this.editButton.setGraphic(editIcon);
            this.editButton.setPadding(new Insets(4,4,4,4));
            this.editButton.setStyle("-fx-background-color: #FFAA00");

            this.editButton.setUserData(this);
            this.editButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    CustomOrderController controller = new CustomOrderController(order_item);
                    controller.createNewWindow(null, parent.window);
                }
            });
        }

        public MenuItem getMenuItem() {return menu_item; }
        public OrderItem getOrderItem() { return order_item; }
        public String getName() { return name.get(); }
        public String getPrice() { return price.get(); }
        public Button getDeleteButton() { return deleteButton; }
        public HBox getButtonHBox() { return buttonHBox; }

    }
}
