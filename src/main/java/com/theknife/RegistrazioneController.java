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

    @FXML
    public void initialize() {
        // Crea il ToggleGroup e collega i RadioButton
        ruoloGroup = new ToggleGroup();
        clienteRadio.setToggleGroup(ruoloGroup);
        ristoratoreRadio.setToggleGroup(ruoloGroup);
    }

    @FXML
    private void onBackClicked() throws IOException {
        App.setRoot("welcome"); // Torna alla schermata principale
    }

    @FXML
    private void onSubmitClicked() throws IOException {
        App.setRoot("home"); // Per ora solo cambio schermata
    }
}
