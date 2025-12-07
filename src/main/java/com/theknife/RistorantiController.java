package com.theknife;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;

public class RistorantiController {

    @FXML private FlowPane listaRistoranti;

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    @FXML
    private void initialize() {
        // Per ora non ci sono ristoranti --> lista vuota
        // Quando li avrai nel DB, qui li caricheremo dinamicamente
    }

    @FXML
    private void onBackClicked() throws IOException {
        App.setRoot("menuRistoratore");
    }

    @FXML
    private void onCreaRistoranteClicked() throws IOException {
        App.setRoot("creaRistorante");
        // creerai poi la pagina creaRistorante.fxml
    }

    public void setConnectionSocket(Socket socket, ObjectInputStream in, ObjectOutputStream out){
        this.socket = socket;
        this.in = in;
        this.out = out;
    }
}
