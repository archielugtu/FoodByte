package seng202.teamsix.GUI;

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
import java.util.ResourceBundle;



public class SetCashFloat implements Initializable {
    private Stage mainStage;
    private CashRegister register;
    private Stage controller_window;
    private OrderScreenController orderController;


    @FXML
    private TextField cashInput;
    @FXML
    private Label errorBox;

    void createNewWindow() {
        FXMLLoader loaderCreateItem = new FXMLLoader(getClass().getResource("cash_float_screen.fxml"));
        loaderCreateItem.setController(this);

        // If it cannot load fxml the function exits without creating window
        Parent parentCreateItem = null;
        try {
            parentCreateItem = loaderCreateItem.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Scene root = new Scene(parentCreateItem, 400, 300);
        controller_window = new Stage();
        cashInput.setPromptText(Double.toString(register.getRegisterAmount()));
        controller_window.setTitle("Set cash float");

        controller_window.initModality(Modality.WINDOW_MODAL);
        controller_window.initOwner(mainStage);
        controller_window.setScene(root);
        controller_window.getIcons().add(new Image("file:assets/icons/icon.png"));
        controller_window.show();
    }

    public SetCashFloat(CashRegister register, Stage mainWindow, OrderScreenController orderController) {
        this.orderController = orderController;
        this.register = register;
        mainStage = mainWindow;
    }

    public void cancel() {
        controller_window.close();
    }

    public void confirm() {
        if (checkInputs()) {
            Double cash = Double.parseDouble(cashInput.getText());
            register.setRegisterAmount(cash);
            orderController.update_current_cash();
            StorageAccess.instance().updateCashRegister(register);
            controller_window.close();
        }
    }

    private boolean checkInputs() {
        errorBox.setText("");
        try {
            double cashValue = Double.parseDouble(cashInput.getText());
            if (cashValue < 0) {
                errorBox.setText("Price must be\ngreater than or equal to $0");
                return false;
            } else if (cashValue >= 2000000) {
                errorBox.setText("Error: Cash float is expected\nto be below $2,000,000");
                return false;
            }
        } catch(NumberFormatException e){
                errorBox.setText("Price must be a number");
                return false;
        }
        return true;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
