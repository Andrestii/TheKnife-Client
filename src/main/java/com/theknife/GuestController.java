package com.theknife;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class GuestController {

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

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

    public void setConnectionSocket(Socket socket, ObjectInputStream in, ObjectOutputStream out){
        this.socket = socket;
        this.in = in;
        this.out = out;
    }
}