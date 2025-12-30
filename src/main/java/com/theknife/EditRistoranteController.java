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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class EditRistoranteController {

    // --- CAMPI ---
    @FXML private TextField nomeField;
    @FXML private TextField nazioneField;
    @FXML private TextField cittaField;
    @FXML private TextField indirizzoField;
    @FXML private TextField latitudineField;
    @FXML private TextField longitudineField;
    @FXML private TextField prezzoField;

    // --- ERRORI ---
    @FXML private Label infoLabel;
    @FXML private Label nomeError;
    @FXML private Label nazioneError;
    @FXML private Label cittaError;
    @FXML private Label indirizzoError;
    @FXML private Label latitudineError;
    @FXML private Label longitudineError;
    @FXML private Label prezzoError;
    @FXML private Label cucinaError;
    @FXML private Label prenotazioneError;
    @FXML private Label deliveryError;

    // --- CHECKBOX CUCINA ---
    @FXML private CheckBox italianaCheck;
    @FXML private CheckBox hamburgerCheck;
    @FXML private CheckBox asiaticaCheck;
    @FXML private CheckBox sudamericanaCheck;

    // --- RADIO PRENOTAZIONI / DELIVERY ---
    @FXML private RadioButton prenotazioneSi;
    @FXML private RadioButton prenotazioneNo;
    @FXML private RadioButton deliverySi;
    @FXML private RadioButton deliveryNo;

    private ToggleGroup prenGroup;
    private ToggleGroup deliveryGroup;

    // --- NAV / CONNESSIONE ---
    private Stage stage;
    private Parent root;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    @FXML
    public void initialize() {
        // ToggleGroup Prenotazioni
        prenGroup = new ToggleGroup();
        if (prenotazioneSi != null) prenotazioneSi.setToggleGroup(prenGroup);
        if (prenotazioneNo != null) prenotazioneNo.setToggleGroup(prenGroup);

        // ToggleGroup Delivery
        deliveryGroup = new ToggleGroup();
        if (deliverySi != null) deliverySi.setToggleGroup(deliveryGroup);
        if (deliveryNo != null) deliveryNo.setToggleGroup(deliveryGroup);

        clearErrors();
        if (infoLabel != null) infoLabel.setText(""); // opzionale
    }

    @FXML
    private void onBackClicked(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ristoranti.fxml"));
        root = loader.load();

        RistorantiController controller = loader.getController();
        controller.setConnectionSocket(socket, in, out);

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    @FXML
    private void onSalvaClicked(ActionEvent event) {
        // Basico: niente server ancora
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Modifica Ristorante");
        alert.setHeaderText("Salvataggio non ancora implementato");
        alert.setContentText("Qui poi invieremo i dati al server (updateRestaurant).");
        alert.showAndWait();
    }

    // --- SUPPORTO ---
    private void clearErrors() {
        if (nomeError != null) nomeError.setText("");
        if (nazioneError != null) nazioneError.setText("");
        if (cittaError != null) cittaError.setText("");
        if (indirizzoError != null) indirizzoError.setText("");
        if (latitudineError != null) latitudineError.setText("");
        if (longitudineError != null) longitudineError.setText("");
        if (prezzoError != null) prezzoError.setText("");
        if (cucinaError != null) cucinaError.setText("");
        if (prenotazioneError != null) prenotazioneError.setText("");
        if (deliveryError != null) deliveryError.setText("");

        resetBorder(nomeField);
        resetBorder(nazioneField);
        resetBorder(cittaField);
        resetBorder(indirizzoField);
        resetBorder(latitudineField);
        resetBorder(longitudineField);
        resetBorder(prezzoField);
    }

    private void resetBorder(TextField field) {
        if (field != null) field.setStyle("");
    }

    public void setConnectionSocket(Socket socket, ObjectInputStream in, ObjectOutputStream out){
        this.socket = socket;
        this.in = in;
        this.out = out;
    }
}
