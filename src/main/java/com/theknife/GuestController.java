package com.theknife;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class GuestController {

    @FXML
    private TextField posizioneField;

    @FXML
    private void onBackClicked() throws IOException {
        App.setRoot("welcome");
    }

    @FXML
    private void onInviaClicked() throws IOException {
        String luogo = posizioneField.getText();

        SessioneUtente sessione = SessioneUtente.getInstance();
        sessione.reset(); // pulisci tutto
        sessione.setLuogo(luogo);
        sessione.setRuolo(Ruolo.GUEST);
        SessioneUtente.getInstance().stampaDettagli();

        App.setRoot("home");
    }
}