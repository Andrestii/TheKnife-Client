package com.theknife;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

import java.io.IOException;

public class CreaRistoranteController {

    @FXML private TextField nomeField;
    @FXML private TextField cittaField;
    @FXML private TextField indirizzoField;
    @FXML private TextField telefonoField;

    @FXML private Label nomeError;
    @FXML private Label cittaError;
    @FXML private Label indirizzoError;
    @FXML private Label telefonoError;
    @FXML private Label cucinaError;
    @FXML private Label deliveryError;
    @FXML private Label prenotazioneError;

    @FXML private CheckBox italianaCheck;
    @FXML private CheckBox hamburgerCheck;
    @FXML private CheckBox asiaticaCheck;
    @FXML private CheckBox sudamericanaCheck;

    @FXML private RadioButton deliverySi;
    @FXML private RadioButton deliveryNo;
    @FXML private RadioButton prenotazioneSi;
    @FXML private RadioButton prenotazioneNo;

    private ToggleGroup deliveryGroup;
    private ToggleGroup prenGroup;

    @FXML
    public void initialize() {
        deliveryGroup = new ToggleGroup();
        deliverySi.setToggleGroup(deliveryGroup);
        deliveryNo.setToggleGroup(deliveryGroup);

        prenGroup = new ToggleGroup();
        prenotazioneSi.setToggleGroup(prenGroup);
        prenotazioneNo.setToggleGroup(prenGroup);

        clearErrors();
    }

    @FXML
    private void onBackClicked(ActionEvent event) throws IOException {
        App.setRoot("ristoranti");
    }

    @FXML
    private void onCreaClicked(ActionEvent event) throws IOException {

        clearErrors(); // reset iniziale
        boolean valid = true;

        String nome = nomeField.getText().trim();
        String citta = cittaField.getText().trim();
        String indirizzo = indirizzoField.getText().trim();
        String telefono = telefonoField.getText().trim();

        boolean cucinaSelezionata =
                italianaCheck.isSelected() ||
                hamburgerCheck.isSelected() ||
                asiaticaCheck.isSelected() ||
                sudamericanaCheck.isSelected();

        RadioButton deliverySelected = (RadioButton) deliveryGroup.getSelectedToggle();
        RadioButton prenSelected = (RadioButton) prenGroup.getSelectedToggle();

        // --- VALIDAZIONI ---

        if (nome.isEmpty()) {
            setError(nomeField, nomeError, "Inserisci un nome valido");
            valid = false;
        }

        if (citta.isEmpty()) {
            setError(cittaField, cittaError, "Inserisci una città valida");
            valid = false;
        }

        if (indirizzo.isEmpty()) {
            setError(indirizzoField, indirizzoError, "Inserisci un indirizzo valido");
            valid = false;
        }

        if (telefono.isEmpty() || !telefono.matches("\\d{6,15}")) {
            setError(telefonoField, telefonoError, "Inserisci un telefono valido (6-15 numeri)");
            valid = false;
        }

        if (!cucinaSelezionata) {
            cucinaError.setText("Seleziona almeno una tipologia di cucina");
            valid = false;
        }

        if (deliverySelected == null) {
            deliveryError.setText("Seleziona una opzione");
            valid = false;
        }

        if (prenSelected == null) {
            prenotazioneError.setText("Seleziona una opzione");
            valid = false;
        }

        // Se anche un solo campo non è valido → STOP
        if (!valid) return;

        // --------------------
        // QUI I DATI SONO VALIDI
        // --------------------

        boolean delivery = deliverySelected == deliverySi;
        boolean pren = prenSelected == prenotazioneSi;

        System.out.println("Ristorante creato correttamente!");
        System.out.println("Nome: " + nome);
        System.out.println("Città: " + citta);
        System.out.println("Indirizzo: " + indirizzo);
        System.out.println("Telefono: " + telefono);
        System.out.println("Delivery: " + delivery);
        System.out.println("Prenotazioni: " + pren);

        // Mostra popup di successo
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Creazione Ristorante");
        alert.setHeaderText("Ristorante creato con successo!");
        alert.showAndWait();

        // Torna alla schermata ristoranti.fxml
        App.setRoot("ristoranti");
    }

    // --- FUNZIONI DI SUPPORTO ---

    private void clearErrors() {
        nomeError.setText("");
        cittaError.setText("");
        indirizzoError.setText("");
        telefonoError.setText("");
        cucinaError.setText("");
        deliveryError.setText("");
        prenotazioneError.setText("");

        resetBorder(nomeField);
        resetBorder(cittaField);
        resetBorder(indirizzoField);
        resetBorder(telefonoField);
    }

    private void setError(TextField field, Label label, String message) {
        label.setText(message);
        field.setStyle("-fx-border-color: red; -fx-border-width: 2;");
    }

    private void resetBorder(TextField field) {
        field.setStyle(""); // rimuove eventuali bordi precedenti
    }
}
