package com.theknife;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MenuRistoratoreController {

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

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
    private void onMieiRistorantiClicked() throws Exception {
        App.setRoot("ristoranti");
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

    public void setConnectionSocket(Socket socket, ObjectInputStream in, ObjectOutputStream out){
        this.socket = socket;
        this.in = in;
        this.out = out;
    }
}
