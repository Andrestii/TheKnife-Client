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
        App.setRoot("welcome"); // Torna alla schermata di benvenuto
    }

    @FXML
    private void onLoginClicked() throws IOException {
        String target = null;
        if (clienteRadio.isSelected()) {
            target = "clientehome";
        } else if (ristoratoreRadio.isSelected()) {
            target = "ristoratorehome";
        } else {
            System.out.println("Seleziona un ruolo prima di procedere!");
            return;
        }

        // Verify the FXML resource exists before switching to avoid Location not set exceptions
        if (App.class.getResource(target + ".fxml") == null) {
            System.out.println("FXML non trovato per: " + target + " (nessun file " + target + ".fxml nella resources)");
            return;
        }

        App.setRoot(target);
    }
}
