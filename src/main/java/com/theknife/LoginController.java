package com.theknife;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private RadioButton clienteRadio;
    @FXML private RadioButton ristoratoreRadio;

    private ToggleGroup ruoloGroup;

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    // Serve a rendere mutuamente esclusivi i due RadioButton (“Cliente” o “Ristoratore”) 
    // e garantire che funzioni anche se l’FXML è parzialmente modificato o corrotto.
    @FXML
    public void initialize() { 
        ruoloGroup = new ToggleGroup();
        if (clienteRadio != null && ristoratoreRadio != null) {
            clienteRadio.setToggleGroup(ruoloGroup);
            ristoratoreRadio.setToggleGroup(ruoloGroup);
        }
    }

    @FXML
    private void onBackClicked() throws IOException {
        App.setRoot("welcome");
    }

    // TODO: da completare con la logica di autenticazione lato server
    @FXML
    private void onInviaClicked() throws IOException {
        // SessioneUtente sessione = SessioneUtente.getInstance();
        // sessione.setUsername(usernameField.getText());
        // sessione.setPassword(passwordField.getText());
        // sessione.setRuolo(clienteRadio.isSelected() ? Ruolo.CLIENTE : Ruolo.RISTORATORE);
        // SessioneUtente.getInstance().stampaDettagli();

        App.setRoot("home");
    }

    public void setConnectionSocket(Socket socket, ObjectInputStream in, ObjectOutputStream out){
        this.socket = socket;
        this.in = in;
        this.out = out;
    }
}
