package com.theknife;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MenuRistoratoreController {

    @FXML
    private Label userNameLabel;

    @FXML
    private void initialize() {
        userNameLabel.setText(SessioneUtente.getInstance().getNome());
    }

    @FXML
    private void onOverlayClicked() throws Exception {
        App.setRoot("home");
    }

    @FXML
    private void onCloseMenuClicked() throws Exception {
        App.setRoot("home");
    }

    @FXML
    private void onMieiRistorantiClicked() {
        System.out.println("Apertura sezione: I miei ristoranti");
    }

    @FXML
    private void onImpostazioniClicked() throws Exception {
        App.setRoot("impostazioni");
    }

    @FXML
    private void onLogoutClicked() throws Exception {
        System.out.println("Logout eseguito");
        SessioneUtente.getInstance().reset();
        App.setRoot("welcome");
    }
}
