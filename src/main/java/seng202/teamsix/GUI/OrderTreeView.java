package seng202.teamsix.GUI;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import seng202.teamsix.data.*;

import java.util.List;

public class OrderTreeView extends TreeView<OrderItem> {

    OrderTreeView(OrderItem root_order) {
        setCellFactory(e -> new OrderTreeCell());

        TreeItem<OrderItem> root = createTreeFromOrderItem(root_order);
        root.setExpanded(true);

        this.setRoot(root);
        this.setShowRoot(true);
    }

    protected TreeItem<OrderItem> createTreeFromOrderItem(OrderItem root_order) {
        TreeItem<OrderItem> root = new TreeItem<>(root_order);

        Item item = StorageAccess.instance().getItem(root_order.getItem());
        if(!(item instanceof VariantItem)) {
            for (OrderItem dependant_order : root_order.getDependants()) {
                root.getChildren().add(createTreeFromOrderItem(dependant_order));
            }
        }
        return root;
    }
}

class OrderTreeCell extends TreeCell<OrderItem> {

    @Override
    protected void updateItem(OrderItem order_item, boolean empty) {
        final double icon_size = 16;
        final double icon_padding = 4;
        final ImageView addQtyIcon = new ImageView(getClass().getResource("icons/plus.png").toString());
        addQtyIcon.setFitWidth(icon_size); addQtyIcon.setFitHeight(icon_size);
        final ImageView subQtyIcon = new ImageView(getClass().getResource("icons/minus.png").toString());
        subQtyIcon.setFitWidth(icon_size); subQtyIcon.setFitHeight(icon_size);
        final ImageView nextVariantIcon = new ImageView(getClass().getResource("icons/right_arrow.png").toString());
        nextVariantIcon.setFitWidth(icon_size); nextVariantIcon.setFitHeight(icon_size);
        final ImageView prevVariantIcon = new ImageView(getClass().getResource("icons/left_arrow.png").toString());
        prevVariantIcon.setFitWidth(icon_size); prevVariantIcon.setFitHeight(icon_size);
        final ImageView addItemIcon = new ImageView(getClass().getResource("icons/plus_circle.png").toString());
        addItemIcon.setFitWidth(icon_size+icon_padding); addItemIcon.setFitHeight(icon_size+icon_padding);


        super.updateItem(order_item, empty);

        // If the cell is empty we don't show anything.
        if (isEmpty()) {
            setGraphic(null);
            setText(null);
        } else {
            Button addQuantityButton = new Button("");
            addQuantityButton.setGraphic(addQtyIcon);
            addQuantityButton.setPadding(new Insets(icon_padding));
            addQuantityButton.setStyle("-fx-background-color: #2bff00");
            addQuantityButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    order_item.setQuantity(order_item.getQuantity() + 1);
                    getTreeView().refresh();
                }
            });

            Button subQuantityButton = new Button("");
            subQuantityButton.setGraphic(subQtyIcon);
            subQuantityButton.setPadding(new Insets(icon_padding));
            subQuantityButton.setStyle("-fx-background-color: #ff493f");
            subQuantityButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    if ((getTreeItem().getParent() == null && order_item.getQuantity() > 1) || getTreeItem().getParent() != null){
                        order_item.setQuantity(order_item.getQuantity() - 1);
                    }


                    if (order_item.getQuantity() == 0) {
                        if(order_item.getParent() != null) {
                            order_item.getParent().removeFromOrder(order_item);
                        }
                        if(getTreeItem().getParent() != null) {
                            getTreeItem().getParent().getChildren().remove(getTreeItem());
                        }

                    }
                    getTreeView().refresh();
                }
            });

            HBox buttonHBox = new HBox(5);
            buttonHBox.setAlignment(Pos.CENTER_RIGHT);

            Label label = null;

            Item item = StorageAccess.instance().getItem(order_item.getItem());
            if(item instanceof VariantItem) {
                OrderItem current_variant_order = order_item.getDependants().get(0);
                Item variant_item = StorageAccess.instance().getItem(current_variant_order.getItem());

                label = new Label(variant_item.getName() + " x " + order_item.getQuantity());

                Button nextVariantButton = new Button("");
                nextVariantButton.setGraphic(nextVariantIcon);
                nextVariantButton.setPadding(new Insets(icon_padding));
                nextVariantButton.setStyle("-fx-background-color: #56d1ff");
                nextVariantButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        order_item.swapWithNextVariant();
                        getTreeView().refresh();
                    }
                });
                Button prevVariantButton = new Button("");
                prevVariantButton.setGraphic(prevVariantIcon);
                prevVariantButton.setPadding(new Insets(icon_padding));
                prevVariantButton.setStyle("-fx-background-color: #56d1ff");
                prevVariantButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        order_item.swapWithPrevVariant();
                        getTreeView().refresh();
                    }
                });
                buttonHBox.getChildren().addAll(prevVariantButton, nextVariantButton);
            } else if(item instanceof CompositeItem) {
                label = new Label(item.getName() + " x " + order_item.getQuantity());

                double addItemRadius = 4;
                Button addItemButton = new Button("");
                addItemButton.setGraphic(addItemIcon);
                addItemButton.setPadding(new Insets(0));
                addItemButton.setStyle("-fx-background-color: #ffffff00");
                addItemButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        SelectItemWindow item_selection = new SelectItemWindow();
                        List<Item_Ref> selected_items = item_selection.showAndWait(true, (Stage)getTreeView().getScene().getWindow());
                        if(selected_items.size() > 0) {
                            OrderItem added_orderitem = order_item.addToOrder(selected_items.get(0), 1, null, 0);
                            getTreeItem().getChildren().add(((OrderTreeView) getTreeView()).createTreeFromOrderItem(added_orderitem));
                            getTreeView().refresh();
                        }
                    }
                });
                buttonHBox.getChildren().add(addItemButton);
            } else {
                label = new Label(item.getName() + " x " + order_item.getQuantity());

            }

            buttonHBox.getChildren().addAll(subQuantityButton, addQuantityButton);


            BorderPane cellPane = new BorderPane();
            cellPane.setLeft(label);
            cellPane.setRight(buttonHBox);

            setGraphic(cellPane);
            setText(null);
        }
    }
}