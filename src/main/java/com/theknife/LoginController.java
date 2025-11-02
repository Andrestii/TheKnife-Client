package com.theknife;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private RadioButton clienteRadio;
    @FXML private RadioButton ristoratoreRadio;

    private ToggleGroup ruoloGroup;

    @FXML
    public void initialize() {
        // create ToggleGroup in controller to avoid malformed FXML toggleGroup attributes
        ruoloGroup = new ToggleGroup();
        if (clienteRadio != null && ristoratoreRadio != null) {
            clienteRadio.setToggleGroup(ruoloGroup);
            ristoratoreRadio.setToggleGroup(ruoloGroup);
        }
    }

    @FXML
    private void onBackClicked() throws IOException {
        App.setRoot("welcome");
    }

    @FXML
    private void onInviaClicked() throws IOException {
        App.setRoot("home");
    }
}
