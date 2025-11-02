package com.theknife;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegistrazioneController {

    @FXML private TextField nomeField;
    @FXML private TextField cognomeField;
    @FXML private TextField dataNascitaField;
    @FXML private TextField domicilioField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confermaPasswordField;
    @FXML private RadioButton clienteRadio;
    @FXML private RadioButton ristoratoreRadio;

    private ToggleGroup ruoloGroup;

    // Serve a rendere mutuamente esclusivi i due RadioButton (“Cliente” o “Ristoratore”) 
    // e garantire che funzioni anche se l’FXML è parzialmente modificato o corrotto.
    @FXML
    public void initialize() {
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
    private void onSubmitClicked() throws IOException {
        App.setRoot("home");
    }
}
