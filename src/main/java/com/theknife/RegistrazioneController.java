package com.theknife;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegistrazioneController {

    @FXML private TextField nomeField;
    @FXML private TextField cognomeField;
    @FXML private TextField dataNascitaField;
    @FXML private TextField domicilioField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confermaPasswordField;
    @FXML private RadioButton clienteRadio;
    @FXML private RadioButton ristoratoreRadio;

    // Label di errore sotto ogni campo
    @FXML private Label nomeError;
    @FXML private Label cognomeError;
    @FXML private Label dataError;
    @FXML private Label domicilioError;
    @FXML private Label usernameError;
    @FXML private Label passwordError;
    @FXML private Label confermaError;
    @FXML private Label ruoloError;

    private ToggleGroup ruoloGroup;

    @FXML
    public void initialize() {
        ruoloGroup = new ToggleGroup();
        if (clienteRadio != null && ristoratoreRadio != null) {
            clienteRadio.setToggleGroup(ruoloGroup);
            ristoratoreRadio.setToggleGroup(ruoloGroup);
        }
        resetErrorLabels();
    }

    @FXML
    private void onBackClicked() throws IOException {
        App.setRoot("welcome");
    }

    @FXML
    private void onSubmitClicked() throws IOException {
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
            mostraErroreCampo(dataNascitaField, dataError, "Inserisci una data valida (gg/mm/aaaa)");
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
        String conferma = confermaPasswordField.getText();

        if (!validaPassword(password)) {
            mostraErroreCampo(passwordField, passwordError, "Password troppo debole");
            valido = false;
        } else if (!password.equals(conferma)) {
            mostraErroreCampo(confermaPasswordField, confermaError, "Le password non coincidono");
            valido = false;
        }

        // Ruolo
        if (ruoloGroup.getSelectedToggle() == null) {
            ruoloError.setText("Seleziona un ruolo");
            valido = false;
        }

        // Se tutto valido
        if (valido) {
            SessioneUtente sessione = SessioneUtente.getInstance();
            sessione.reset();
            sessione.setNome(nomeField.getText());
            sessione.setCognome(cognomeField.getText());
            sessione.setDataNascita(dataNascitaField.getText());
            sessione.setLuogo(domicilioField.getText());
            sessione.setUsername(usernameField.getText());
            sessione.setPassword(passwordField.getText());
            sessione.setRuolo(clienteRadio.isSelected() ? Ruolo.CLIENTE : Ruolo.RISTORATORE);
            SessioneUtente.getInstance().stampaDettagli();

            App.setRoot("home");
        }
    }

    // ---------------------
    // Metodi di supporto
    // ---------------------

    private void resetErrorLabels() {
        Label[] labels = { nomeError, cognomeError, dataError, domicilioError, usernameError, passwordError, confermaError, ruoloError };
        for (Label l : labels) {
            if (l != null) l.setText("");
        }
    }

    private void resetFieldStyles() {
        TextField[] campi = { nomeField, cognomeField, dataNascitaField, domicilioField, usernameField, passwordField, confermaPasswordField };
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
        if (data == null || data.isEmpty()) return false;
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
}
