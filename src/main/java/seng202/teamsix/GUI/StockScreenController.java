package seng202.teamsix.GUI;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seng202.teamsix.data.*;
import seng202.teamsix.data.Menu;
import seng202.teamsix.managers.OrderManager;

import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.util.List;
import java.util.ResourceBundle;

/**
 * StockScreenController manages the elements in the stock management screen
 */
public class StockScreenController implements Initializable {
    private List<UUID_Entity> stockList;
    private List<UUID_Entity> itemList;
    private List<UUID_Entity> orderList;
    private List<UUID_Entity> menuList;

    private FoodByteApplication parent;
    private Stage window;

    private TableView<StockTableEntry> stockTable = new TableView<>();
    private TableView<ItemTableEntry> itemTable = new TableView<>();
    private TableView<OrderTableEntry> orderTable = new TableView<>();
    private TableView<MenuTableEntry> menuTable = new TableView<>();

    private ObservableList<StockTableEntry> stockEntries = FXCollections.observableArrayList();
    private ObservableList<ItemTableEntry> itemEntries = FXCollections.observableArrayList();
    private ObservableList<OrderTableEntry> orderEntries = FXCollections.observableArrayList();
    private ObservableList<MenuTableEntry> menuEntries = FXCollections.observableArrayList();


    @FXML
    private StackPane stockTabPane;
    @FXML
    private StackPane itemTabPane;
    @FXML
    private StackPane menuTabPane;
    @FXML
    private StackPane orderTabPane;
    @FXML
    private TextField searchBox;
    @FXML
    private Button clearSearchBtn;
    @FXML
    private Button addButton;
    @FXML
    private Button AddToMenuBtn;
    @FXML
    private GridPane filtergrid;


    // Tabs in table view
    @FXML
    private Tab stockTab;
    @FXML
    private Tab itemTab;
    @FXML
    private Tab menuTab;
    @FXML
    private Tab orderTab;


    private FXMLLoader loader;
    public OrderManager orderManager;

    /**
     * Initialise the GUI
     * @param url url
     * @param resourceBundle resource
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        createPanes();
        // Add tables to panes in tabs
        itemTabPane.getChildren().addAll(itemTable);
        stockTabPane.getChildren().addAll(stockTable);
        orderTabPane.getChildren().addAll(orderTable);
        menuTabPane.getChildren().addAll(menuTable);

        // Set action events for tabs
        stockTab.setOnSelectionChanged(e -> tabChanged("stockTab"));
        itemTab.setOnSelectionChanged(e -> tabChanged("itemTab"));
        menuTab.setOnSelectionChanged(e -> tabChanged("menuTab"));
        orderTab.setOnSelectionChanged(e -> tabChanged("orderTab"));
        tabChanged("stockTab");
    }

    /**
     * Switches to order view by calling method in parent
     */
    public void openOrderView() {
        parent.switchToOrderScreen();
    }

    /**
     * Opens create item screen
     */
    @FXML
    public void addItemAction() {
        CreateItemController itemController = new CreateItemController(null);
        itemController.createNewWindow(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                refreshData();
            }
        });
    }

    public void addToMenuAction() {

                SelectMenuItemController selectedMenuItem = new SelectMenuItemController();
                selectedMenuItem.createNewWindow();

    }

    /**
     * Creates a dialog for adding a new menu
     */
    public void addMenuAction() {
        createDialog(new EditMenu(null), "edit_menu.fxml", "Add Menu");
    }

    public void addStockAction() {
        ItemSelectionController stockItemSelection = new ItemSelectionController();
        stockItemSelection.createNewWindow(parent);
    }

    public void editItemTagsAction() {
        createDialog(new EditItemTags(), "edit_item_tags.fxml", "Edit ItemTags");
    }

    /**
     * Creates a dialog window and uses refreshData when it closes.
     * @param controller controller that implements CustomDialogInterface
     * @param fxml String name of FXML file in classpath e.g: edit_menu.fxml
     * @param title String title of window
     */
    void createDialog(CustomDialogInterface controller, String fxml, String title) {
        try {
            FXMLLoader menuEditDialogLoader = new FXMLLoader(getClass().getResource(fxml));
            menuEditDialogLoader.setController(controller);
            Stage stage = new Stage();
            controller.preSet(stage);
            Parent root = menuEditDialogLoader.load();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            refreshData();
        } catch (java.io.IOException e) {
            System.out.println("Failed to launch dialog: " + e);
        }
    }

    /**
     * Creates a dialog window and uses refreshData when it closes.
     * @param controller controller that implements CustomDialogInterface
     * @param fxml String name of FXML file in classpath e.g: edit_menu.fxml
     * @param title String title of window
     * @param refundAlertBox the alert box for refund
     */
    void createDialog(CustomDialogInterface controller, String fxml, String title, RefundAlertBox refundAlertBox) {
        try {
            FXMLLoader menuEditDialogLoader = new FXMLLoader(getClass().getResource(fxml));
            menuEditDialogLoader.setController(controller);
            Stage stage = new Stage();
            controller.preSet(stage);
            Parent root = menuEditDialogLoader.load();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(title);
            refundAlertBox.init();
            stage.setScene(new Scene(root));
            stage.showAndWait();
//            refreshData();
        } catch (java.io.IOException e) {
            System.out.println("Failed to launch dialog: " + e);
        }
    }

    /**
     * Export StorageAccess data called from export button
     */
    public void exportXML() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("data.xml");
        fileChooser.setTitle("Export XML File");
        File file = fileChooser.showSaveDialog(window);
        if(file != null) {
            boolean result = StorageAccess.instance().exportData(file.getAbsolutePath());
            if (!result) {
                // Show error
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Could not export data.");
                alert.showAndWait();
            }
        }
    }

    /**
     * Import data into StorageAccess called from import button
     */
    public void importXML() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("FOODBYTE Files", "*.xml"));
        fileChooser.setTitle("Import XML File");
        File file = fileChooser.showOpenDialog(window);

        if(file != null) {
            boolean result = StorageAccess.instance().importData(file.getAbsolutePath());
            if (!result) {
                // Show error
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Could not import data.");
                alert.showAndWait();
            }
        }
        refreshData();
    }

    /**
     * Saves system data
     */
    public void saveData() {
        StorageAccess.instance().saveData();
    }

    /**
     * Sets up the parent application and window
     * @param window Stage this controller is in.
     * @param parent Parent application.
     */
    public void preSet(Stage window, FoodByteApplication parent) {
        this.parent = parent;
        this.window = window;
    }

    /**
     * Refreshes data for tables and searches
     */
    private void refreshData(boolean doSearch) {


        DataQuery<StockInstance> stockDataQuery = new DataQuery<>(StockInstance.class);
        DataQuery<Item> itemDataQuery = new DataQuery<>(Item.class);
        DataQuery<Order> orderDataQuery = new DataQuery<>(Order.class);
        DataQuery<Menu> menuDataQuery = new DataQuery<>(Menu.class);
        stockDataQuery.addConstraintEqual("hidden", "false");
        stockDataQuery.addConstraintRegex("notExpired", "true");

        if (doSearch) {
            String searchText = searchBox.getText();

            stockDataQuery.sort_by("name", true);

            itemDataQuery.sort_by("name", true);


            if (searchText.length() != 0) {
                String regex = String.format("(?i).*(%s).*", searchText);
                stockDataQuery.addConstraintRegex("name", regex);
                itemDataQuery.addConstraintRegex("name", regex);
            }
        }

        stockList = stockDataQuery.runQuery();
        itemList = itemDataQuery.runQuery();
        orderList = orderDataQuery.runQuery();
        menuList = menuDataQuery.runQuery();

        getObservableOrderTableEntryList(orderEntries);
        getObservableStockTableEntryList(stockEntries);
        getObservableItemTableEntryList(itemEntries);
        getObservableMenuTableEntryList(menuEntries);
    }


    /**
     * Overloaded function for refreshData to make default option for doSearch false
     */
    public void refreshData() {
        refreshData(false);
    }

    /**
     * Refreshes data with the doSearch true
     */
    public void searchItems() {
        refreshData(true);
    }

    /**
     * Refreshes data and clears search box
     */
    public void clearSearch() {
        refreshData();
        searchBox.setText("");
    }

    /**
     * Updates button at bottom to reflect tab view.
     */
    public void tabChanged(String tabId) {
        if (tabId.equals("itemTab")) {
            addButton.setText("Add Item");
            addButton.setOnAction(e -> addItemAction());
            addButton.setDisable(false);
            AddToMenuBtn.setVisible(false);
            AddToMenuBtn.setDisable(true);
            filtergrid.setDisable(false);
        }
        else if (tabId.equals("menuTab")) {
            addButton.setText("Add Menu");
            addButton.setOnAction(e -> addMenuAction());
            addButton.setDisable(false);
            AddToMenuBtn.setVisible(true);
            AddToMenuBtn.setDisable(false);
            filtergrid.setDisable(true);
        }
        else if (tabId.equals("stockTab")) {
            addButton.setText("Add Stock");
            addButton.setOnAction(e -> addStockAction());
            addButton.setDisable(false);
            AddToMenuBtn.setVisible(false);
            AddToMenuBtn.setDisable(true);
            filtergrid.setDisable(false);
        }
        else {
            addButton.setText("");
            addButton.setDisable(true);
            AddToMenuBtn.setVisible(false);
            AddToMenuBtn.setDisable(true);
            filtergrid.setDisable(true);
        }
    }

    /**
     * Runs table initialisation
     */
    private void createPanes() {
        createStockTable();
        createItemTable();
        createOrderTable();
        createMenuTable();
    }

    /**
     * Creates stock table
     */
    void createStockTable() {
        stockTable = new TableView<>();
        stockTable.setItems(stockEntries);

        //Add name column
        TableColumn<StockTableEntry, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setMinWidth(300);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        stockTable.getColumns().add(nameColumn);

        //Add date added column
        TableColumn<StockTableEntry, String> dateAddedColumn = new TableColumn<>("Date Added");
        dateAddedColumn.setMinWidth(100);
        dateAddedColumn.setCellValueFactory(new PropertyValueFactory<>("date_added"));
        stockTable.getColumns().add(dateAddedColumn);

        //Add expiry date column
        TableColumn<StockTableEntry, String> dateExpiredColumn = new TableColumn<>("Expiry Date");
        dateExpiredColumn.setMinWidth(100);
        dateExpiredColumn.setCellValueFactory(new PropertyValueFactory<>("date_expires"));
        stockTable.getColumns().add(dateExpiredColumn);

        //Add quantity column
        TableColumn<StockTableEntry, String> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setMinWidth(100);
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity_remaining"));
        stockTable.getColumns().add(quantityColumn);
    }

    /**
     * Creates item table
     */
    private void createItemTable() {
        itemTable = new TableView<>();
        itemTable.setItems(itemEntries);

        //Add name column
        TableColumn<ItemTableEntry, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setMinWidth(200);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        itemTable.getColumns().add(nameColumn);

        //Add description column
        TableColumn<ItemTableEntry, String> descColumn = new TableColumn<>("Description");
        descColumn.setMinWidth(200);
        descColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        itemTable.getColumns().add(descColumn);

        //Add base price column
        TableColumn<ItemTableEntry, String> basePriceColumn = new TableColumn<>("Cost Price");
        basePriceColumn.setMinWidth(80);
        basePriceColumn.setCellValueFactory(new PropertyValueFactory<>("base_price"));
        itemTable.getColumns().add(basePriceColumn);

        //Add markup price column
        TableColumn<ItemTableEntry, String> markUpColumn = new TableColumn<>("Sale Price");
        markUpColumn.setMinWidth(80);
        markUpColumn.setCellValueFactory(new PropertyValueFactory<>("markup_price"));
        itemTable.getColumns().add(markUpColumn);

        //Add unit column
        TableColumn<ItemTableEntry, String> unitColumn = new TableColumn<>("Qty units");
        unitColumn.setMinWidth(60);
        unitColumn.setCellValueFactory(new PropertyValueFactory<>("qty_unit"));
        itemTable.getColumns().add(unitColumn);

        //Add edit button column
        TableColumn<ItemTableEntry, Button> addEditButtonColumn = new TableColumn<>();
        addEditButtonColumn.setMinWidth(84);
        addEditButtonColumn.setCellValueFactory(new PropertyValueFactory<>("editItem"));
        itemTable.getColumns().add(addEditButtonColumn);

    }

    /**
     * Creates past order table
     */
    public void createOrderTable() {
        orderTable = new TableView<>();
        orderTable.setItems(orderEntries);

        TableColumn<OrderTableEntry, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setMinWidth(100);
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        orderTable.getColumns().add(dateColumn);

        TableColumn<OrderTableEntry, String> priceColumn = new TableColumn<>("Price");
        priceColumn.setMinWidth(100);
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        orderTable.getColumns().add(priceColumn);

        TableColumn<OrderTableEntry, ToggleButton> refundColumn = new TableColumn<>();
        refundColumn.setMaxWidth(70);
        refundColumn.setCellValueFactory(new PropertyValueFactory<>("refundToggleBtn"));
        orderTable.getColumns().add(refundColumn);
    }

    /**
     * Creates menu table
     */
    public void createMenuTable() {
        menuTable = new TableView<>();
        menuTable.setItems(menuEntries);

        TableColumn<MenuTableEntry, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setMinWidth(100);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        menuTable.getColumns().add(nameColumn);

        TableColumn<MenuTableEntry, String> descColumn = new TableColumn<>("Description");
        descColumn.setMinWidth(500);
        descColumn.setCellValueFactory(new PropertyValueFactory<>("desc"));
        menuTable.getColumns().add(descColumn);

        TableColumn<MenuTableEntry, Button> editBtnColumn = new TableColumn<>();
        editBtnColumn.setMinWidth(90);
        editBtnColumn.setCellValueFactory(new PropertyValueFactory<>("viewButton"));
        menuTable.getColumns().add(editBtnColumn);
    }

    /**
     * Updates observable list for ItemTableEntries
     * @param items Observable list of ItemTableEntries to edit
     */
    private void getObservableItemTableEntryList(ObservableList<ItemTableEntry> items) {
        items.clear();
        for (UUID_Entity entity: itemList) {
            Item item = StorageAccess.instance().getItem(new Item_Ref(entity));
            items.add(new ItemTableEntry(item, this));
        }
    }

    /**
     * Updates observable list for StockTableEntries
     * @param stocks Observable list of StockTableEntries to edit
     */
    private void getObservableStockTableEntryList(ObservableList<StockTableEntry> stocks) {
        stocks.clear();
        for (UUID_Entity entity: stockList) {
            StockInstance stockInstance = StorageAccess.instance().getStockInstance(new StockInstance_Ref(entity));
            Item item = StorageAccess.instance().getItem(stockInstance.getStockItem());
            stocks.add(new StockTableEntry(stockInstance, item));
        }
    }

    /**
     * Updates observable list for OrderTableEntries
     * @param orderEntries Observable list of OrderTableEntries to edit
     */
    private void getObservableOrderTableEntryList(ObservableList<OrderTableEntry> orderEntries) {
        orderEntries.clear();
        for (UUID_Entity entity: orderList) {
            Order order = StorageAccess.instance().getOrder(new Order_Ref(entity));
            orderEntries.add(new OrderTableEntry(order, parent, this));
        }
    }

    /**
     * Updates observable list for MenuTableEntries
     * @param menuEntries Observable list of MenuTableEntries to edit
     */
    private void getObservableMenuTableEntryList(ObservableList<MenuTableEntry> menuEntries) {
        menuEntries.clear();
        for (UUID_Entity entity: menuList) {
            Menu menu = StorageAccess.instance().getMenu(new Menu_Ref(entity));
            menuEntries.add(new MenuTableEntry(menu, this));
        }
    }

    /**
     * Setter function for the orderManager
     * @param orderManager
     */
    public void setOrderManager(OrderManager orderManager) {
        this.orderManager = orderManager;
    }

    /**
     * Class that stores the information needed for each row of the ItemTable
     */
    public static class ItemTableEntry {
        private final Item_Ref item_ref;
        private final SimpleStringProperty name;
        private final SimpleStringProperty description;
        private final SimpleStringProperty base_price;
        private final SimpleStringProperty markup_price;
        private final SimpleStringProperty qty_unit;
        private final Button addStockInstance;
        private final Button editItem;

        private ItemTableEntry(Item item, StockScreenController parent) {
            this.item_ref = item;
            this.name = new SimpleStringProperty(item.getName());
            this.description = new SimpleStringProperty(item.getDescription());
            this.base_price = new SimpleStringProperty(item.getBasePrice().toString());
            this.markup_price = new SimpleStringProperty(item.getMarkupPrice().toString());
            this.qty_unit = new SimpleStringProperty(item.getQtyUnit().toString());
            this.addStockInstance = new Button("Add Stock");
            this.editItem = new Button("Edit Item");
            this.editItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    CreateItemController itemController = new CreateItemController(item_ref);
                    itemController.createNewWindow(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            parent.refreshData();
                        }
                    });
                }
            });
        }

        public String getName() {
            return name.get();
        }
        public String getDescription() {
            return description.get();
        }
        public String getBase_price() {
            return base_price.get();
        }
        public String getMarkup_price() {
            return markup_price.get();
        }
        public String getQty_unit() {
            return qty_unit.get();
        }
        public Item_Ref getItem_ref() {
            return item_ref;
        }
        public Button getAddStockInstance() {
            return addStockInstance;
        }
        public Button getEditItem() {
            return editItem;
        }
    }

    /**
     * Class that stores the information needed for each row of the OrderTable
     */
    public class OrderTableEntry {
        private final ToggleButton refundToggleBtn;
        private final SimpleStringProperty date;
        private final SimpleStringProperty price;


        private OrderTableEntry(Order order, FoodByteApplication orderScreen, StockScreenController parent) {
            DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
            this.date = new SimpleStringProperty(df.format(order.getTimestamp()));
            this.price = new SimpleStringProperty(order.getTotalCost().toString());
            this.refundToggleBtn = new ToggleButton("Refund");
            this.refundToggleBtn.setSelected(order.getIsRefunded());

            this.refundToggleBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    // gets the price of the order
                    Currency orderPrice = order.getTotalCost().roundToCash();
                    CashRegister cashRegister = orderScreen.getOrderManager().getCashRegister();
                    if ((refundToggleBtn.isSelected())) {
                        String orderPriceString = orderPrice.toString();
                        String title;
                        String menuTitle = "Refund Alert!!";

                        if (orderPrice.getTotalCash() <= cashRegister.getRegisterAmount()) {
                            cashRegister.subRegisterAmount(orderPrice);
                            StorageAccess.instance().updateCashRegister(cashRegister);

                            title = "Refund Successfull!";
                            order.setIsRefunded(true);

                            RefundAlertBox refundAlertBox = new RefundAlertBox(title, orderPriceString);
                            parent.createDialog(refundAlertBox, "refund_message.fxml", menuTitle, refundAlertBox);
                            //                        refundAlertBox.init(title, orderPriceString);
                        } else {
                            refundToggleBtn.setSelected(false);
                            title = "Refund Unsuccessfull!";
                            RefundAlertBox refundAlertBox = new RefundAlertBox(title, "NULL");
                            parent.createDialog(refundAlertBox, "refund_message.fxml", menuTitle, refundAlertBox);
                            //                        refundAlertBox.init(title, orderPriceString);
                        }
                    } else {
                        refundToggleBtn.setSelected(false);
                        order.setIsRefunded(false);
                        cashRegister.addRegisterAmount(orderPrice);
                    }
                    StorageAccess.instance().updateOrder(order);
                }
            });

        }

        public String getDate() {
            return date.get();
        }
        public String getPrice() {
            return price.get();
        }
        public ToggleButton getRefundToggleBtn() {
            return refundToggleBtn;
        }
    }

    /**
     * Class that stores the information needed for each row of the MenuTable
     */
    public static class MenuTableEntry {
        private final Menu_Ref menu_ref;
        private final SimpleStringProperty name;
        private final SimpleStringProperty desc;
        private final Button viewButton;

        private MenuTableEntry(Menu menu, StockScreenController parent){
            this.menu_ref = menu;
            name = new SimpleStringProperty(menu.getName());
            desc = new SimpleStringProperty(menu.getDescription());

            viewButton = new Button("Edit Menu");
            viewButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    parent.createDialog(new EditMenu(menu_ref), "edit_menu.fxml", "Edit Menu");
                }
            });
        }

        public String getName() {
            return name.get();
        }
        public String getDesc() {
            return desc.get();
        }
        public Button getViewButton() {
            return viewButton;
        }
    }

    /**
     * Class that stores the information needed for each row of the StockTable
     */
    public static class StockTableEntry {
        private final StockInstance_Ref stockInstance_ref;
        private final SimpleStringProperty name;
        private final SimpleStringProperty date_added;
        private final SimpleStringProperty date_expires;
        private final SimpleStringProperty quantity_remaining;

        private StockTableEntry(StockInstance stockInstance, Item item) {
            this.stockInstance_ref = stockInstance;
            DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);

            this.name = new SimpleStringProperty(item.getName());
            this.date_added = new SimpleStringProperty(df.format(stockInstance.getDateAdded()));
            if (stockInstance.getDoesExpire()) {
                this.date_expires = new SimpleStringProperty(df.format(stockInstance.getDateExpired()));
            } else {
                date_expires = new SimpleStringProperty("N/A");
            }
            this.quantity_remaining = new SimpleStringProperty("" + stockInstance.getQuantityRemaining());
        }
        public String getName() {
            return name.get();
        }
        public String getDate_added() {
            return date_added.get();
        }
        public String getDate_expires() {
            return date_expires.get();
        }
        public String getQuantity_remaining() {
            return quantity_remaining.get();
        }
        public StockInstance_Ref getStockInstance_ref() {
            return stockInstance_ref;
        }
    }
}
