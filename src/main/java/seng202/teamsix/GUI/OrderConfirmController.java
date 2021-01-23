package seng202.teamsix.GUI;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import seng202.teamsix.data.*;
import seng202.teamsix.data.MenuItem;
import seng202.teamsix.managers.OrderManager;
import seng202.teamsix.data.Currency;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Name: OrderScreenController.java
 * Authors: Andy Clifford, Taran Jennison
 * Date: 15/09/2019
 * Last Updated: 24/09/2019, Andy
 */

/**
 * OrderConfirmController, used to manage the order confirmation screen of the GUI. Includes a
 * TableView of the current order so it can still be viewed before confirming the order, and options
 * for an eftpos or cash transaction. Items can not be added or removed from the confirmation screen.
 */
public class OrderConfirmController implements Initializable {

    private HashMap<String, Currency> currencyConvert = new HashMap<String, Currency>();
    private Currency totalChange = new Currency();
    private Currency orderCost = new Currency(); //get order cost
    private boolean isEftpos = false;
    private OrderManager orderManager;
    private OrderScreenController orderScreenController;
    private Popup changePopup = new Popup();
    private ArrayList<OrderTableEntry> orderList;

    @FXML
    private TableView<OrderTableEntry> order_list_display;
    @FXML
    private TableColumn<OrderItem, String> itemCol;
    @FXML
    private TableColumn<Currency, String> priceCol;
    @FXML
    private Label cost_field;
    @FXML
    private Label change_field;
    @FXML
    private Button confirmButton;
    @FXML
    private Label changeDueField;

    /**
     * Constructor for the OrderConfirmController. Used to pass in table data (OrderTableEntry's) from the main order screen so it
     * can be replicated in the order confirmation screen.
     * @param orderList
     */
    public OrderConfirmController(ArrayList<OrderScreenController.OrderTableEntry> orderList) {
        this.orderList = new ArrayList<>();
        for (OrderScreenController.OrderTableEntry entry: orderList) {
            MenuItem menu_item = entry.getMenuItem();
            OrderTableEntry e = new OrderTableEntry(menu_item, this);
            this.orderList.add(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        change_field.setText("Change: " + totalChange);
        cost_field.setText("Cost: ");
        currencyConvert.put("10c", new Currency(0.1));
        currencyConvert.put("20c", new Currency(0.2));
        currencyConvert.put("50c", new Currency(0.5));
        currencyConvert.put("$1", new Currency(1));
        currencyConvert.put("$2", new Currency(2));
        currencyConvert.put("$5", new Currency(5));
        currencyConvert.put("$10", new Currency(10));
        currencyConvert.put("$20", new Currency(20));
        currencyConvert.put("$50", new Currency(50));
        currencyConvert.put("$100", new Currency(100));

        TableColumn itemCol = new TableColumn<MenuItem, String>("Item");
        TableColumn priceCol = new TableColumn<MenuItem, String>("Price");
        itemCol.setMinWidth(270);
        priceCol.setMinWidth(90);
        itemCol.setCellValueFactory(new PropertyValueFactory("name"));
        priceCol.setCellValueFactory(new PropertyValueFactory("price"));
        itemCol.setSortable(false);
        priceCol.setSortable(false);
        order_list_display.getColumns().clear();
        order_list_display.setSelectionModel(null);
        order_list_display.getColumns().addAll(itemCol, priceCol);

        for (OrderTableEntry entry: orderList) {
            order_list_display.getItems().add(entry);
        }
    }

    /**
     * Adds the amount to the totalChange received from the buyer buy the amount of the calculator button that was clicked on
     * @param event the calculator button that was clicked on
     */
    public void add_change(ActionEvent event) {
        String input = ((Button) event.getSource()).getText();
        if (input.equals("Clear")) {
            totalChange.setTotalCash(0);
            change_field.setText("Change: " + totalChange);
        } else {
            totalChange.addCash(currencyConvert.get(input));
            change_field.setText("Change: " + totalChange);
        }
        if (totalChange.compareTo(orderCost) <= 0 || isEftpos) {
            confirmButton.setDisable(false);
        } else {
            confirmButton.setDisable(true);
        }
    }

    /**
     * Called when the eftpos toggle button is clicked
     */
    public void eftpos_toggle() {
        isEftpos = !isEftpos;
        if (totalChange.compareTo(orderCost) <= 0 || isEftpos) {
            confirmButton.setDisable(false);
        } else {
            confirmButton.setDisable(true);
        }
    }

    /**
     * Closes the order confirmation screen, called when the Back button is clicked
     * @param event Cancel order button click
     */
    public void cancel_confirm(ActionEvent event) {
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.close();
    }

    /**
     * Confirms the current order, called when the Confirm button is clicked from the order confirmation
     * screen
     * @param event Contirm order button click
     * @throws IOException
     */
    public void confirm_order(ActionEvent event) throws IOException {
        orderManager.finaliseOrder(isEftpos);
        confirmButton.setDisable(true);
        orderScreenController.clear_order();
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.close();
        if (!isEftpos) {
            displayChange(window);
        }
        orderScreenController.update_current_cash();
    }

    /**
     * Displays a popup on the main order screen that shows the change due to the customer
     * @param window
     * @throws IOException
     */
    private void displayChange(Stage window) throws IOException {
        Window mainScreen = window.getOwner();
        FXMLLoader loaderOptions = new FXMLLoader(getClass().getResource("change_due.fxml"));
        loaderOptions.setController(this);
        Parent changeNode = loaderOptions.load();
        changePopup.getContent().add(changeNode);
        changePopup.setAutoHide(true);
        changeDueField.setText(String.format("$%.1f0",(float) orderCost.compareTo(totalChange)/100));
        Double centreHeight = mainScreen.getHeight()/2 - 250;
        Double centreWidth = mainScreen.getWidth()/2 - 270;
        changePopup.show(mainScreen, mainScreen.getX()+centreWidth, mainScreen.getY()+centreHeight);
    }

    public void close_change() {
        changePopup.hide();
    }

    public void setOrderManager(OrderManager order_manager, OrderScreenController orderController) {
        orderManager = order_manager;
        orderScreenController = orderController;
        orderCost = orderManager.getCart().getTotalCost();
        cost_field.setText("Cost: " + orderCost);

    }

    /**
     * Class OrderTableEntry, used to store items in a TableView that displays the current order
     */
    public static class OrderTableEntry {
        private final MenuItem menu_item;
        private final SimpleStringProperty name;
        private final SimpleStringProperty price;

        private OrderTableEntry(MenuItem menu_item, OrderConfirmController parent) {
            this.menu_item = menu_item;
            this.name = new SimpleStringProperty(menu_item.getName());
            this.price = new SimpleStringProperty(menu_item.getPrice().toString());
        }

        public String getName() { return name.get(); }
        public String getPrice() { return price.get(); }
    }

}
