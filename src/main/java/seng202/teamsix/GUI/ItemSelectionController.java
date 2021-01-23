package seng202.teamsix.GUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import seng202.teamsix.data.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ItemSelectionController extends StockScreenController implements Initializable, CustomDialogInterface {
    private Stage stage;
    private Stage controller_window;
    private FoodByteApplication parent;
    private Item item;

    @FXML
    private ListView<Item> listview_item_search;

    @FXML
    private TextField textfield_search_items;

    void createNewWindow(FoodByteApplication parent) {
        this.parent = parent;
        FXMLLoader loaderCreateItem = new FXMLLoader(getClass().getResource("select_item_screen.fxml"));
        loaderCreateItem.setController(this);

        // If it cannot load fxml the function exits without creating window
        Parent parentCreateItem = null;
        try {
            parentCreateItem = loaderCreateItem.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Scene root = new Scene(parentCreateItem, 250, 225);
        controller_window = new Stage();
        controller_window.setTitle("Select Item");
        controller_window.setScene(root);
        controller_window.getIcons().add(new Image("file:assets/icons/icon.png"));
        controller_window.show();
    }


    @FXML
    private void updateItemSearchList() {
        String searchText = textfield_search_items.getText();
        DataQuery<Item> query = new DataQuery<>(Item.class);
        query.sort_by("name", true);

        if (searchText.length() != 0) {
            String regex = String.format("(?i).*(%s).*", searchText);
            query.addConstraintRegex("name", regex);
        }

        List<UUID_Entity> itemref_list = query.runQuery();
        listview_item_search.getItems().clear();
        for (UUID_Entity item_ref : itemref_list) {
            Item item = StorageAccess.instance().getItem(new Item_Ref(item_ref));
            listview_item_search.getItems().add(item);
        }
    }

    public void confirm() {
        Item_Ref item_ref = new Item_Ref();
        if (listview_item_search.getSelectionModel().getSelectedItem() != null) {
            item_ref = listview_item_search.getSelectionModel().getSelectedItem();
        }
        createDialog(new StockInstanceDialog(item_ref, parent), "create_stock_instance.fxml", "Add Stock");
        controller_window.close();
    }

    public void cancel() {controller_window.close();}

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Setup list view of list view
        listview_item_search.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Setup search listener
        textfield_search_items.textProperty().addListener((observable, oldValue, newValue) -> {
            updateItemSearchList();
        });
        updateItemSearchList();

        if(StorageAccess.instance().getAllItems() != null) {
            for(Item_Ref ref : StorageAccess.instance().getAllItems()) {
                Item selected_item = StorageAccess.instance().getItem(ref);
                if (selected_item != null) {
                listview_item_search.getItems().add(selected_item);
                }
            }
        }

        // Setup search listener
        textfield_search_items.textProperty().addListener((observable, oldValue, newValue) -> {
            updateItemSearchList();
        });
        updateItemSearchList();
    }


    @Override
    public void preSet(Stage stage) {
        this.stage = stage;
    }
}
