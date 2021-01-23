package seng202.teamsix.GUI;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import seng202.teamsix.data.ItemTag;
import seng202.teamsix.data.ItemTag_Ref;
import seng202.teamsix.data.StorageAccess;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class EditItemTags implements Initializable, CustomDialogInterface{
    private Stage window;
    private ObservableList<TagTableEntry> tagTableEntries = FXCollections.observableArrayList();
    private ArrayList<ItemTag_Ref> tagList = new ArrayList<>();

    @FXML
    private TextField tagNameInput;
    @FXML
    private TableView<TagTableEntry> itemTagTable;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        createTable();
        refresh();
    }

    @Override
    public void preSet(Stage stage) {
        this.window = stage;
    }

    public void addTag() {
        if (!tagNameInput.getText().matches("[a-zA-Z0-9]+")) {
            StorageAccess.instance().updateItemTag(new ItemTag(tagNameInput.getText(), true));
        }
        refresh();
    }

    public void done() {
        window.close();
    }

    private void refresh() {
        tagList.clear();
        tagList.addAll(StorageAccess.instance().getAllItemTags());
        getObservableTagList(tagTableEntries);
    }

    private void createTable() {
        itemTagTable.setItems(tagTableEntries);

        TableColumn<TagTableEntry, String> nameColumn = new TableColumn<>("Tag Name");
        nameColumn.setMinWidth(200);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        itemTagTable.getColumns().add(nameColumn);

        TableColumn<TagTableEntry, Button> btnColumn = new TableColumn<>();
        btnColumn.setMinWidth(70);
        btnColumn.setCellValueFactory(new PropertyValueFactory<>("btn"));
        itemTagTable.getColumns().add(btnColumn);
    }

    private void getObservableTagList(ObservableList<TagTableEntry> observableList) {
        observableList.clear();
        for (ItemTag_Ref itemTag: tagList) {
            ItemTag tag = StorageAccess.instance().getItemTag(itemTag);
            observableList.add(new TagTableEntry(tag));
        }
    }

    public static class TagTableEntry {
        private final SimpleStringProperty name;
        private final Button delBtn;

        private TagTableEntry(ItemTag tag) {
            name = new SimpleStringProperty(tag.getName());
            delBtn = new Button("Delete");
            delBtn.setDisable(true);
        }

        public String getName() {
            return name.get();
        }

        public Button getBtn() {
            return delBtn;
        }
    }
}
