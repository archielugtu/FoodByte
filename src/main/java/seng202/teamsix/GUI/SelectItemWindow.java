package seng202.teamsix.GUI;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seng202.teamsix.data.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SelectItemWindow implements Initializable {
    private Stage controller_window;
    private boolean single_selectmode = true;

    @FXML
    private TextField textfield_search;

    @FXML
    private ComboBox<String> combobox_itemtype;

    @FXML
    private ListView<Item_Ref> listview_items;

    @FXML
    private Button button_confirm;

    public List<Item_Ref> showAndWait(boolean is_single_select, Stage parent_window) {
        FXMLLoader loaderSelectItem = new FXMLLoader(getClass().getResource("select_item.fxml"));
        loaderSelectItem.setController(this);

        // If it cannot load fxml the function exits without creating window
        Parent parentCreateItem = null;
        try {
            parentCreateItem = loaderSelectItem.load();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        single_selectmode = is_single_select;

        // Create window
        Scene root = new Scene(parentCreateItem);
        controller_window = new Stage();
        controller_window.initModality(Modality.WINDOW_MODAL);
        controller_window.initOwner(parent_window);
        controller_window.setScene(root);
        controller_window.getIcons().add(new Image("file:assets/icons/icon.png"));
        controller_window.setTitle("Select Items");
        controller_window.showAndWait();

        return listview_items.getSelectionModel().getSelectedItems();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        combobox_itemtype.getItems().add("Any");
        combobox_itemtype.getItems().add("Ingredient");
        combobox_itemtype.getItems().add("Product");
        combobox_itemtype.getSelectionModel().select(combobox_itemtype.getItems().get(0));

        textfield_search.textProperty().addListener((observable, oldValue, newValue) -> {
            refreshItemList();
        });
        refreshItemList();

        if(single_selectmode) {
            listview_items.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        } else {
            listview_items.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        }
        listview_items.setCellFactory(e -> new ItemRefCell());
        listview_items.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Item_Ref>() {
            @Override
            public void changed(ObservableValue<? extends Item_Ref> observable, Item_Ref oldValue, Item_Ref newValue) {
                button_confirm.setDisable(listview_items.getSelectionModel().getSelectedItems().size() == 0);
            }
        });

        button_confirm.setDisable(true);
    }

    void refreshItemList() {
        String searchText = textfield_search.getText();
        DataQuery<Item> query = new DataQuery<>(Item.class);
        query.sort_by("name", true);

        if (searchText.length() != 0) {
            String regex = String.format("(?i).*(%s).*", searchText);
            query.addConstraintRegex("name", regex);
        }

        List<UUID_Entity> itemref_list = query.runQuery();
        listview_items.getItems().clear();
        for (UUID_Entity item_ref : itemref_list) {
            Item item = StorageAccess.instance().getItem(new Item_Ref(item_ref));
            if ( (item instanceof CompositeItem && combobox_itemtype.getSelectionModel().getSelectedItem().equals("Product"))
              || (!(item instanceof CompositeItem) && combobox_itemtype.getSelectionModel().getSelectedItem().equals("Ingredient"))
              || (combobox_itemtype.getSelectionModel().getSelectedItem().equals("Any")))
            listview_items.getItems().add(new Item_Ref(item_ref));
        }
    }

    @FXML
    void confirmClicked(ActionEvent event) {
        controller_window.close();
    }

    @FXML
    void on_searchchange() {
        refreshItemList();
    }

    @FXML
    void itemtypeChanged() {
        refreshItemList();
    }
}

class ItemRefCell extends ListCell<Item_Ref> {
    @Override
    protected void updateItem(Item_Ref item_ref, boolean empty) {
        super.updateItem(item_ref, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            Item item = StorageAccess.instance().getItem(new Item_Ref(item_ref));
            if(item != null){
                setText(item.getName());
            } else {
                setText(null);
            }
            setGraphic(null);
        }
    }
}