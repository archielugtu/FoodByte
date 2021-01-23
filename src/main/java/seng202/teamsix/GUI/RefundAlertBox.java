package seng202.teamsix.GUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

import java.io.IOException;

public class RefundAlertBox implements CustomDialogInterface{
    private Stage stage;

    @FXML
    private Label refundTitleLbl;
    @FXML
    private Label refundAmountLbl;

    private String refundTitleString;
    private String refundAmountString;

    public RefundAlertBox() {}

    public RefundAlertBox(String title, String msg) {
//        refundTitleLbl.setText(title);
//        refundAmountLbl.setText("To be refunded: $" + msg);
        this.refundTitleString = title;
        this.refundAmountString = ("To be refunded: " + msg);
    }

    public RefundAlertBox(String msg) {
        this.refundAmountLbl.setText("To be refunded: $" + msg);
    }

    public void init() {
        refundTitleLbl.setText(refundTitleString);
        refundAmountLbl.setText(refundAmountString);
    }

    public void display() {
        FXMLLoader loaderRefundPopup = new FXMLLoader(getClass().getResource("refund_message.fxml"));
        loaderRefundPopup.setController(this);
        stage.initModality(Modality.APPLICATION_MODAL);

        // If it cannot load fxml the function exits without creating window
        Parent parentCreateItem = null;
        try {
            parentCreateItem = loaderRefundPopup.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * Sets parent stage
     * @param stage parent stage
     */
    public void preSet(Stage stage) {
        this.stage = stage;
    }

    public void OK() {
        stage.close();
    }
}
