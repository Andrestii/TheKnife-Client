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
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class CreaRistoranteController {

    // --- CAMPI TESTO ---
    @FXML private TextField nomeField;
    @FXML private TextField nazioneField;      
    @FXML private TextField cittaField;
    @FXML private TextField indirizzoField;
    @FXML private TextField latitudineField;   
    @FXML private TextField longitudineField;
    @FXML private TextField prezzoField;  

    // --- LABEL ERRORI ---
    @FXML private Label nomeError;
    @FXML private Label nazioneError;          
    @FXML private Label cittaError;
    @FXML private Label indirizzoError;
    @FXML private Label latitudineError;       
    @FXML private Label longitudineError;      
    @FXML private Label cucinaError;
    @FXML private Label deliveryError;
    @FXML private Label prenotazioneError;
    @FXML private Label prezzoError;

    // --- TIPOLOGIA CUCINA ---
    @FXML private RadioButton italianaCheck;
    @FXML private RadioButton hamburgerCheck;
    @FXML private RadioButton asiaticaCheck;
    @FXML private RadioButton sudamericanaCheck;

    // --- RADIOBUTTON ---
    @FXML private RadioButton deliverySi;
    @FXML private RadioButton deliveryNo;
    @FXML private RadioButton prenotazioneSi;
    @FXML private RadioButton prenotazioneNo;

    private ToggleGroup tipoCucinaGroup;
    private ToggleGroup deliveryGroup;
    private ToggleGroup prenGroup;

    // --- NAV / CONNESSIONE ---
    private Stage stage;
    private Parent root;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Parent previousRoot;

    @FXML
    public void initialize() {
        tipoCucinaGroup = new ToggleGroup();
        if (italianaCheck != null) italianaCheck.setToggleGroup(tipoCucinaGroup);
        if (hamburgerCheck != null) hamburgerCheck.setToggleGroup(tipoCucinaGroup);
        if (asiaticaCheck != null) asiaticaCheck.setToggleGroup(tipoCucinaGroup);
        if (sudamericanaCheck != null) sudamericanaCheck.setToggleGroup(tipoCucinaGroup);

        deliveryGroup = new ToggleGroup();
        if (deliverySi != null) deliverySi.setToggleGroup(deliveryGroup);
        if (deliveryNo != null) deliveryNo.setToggleGroup(deliveryGroup);

        prenGroup = new ToggleGroup();
        if (prenotazioneSi != null) prenotazioneSi.setToggleGroup(prenGroup);
        if (prenotazioneNo != null) prenotazioneNo.setToggleGroup(prenGroup);

        clearErrors();
    }

    @FXML
    private void onBackClicked(ActionEvent event) throws IOException {
        try {
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            if (previousRoot != null) stage.getScene().setRoot(previousRoot);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void onCreaClicked(ActionEvent event) throws IOException {
        clearErrors();
        boolean valid = true;

        String nome = nomeField.getText().trim();
        String nazione = nazioneField.getText().trim();          
        String citta = cittaField.getText().trim();
        String indirizzo = indirizzoField.getText().trim();
        String latStr = latitudineField.getText().trim();       
        String lonStr = longitudineField.getText().trim();     
        String prezzo = prezzoField.getText().trim();  

        boolean cucinaSelezionata =
                italianaCheck.isSelected() ||
                hamburgerCheck.isSelected() ||
                asiaticaCheck.isSelected() ||
                sudamericanaCheck.isSelected();

        RadioButton deliverySelected = (RadioButton) deliveryGroup.getSelectedToggle();
        RadioButton prenSelected = (RadioButton) prenGroup.getSelectedToggle();

        // --- VALIDAZIONI BASE ---

        if (nome.isEmpty()) {
            setError(nomeField, nomeError, "Inserisci un nome valido");
            valid = false;
        }

        if (nazione.isEmpty() || nazione.length() < 2) {
            setError(nazioneField, nazioneError, "Inserisci una nazione valida");
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

        // --- VALIDAZIONI LAT/LON (numeriche + range) ---
        Double lat = null;
        Double lon = null;

        lat = parseDoubleOrNull(latStr);
        if (lat == null || lat < -90 || lat > 90) {
            setError(latitudineField, latitudineError, "Latitudine non valida (-90 a 90)");
            valid = false;
        }

        lon = parseDoubleOrNull(lonStr);
        if (lon == null || lon < -180 || lon > 180) {
            setError(longitudineField, longitudineError, "Longitudine non valida (-180 a 180)");
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

        if (prezzo.isEmpty() || !prezzo.matches("\\d+(\\.\\d{1,2})?")) {
            setError(prezzoField, prezzoError, "Inserisci un prezzo valido (es. 15 o 15.50)");
            valid = false;
        }

        if (!valid) return;

        // Se i dati sono validi
        boolean delivery = deliverySelected == deliverySi;
        boolean pren = prenSelected == prenotazioneSi;
        String tipoCucina = buildTipologiaCucina();
        SessioneUtente sessione = SessioneUtente.getInstance();

        // Inserisco dati nel db
        out.writeObject("addRestaurant");
        out.writeObject(nome);
        out.writeObject(nazione);
        out.writeObject(citta);
        out.writeObject(indirizzo);
        out.writeObject(lat);
        out.writeObject(lon);
        out.writeObject(delivery);
        out.writeObject(pren);
        out.writeObject(tipoCucina);
        out.writeObject(sessione.getUsername());
        out.writeObject(Integer.parseInt(prezzo));
        out.flush();

        // Controllo se la registrazione è andata a buon fine e cambio schermata
        ServerResponse response;
        try {
            response = (ServerResponse) in.readObject();
        } catch (ClassNotFoundException ex) {
            throw new IOException("Risposta del server non valida", ex);
        }  
            
        if (response.status.equals("OK")) {

        System.out.println("Ristorante creato correttamente!");
        System.out.println("Nome: " + nome);
        System.out.println("Nazione: " + nazione);
        System.out.println("Città: " + citta);
        System.out.println("Indirizzo: " + indirizzo);
        System.out.println("Latitudine: " + lat);
        System.out.println("Longitudine: " + lon);
        System.out.println("Delivery: " + delivery);
        System.out.println("Prenotazioni: " + pren);
        System.out.println("Tipi di cucina: " + tipoCucina);
        System.out.println("Prezzo: " + prezzo);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Creazione Ristorante");
        alert.setHeaderText("Ristorante creato con successo!");
        alert.showAndWait();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ristoranti.fxml"));
        root = loader.load();

        RistorantiController controller = loader.getController();
        controller.setConnectionSocket(socket, in, out);
        controller.setPreviousRoot(((Node)event.getSource()).getScene().getRoot());

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore di creazione del ristorante");
            alert.setHeaderText("Creazione del ristorante non riuscita");
            alert.setContentText("Riprova più tardi...");
            alert.showAndWait();

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ristoranti.fxml"));
                Parent root = loader.load();

                RistorantiController controller = loader.getController();
                controller.setConnectionSocket(socket, in, out);
                controller.setPreviousRoot(((Node)event.getSource()).getScene().getRoot());

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.getScene().setRoot(root);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // --- FUNZIONI DI SUPPORTO ---

    private void clearErrors() {
        nomeError.setText("");
        nazioneError.setText("");
        cittaError.setText("");
        indirizzoError.setText("");
        latitudineError.setText("");
        longitudineError.setText("");
        cucinaError.setText("");
        deliveryError.setText("");
        prenotazioneError.setText("");
        prezzoError.setText("");

        resetBorder(nomeField);
        resetBorder(nazioneField);
        resetBorder(cittaField);
        resetBorder(indirizzoField);
        resetBorder(latitudineField);
        resetBorder(longitudineField);
        resetBorder(prezzoField);
    }

    private void setError(TextField field, Label label, String message) {
        if (label != null) label.setText(message);
        if (field != null) field.setStyle("-fx-border-color: red; -fx-border-width: 2;");
    }

    private void resetBorder(TextField field) {
        if (field != null) field.setStyle("");
    }

    private Double parseDoubleOrNull(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            String normalized = s.replace(",", ".");
            return Double.parseDouble(normalized);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String buildTipologiaCucina() {
        Toggle t = tipoCucinaGroup.getSelectedToggle();
        if (t == italianaCheck) return "italiana";
        if (t == hamburgerCheck) return "hamburger";
        if (t == asiaticaCheck) return "asiatica";
        if (t == sudamericanaCheck) return "sudamericana";
        return "";
    }

    public void setConnectionSocket(Socket socket, ObjectInputStream in, ObjectOutputStream out){
        this.socket = socket;
        this.in = in;
        this.out = out;
    }
    
    public void setPreviousRoot(Parent previousRoot) {
        this.previousRoot = previousRoot;
    }
}
