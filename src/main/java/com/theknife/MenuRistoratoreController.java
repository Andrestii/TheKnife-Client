package com.theknife;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class MenuRistoratoreController {

    @FXML
    private Label userNameLabel;

    @FXML
    private void initialize() {
        userNameLabel.setText(SessioneUtente.getInstance().getNome());
    }

    @FXML
    private void onOverlayClicked(MouseEvent event) throws Exception {
        App.setRoot("home");
    }

    @FXML
    private void onMieiRistorantiClicked() {
        System.out.println("Apertura sezione 'I miei ristoranti'");
    }

    @FXML
    private void onImpostazioniClicked() {
        System.out.println("Apertura sezione 'Impostazioni'");
    }

    @FXML
    private void onLogoutClicked() throws Exception {
        System.out.println("Logout eseguito");
        SessioneUtente.getInstance().reset();
        App.setRoot("welcome");
    }
}
