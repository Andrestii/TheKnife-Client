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
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

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

    private Stage stage;
    private Scene scene;
    private Parent root;

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

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
        //App.setRoot("ristoranti"); // Eliminare riga

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ristoranti.fxml"));
        root = loader.load();

        RistorantiController controller = loader.getController();
        controller.setConnectionSocket(socket, in, out);

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
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
        //App.setRoot("ristoranti"); // Eliminare riga
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ristoranti.fxml"));
        root = loader.load();

        RistorantiController controller = loader.getController();
        controller.setConnectionSocket(socket, in, out);

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
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

    public void setConnectionSocket(Socket socket, ObjectInputStream in, ObjectOutputStream out){
        this.socket = socket;
        this.in = in;
        this.out = out;
    }
}
