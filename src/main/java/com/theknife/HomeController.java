package com.theknife;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class HomeController {

    @FXML
    private TextField searchField;

    @FXML
    private void onBackClicked() throws IOException {
        App.setRoot("welcome");
    }

    // Da implementare il SessioneUtente per gestire i ruoli
    /*
    @FXML
    private void onUserIconClicked() throws IOException {
        var ruolo = SessioneUtente.getInstance().getRuolo();
        switch (ruolo) {
            case GUEST -> App.setRoot("welcome");
            case CLIENTE -> App.setRoot("menuCliente");      // placeholder
            case RISTORATORE -> App.setRoot("menuRistoratore"); // placeholder
        }
    }
    */

    @FXML
    private void onSearchClicked() {
        System.out.println("Ricerca avviata per: " + searchField.getText());
    }
}
