package com.theknife;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImpostazioniController {

    @FXML private TextField nomeField;
    @FXML private TextField cognomeField;
    @FXML private TextField dataNascitaField;
    @FXML private TextField domicilioField;
    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private ImageView userIcon;

    // Label di errore sotto ogni campo
    @FXML private Label nomeError;
    @FXML private Label cognomeError;
    @FXML private Label dataError;
    @FXML private Label domicilioError;
    @FXML private Label usernameError;
    @FXML private Label passwordError;

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
        dataNascitaField.setText(sessione.getDataNascita());
        domicilioField.setText(sessione.getLuogo());
        usernameField.setText(sessione.getUsername());
        passwordField.setText(sessione.getPassword());

        resetErrorLabels();
        resetFieldStyles();
    }

    @FXML
    private void onConfermaClicked() throws Exception {
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
            mostraErroreCampo(dataNascitaField, dataError, "Data non valida (gg/mm/aaaa)");
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

        if (!validaPassword(password)) {
            mostraErroreCampo(passwordField, passwordError, "Password troppo debole");
            valido = false;
        }

        // Se tutto valido → aggiorni sessione
        if (valido) {
            SessioneUtente sessione = SessioneUtente.getInstance();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Conferma modifiche");
            alert.setHeaderText("Confermi le modifiche ai tuoi dati?");
            alert.setContentText("Le informazioni verranno aggiornate.");

            if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                sessione.setNome(nomeField.getText());
                sessione.setCognome(cognomeField.getText());
                String dataInput = dataNascitaField.getText();
                sessione.setDataNascita((dataInput == null || dataInput.isBlank()) ? null : dataInput);            sessione.setLuogo(domicilioField.getText());
                sessione.setUsername(usernameField.getText());
                sessione.setPassword(passwordField.getText());

                Alert ok = new Alert(Alert.AlertType.INFORMATION);
                ok.setTitle("Successo");
                ok.setHeaderText("Dati aggiornati con successo!");
                ok.show();

                SessioneUtente.getInstance().stampaDettagli();
            }
        }
    }

    @FXML
    private void onIndietroClicked() throws Exception {

        SessioneUtente sessione = SessioneUtente.getInstance();

        switch (sessione.getRuolo()) {
            case CLIENTE:
                App.setRoot("menuCliente");
                break;

            case RISTORATORE:
                App.setRoot("menuRistoratore");
                break;

            default:
                App.setRoot("welcome");
                break;
        }
    }

    // ---------------------
    // Metodi di supporto
    // ---------------------

    private void resetErrorLabels() {
        Label[] labels = { nomeError, cognomeError, dataError, domicilioError, usernameError, passwordError};
        for (Label l : labels) {
            if (l != null) l.setText("");
        }
    }

    private void resetFieldStyles() {
        TextField[] campi = { nomeField, cognomeField, dataNascitaField, domicilioField, usernameField, passwordField};
        for (TextField f : campi) {
            if (f != null) f.setStyle(null);
        }
    }

    private void mostraErroreCampo(TextField campo, Label erroreLabel, String messaggio) {
        campo.setStyle("-fx-border-color: red; -fx-border-width: 2;");
        if (erroreLabel != null) erroreLabel.setText(messaggio);
    }

    private boolean validaNome(String testo) {
        return testo != null && testo.matches("[a-zA-ZàèéìòùÀÈÉÌÒÙ'\\s]{2,}");
    }

    private boolean validaUsername(String testo) {
        return testo != null && testo.matches("[a-zA-Z0-9_]{4,}");
    }

    private boolean validaPassword(String testo) {
        if (testo == null || testo.length() < 8) return false;
        Pattern pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\\$%\\^&\\*]).+$");
        return pattern.matcher(testo).matches();
    }

    private boolean validaData(String data) {
        if (data == null || data.isEmpty()) return true;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate parsed = LocalDate.parse(data, formatter);
            return !parsed.isAfter(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private boolean validaDomicilio(String domicilio) {
        return domicilio != null && domicilio.trim().length() >= 3 && domicilio.matches(".*[a-zA-Z].*");
    }

    public void setConnectionSocket(Socket socket, ObjectInputStream in, ObjectOutputStream out){
        this.socket = socket;
        this.in = in;
        this.out = out;
    }
}
