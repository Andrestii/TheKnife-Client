package com.theknife;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;

public class RistorantiController {

    @FXML private FlowPane listaRistoranti;

    @FXML
    private void initialize() {
        // Per ora non ci sono ristoranti --> lista vuota
        // Quando li avrai nel DB, qui li caricheremo dinamicamente
    }

    @FXML
    private void onBackClicked() throws IOException {
        App.setRoot("home");
    }

    @FXML
    private void onCreaRistoranteClicked() throws IOException {
        App.setRoot("creaRistorante");
        // creerai poi la pagina creaRistorante.fxml
    }
}
