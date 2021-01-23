/**
 * Name: ItemTableData
 * Authors: George Stephenson
 */
package seng202.teamsix.GUI;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import seng202.teamsix.data.StockInstance;

import java.awt.*;

/*
 * Is package private as it is only needed by GUI.
 */
class ItemTableData {
    private GridPane root;

    /**
     * Creates Gridpane which contains buttons and labels to do with given
     * stock instance. This is to be used in a ScrollPane.
     */
    public ItemTableData() {
        GridPane grid = new GridPane();
        Button editBtn = new Button("Edit");
        grid.add(editBtn, 0, 0);
        root = grid;
    }

    /**
     * Returns the GridPane that
     * @return GridPane
     */
    public GridPane getGrid() {
        return root;
    }
}
