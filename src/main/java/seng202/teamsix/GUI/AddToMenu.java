package seng202.teamsix.GUI;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import seng202.teamsix.data.*;
import seng202.teamsix.data.Menu;
import seng202.teamsix.data.MenuItem;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class AddToMenu implements Initializable, CustomDialogInterface {
    private Stage stage;
    private Item_Ref item_ref;
    private final List<UUID_Entity> menus;
    private Boolean extraConfirm = false;
    private int[] colourNum = {0x000000, 0x32a852, 0xa83232, 0x2d54bd, 0x9c5824, 0xc93ab2, 0x9233b5};
    private String[] colourName = {"Black", "Green", "Red", "Blue", "Brown", "Pink", "Purple"};

    @FXML
    private TextField priceInput;
    @FXML
    private TextField descriptionInput;
    @FXML
    private ComboBox<String> menuDropdown;
    @FXML
    private ComboBox<String> colourDropDown;
    @FXML
    private Label titleLbl;
    @FXML
    private Label errorBox;

    public AddToMenu(Item_Ref item_ref, List<UUID_Entity> menu) {
        this.item_ref = item_ref;
        this.menus = menu;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Item item = StorageAccess.instance().getItem(item_ref);
        String name = item.getName();
        titleLbl.setText("Add \"" + name + "\" to a Menu");

        priceInput.setText(Double.toString(item.getMarkupPrice().getTotalCash()));

        for (UUID_Entity entity: menus) {
            menuDropdown.getItems().add(StorageAccess.instance().getMenu(new Menu_Ref(entity)).getName());
        }
        colourDropDown.getItems().addAll(colourName);
    }

    public void cancel() {
        System.out.println("hit");
        stage.close();
    }

    /**
     * Checks that the inputs are valid and updates the menu with the new item
     */
    public void confirm() {
        if (checkInputs()) {
            float rawPrice = Float.parseFloat(priceInput.getText());
            Currency price = new Currency(rawPrice);
            Menu menu = StorageAccess.instance().getMenu(new Menu_Ref(menus.get(menuDropdown.getSelectionModel().getSelectedIndex())));
            MenuItem newMenuItem = new MenuItem();
            newMenuItem.setItem(item_ref);
            newMenuItem.setDescription(descriptionInput.getText());
            newMenuItem.setName(StorageAccess.instance().getItem(item_ref).getName());
            newMenuItem.setPrice(price);
            newMenuItem.setColour(colourNum[colourDropDown.getSelectionModel().getSelectedIndex()]);
            menu.addToMenu(newMenuItem);

            StorageAccess.instance().updateMenu(menu);
            stage.close();
        }
    }

    /**
     * Checks inputs
     * @return true if inputs valid
     */
    private boolean checkInputs() {
        errorBox.setText("");
        Item item = StorageAccess.instance().getItem(item_ref);
        try {
            if (extraConfirm) {
                extraConfirm = false;
                return true;
            }
            double price = Double.parseDouble(priceInput.getText());
            if (price <= 0) {
                errorBox.setText("Price must be\ngreater than $0");
                return false;
            } else if (price < item.getMarkupPrice().getTotalCash()) {
                errorBox.setText("Warning! Current price is lower\nthan recorded sale price\nconfirm again to bypass");
                extraConfirm = true;
                return false;
            }
            if (menuDropdown.getSelectionModel().getSelectedIndex() != -1 && colourDropDown.getSelectionModel().getSelectedIndex() != -1) {
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException e) {
            errorBox.setText("Price must be a number");
            return false;
        }
    }

    /**
     * Sets parent stage
     * @param stage
     */
    public void preSet(Stage stage) {
        this.stage = stage;
    }

}
