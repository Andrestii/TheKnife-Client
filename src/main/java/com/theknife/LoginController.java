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
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller della schermata di login.
 * Gestisce l'inserimento delle credenziali e invia al server la richiesta di
 * autenticazione,
 * aggiornando la sessione utente e aprendo la schermata Home in caso di
 * successo.
 */
public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    private Stage stage;
    private Parent root;
    private Parent previousRoot;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    @FXML
    private void onBackClicked(ActionEvent e) throws IOException {
        try {
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            if (previousRoot != null)
                stage.getScene().setRoot(previousRoot);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void onInviaClicked(ActionEvent e) throws IOException {
        SessioneUtente sessione = SessioneUtente.getInstance();
        sessione.setUsername(usernameField.getText());
        sessione.setPassword(passwordField.getText());
        sessione.stampaDettagli();

        // Inserisco dati nel db
        out.writeObject("login");
        out.writeObject(sessione.getUsername());
        out.writeObject(sessione.getPassword());
        out.flush();

        // Controllo se il login è andato a buon fine e cambio schermata
        ServerResponse response;
        try {
            response = (ServerResponse) in.readObject();
        } catch (ClassNotFoundException ex) {
            throw new IOException("Risposta del server non valida", ex);
        }

        if (response.status.equals("OK")) {
            // Aggiorno la sessione con i dati ricevuti
            Utente user = new Utente(
                    response.getPayload().toString().split(";")[0],
                    response.getPayload().toString().split(";")[1],
                    response.getPayload().toString().split(";")[2],
                    response.getPayload().toString().split(";")[3],
                    response.getPayload().toString().split(";")[4],
                    response.getPayload().toString().split(";")[5],
                    response.getPayload().toString().split(";")[6]);
            sessione.setNome(user.getNome());
            sessione.setCognome(user.getCognome());
            sessione.setDataNascita(user.getDataNascita());
            sessione.setLuogo(user.getDomicilio());
            sessione.setUsername(user.getUsername());
            sessione.setPassword(user.getPassword());
            if (user.getRuolo().equals("ristoratore")) {
                sessione.setRuolo(Ruolo.RISTORATORE);
            } else {
                sessione.setRuolo(Ruolo.CLIENTE);
            }
            sessione.stampaDettagli();

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));
                Parent root = loader.load();

                HomeController controller = loader.getController();
                controller.setConnectionSocket(socket, in, out);
                controller.setPreviousRoot(((Node) e.getSource()).getScene().getRoot());

                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                stage.getScene().setRoot(root);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login non riuscito");
            alert.setHeaderText("Credenziali errate");
            alert.showAndWait();
        }
    }

    public void setConnectionSocket(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        this.socket = socket;
        this.in = in;
        this.out = out;
    }

    public void setPreviousRoot(Parent previousRoot) {
        this.previousRoot = previousRoot;
    }
}
