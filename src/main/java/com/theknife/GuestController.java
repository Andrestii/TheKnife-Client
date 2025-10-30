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
        // Per ora: torna semplicemente alla home principale
        App.setRoot("apphome"); // quando creerai la schermata apphome.fxml
    }
}