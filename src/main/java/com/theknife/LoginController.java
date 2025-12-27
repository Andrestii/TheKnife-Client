package com.theknife;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private RadioButton clienteRadio;
    @FXML private RadioButton ristoratoreRadio;

    private ToggleGroup ruoloGroup;

    private Stage stage;
    private Scene scene;
    private Parent root;

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
    private void onBackClicked(ActionEvent e) throws IOException {
        //App.setRoot("welcome"); // Eliminare riga

        FXMLLoader loader = new FXMLLoader(getClass().getResource("welcome.fxml"));
        root = loader.load();

        WelcomeController controller = loader.getController();
        controller.setConnectionSocket(socket, in, out);

        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    // TODO: da completare con la logica di autenticazione lato server
    @FXML
    private void onInviaClicked(ActionEvent e) throws IOException {
        // SessioneUtente sessione = SessioneUtente.getInstance();
        // sessione.setUsername(usernameField.getText());
        // sessione.setPassword(passwordField.getText());
        // sessione.setRuolo(clienteRadio.isSelected() ? Ruolo.CLIENTE : Ruolo.RISTORATORE);
        // SessioneUtente.getInstance().stampaDettagli();

        //App.setRoot("home"); // Eliminare riga

        FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));
        root = loader.load();

        HomeController controller = loader.getController();
        controller.setConnectionSocket(socket, in, out);

        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    public void setConnectionSocket(Socket socket, ObjectInputStream in, ObjectOutputStream out){
        this.socket = socket;
        this.in = in;
        this.out = out;
    }
}
