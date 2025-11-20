package com.theknife;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MenuClienteController {

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
    private void onImpostazioniClicked() throws Exception {
        App.setRoot("impostazioni");
   }

    @FXML
    private void onPreferitiClicked() {
        System.out.println("Preferiti cliccato");
    }

    @FXML
    private void onRecensioniClicked() {
        System.out.println("Recensioni cliccato");
    }

    @FXML
    private void onLogoutClicked() throws Exception {
        System.out.println("Logout eseguito");
        SessioneUtente.getInstance().reset();
        App.setRoot("welcome");
    }
}
