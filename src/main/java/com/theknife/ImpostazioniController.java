/**
 * Autori del progetto:
 *
 * - Lorenzo De Paoli
 *   Matricola: 753577
 *   Sede: VA
 *
 * - Andrea Onesti
 *   Matricola: 754771
 *   Sede: VA
 *
 * - Weili Wu
 *   Matricola: 752602
 *   Sede: VA
 */
package com.theknife;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * Controller della schermata Impostazioni.
 * Permette all'utente di visualizzare e modificare i propri dati personali,
 * validare i campi e inviare al server la richiesta di aggiornamento.
 */
public class ImpostazioniController {

    @FXML
    private TextField nomeField;
    @FXML
    private TextField cognomeField;
    @FXML
    private TextField dataNascitaField;
    @FXML
    private TextField domicilioField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private ImageView userIcon;

    @FXML
    private Label nomeError;
    @FXML
    private Label cognomeError;
    @FXML
    private Label dataError;
    @FXML
    private Label domicilioError;
    @FXML
    private Label usernameError;
    @FXML
    private Label passwordError;

    private Stage stage;
    private Parent root;
    private Parent previousRoot;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    @FXML
    public void initialize() {

        SessioneUtente sessione = SessioneUtente.getInstance();

        // Imposta immagine utente in base al ruolo
        if (sessione.getRuolo() == Ruolo.CLIENTE) {
            userIcon.setImage(new Image(getClass().getResourceAsStream("/com/theknife/clientIcon.png")));
        } else if (sessione.getRuolo() == Ruolo.RISTORATORE) {
            userIcon.setImage(new Image(getClass().getResourceAsStream("/com/theknife/chefIcon.png")));
        }

        nomeField.setText(sessione.getNome());
        cognomeField.setText(sessione.getCognome());
        if (sessione.getDataNascita() == null || sessione.getDataNascita().isEmpty()
                || sessione.getDataNascita().equals("null"))
            dataNascitaField.setText("");
        else
            dataNascitaField.setText(sessione.getDataNascita());
        domicilioField.setText(sessione.getLuogo());
        usernameField.setText(sessione.getUsername());
        passwordField.clear();

        resetErrorLabels();
        resetFieldStyles();
    }

    @FXML
    private void onConfermaClicked(ActionEvent e) throws Exception {
        resetErrorLabels();
        resetFieldStyles();

        boolean valido = true;

        // Nome
        if (!validaNome(nomeField.getText())) {
            mostraErroreCampo(nomeField, nomeError, "Inserisci un nome valido");
            valido = false;
        }

        // Cognome
        if (!validaNome(cognomeField.getText())) {
            mostraErroreCampo(cognomeField, cognomeError, "Inserisci un cognome valido");
            valido = false;
        }

        // Data di nascita
        if (!validaData(dataNascitaField.getText())) {
            mostraErroreCampo(dataNascitaField, dataError, "Data non valida (YYYY-MM-DD)");
            valido = false;
        }

        // Domicilio
        if (!validaDomicilio(domicilioField.getText())) {
            mostraErroreCampo(domicilioField, domicilioError, "Inserisci un domicilio valido");
            valido = false;
        }

        // Username
        if (!validaUsername(usernameField.getText())) {
            mostraErroreCampo(usernameField, usernameError, "Username non valido (min 4 caratteri)");
            valido = false;
        }

        // Password
        String password = passwordField.getText();
        boolean wantsChangePassword = password != null && !password.isBlank();
        if (wantsChangePassword && !validaPassword(password)) {
            mostraErroreCampo(passwordField, passwordError, "Password troppo debole");
            valido = false;
        }

        // Se tutto valido aggiorno la sessione
        if (valido) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Conferma modifiche");
            alert.setHeaderText("Confermi le modifiche ai tuoi dati?");
            alert.setContentText("Le informazioni verranno aggiornate.");

            SessioneUtente sessione = SessioneUtente.getInstance();
            if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                out.writeObject("updateUserInfo");
                out.writeObject(sessione.getUsername()); // username attuale (chiave)
                out.writeObject(nomeField.getText());
                out.writeObject(cognomeField.getText());
                out.writeObject(dataNascitaField.getText());
                out.writeObject(domicilioField.getText());
                out.writeObject(usernameField.getText()); // eventuale nuovo username
                out.writeObject(passwordField.getText());
                out.flush();

                // Controllo se l'operazione è andata a buon fine e cambio schermata
                ServerResponse response;
                try {
                    response = (ServerResponse) in.readObject();
                } catch (ClassNotFoundException ex) {
                    throw new IOException("Risposta del server non valida", ex);
                }

                if (response.status.equals("OK")) {
                    Alert ok = new Alert(Alert.AlertType.INFORMATION);
                    ok.setTitle("Successo");
                    ok.setHeaderText("Dati aggiornati con successo!");
                    ok.setContentText("Ritorno alla home...");
                    ok.showAndWait();
                    // Aggiorno i dati nella sessione
                    sessione.setNome(nomeField.getText());
                    sessione.setCognome(cognomeField.getText());
                    sessione.setDataNascita(dataNascitaField.getText());
                    sessione.setLuogo(domicilioField.getText());
                    sessione.setUsername(usernameField.getText());
                    sessione.stampaDettagli();

                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("menuCliente.fxml"));
                        Parent root = loader.load();

                        MenuClienteController controller = loader.getController();
                        controller.setConnectionSocket(socket, in, out);
                        controller.setPreviousRoot(((Node) e.getSource()).getScene().getRoot());

                        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                        stage.getScene().setRoot(root);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Errore");
                    error.setHeaderText("Impossibile aggiornare i dati");
                    error.setContentText("Qualcosa è andato storto. Riprova più tardi.");
                    error.show();
                }
            }
        }
    }

    @FXML
    private void onIndietroClicked(ActionEvent e) throws Exception {

        /*
         * SessioneUtente sessione = SessioneUtente.getInstance();
         * 
         * FXMLLoader loader;
         * 
         * 
         * switch (sessione.getRuolo()) {
         * case CLIENTE:
         * //App.setRoot("menuCliente");
         * 
         * loader = new FXMLLoader(getClass().getResource("menuCliente.fxml"));
         * root = loader.load();
         * 
         * MenuClienteController controllerCliente = loader.getController();
         * controllerCliente.setConnectionSocket(socket, in, out);
         * 
         * stage = (Stage)((Node)e.getSource()).getScene().getWindow();
         * stage.getScene().setRoot(root);
         * break;
         * 
         * case RISTORATORE:
         * //App.setRoot("menuRistoratore");
         * 
         * loader = new FXMLLoader(getClass().getResource("menuRistoratore.fxml"));
         * root = loader.load();
         * 
         * MenuRistoratoreController controllerRistoratore = loader.getController();
         * controllerRistoratore.setConnectionSocket(socket, in, out);
         * 
         * stage = (Stage)((Node)e.getSource()).getScene().getWindow();
         * stage.getScene().setRoot(root);
         * break;
         * 
         * default:
         * //App.setRoot("welcome");
         * 
         * loader = new FXMLLoader(getClass().getResource("welcome.fxml"));
         * root = loader.load();
         * 
         * WelcomeController controller = loader.getController();
         * controller.setConnectionSocket(socket, in, out);
         * 
         * stage = (Stage)((Node)e.getSource()).getScene().getWindow();
         * stage.getScene().setRoot(root);
         * break;
         * }
         */

        try {
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            if (previousRoot != null)
                stage.getScene().setRoot(previousRoot);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ---------------------
    // Metodi di supporto
    // ---------------------

    private void resetErrorLabels() {
        Label[] labels = { nomeError, cognomeError, dataError, domicilioError, usernameError, passwordError };
        for (Label l : labels) {
            if (l != null)
                l.setText("");
        }
    }

    private void resetFieldStyles() {
        TextField[] campi = { nomeField, cognomeField, dataNascitaField, domicilioField, usernameField, passwordField };
        for (TextField f : campi) {
            if (f != null)
                f.setStyle(null);
        }
    }

    private void mostraErroreCampo(TextField campo, Label erroreLabel, String messaggio) {
        campo.setStyle("-fx-border-color: red; -fx-border-width: 2;");
        if (erroreLabel != null)
            erroreLabel.setText(messaggio);
    }

    private boolean validaNome(String testo) {
        return testo != null && testo.matches("[a-zA-ZàèéìòùÀÈÉÌÒÙ'\\s]{2,}");
    }

    private boolean validaUsername(String testo) {
        return testo != null && testo.matches("[a-zA-Z0-9_]{4,}");
    }

    private boolean validaPassword(String testo) {
        if (testo == null || testo.length() < 8)
            return false;
        Pattern pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\\$%\\^&\\*]).+$");
        return pattern.matcher(testo).matches();
    }

    private boolean validaData(String data) {
        if (data == null || data.isEmpty())
            return true;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate parsed = LocalDate.parse(data, formatter);
            return !parsed.isAfter(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private boolean validaDomicilio(String domicilio) {
        return domicilio != null && domicilio.trim().length() >= 3 && domicilio.matches(".*[a-zA-Z].*");
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
