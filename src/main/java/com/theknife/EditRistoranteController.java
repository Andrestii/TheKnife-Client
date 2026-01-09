package com.theknife;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javafx.application.Platform;
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
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class EditRistoranteController {
    @FXML
    private TextField nomeField;
    @FXML
    private TextField nazioneField;
    @FXML
    private TextField cittaField;
    @FXML
    private TextField indirizzoField;
    @FXML
    private TextField latitudineField;
    @FXML
    private TextField longitudineField;
    @FXML
    private TextField prezzoField;

    @FXML
    private Label infoLabel;
    @FXML
    private Label nomeError;
    @FXML
    private Label nazioneError;
    @FXML
    private Label cittaError;
    @FXML
    private Label indirizzoError;
    @FXML
    private Label latitudineError;
    @FXML
    private Label longitudineError;
    @FXML
    private Label prezzoError;
    @FXML
    private Label cucinaError;
    @FXML
    private Label prenotazioneError;
    @FXML
    private Label deliveryError;

    @FXML
    private RadioButton italianaCheck;
    @FXML
    private RadioButton hamburgerCheck;
    @FXML
    private RadioButton asiaticaCheck;
    @FXML
    private RadioButton sudamericanaCheck;

    @FXML
    private RadioButton prenotazioneSi;
    @FXML
    private RadioButton prenotazioneNo;
    @FXML
    private RadioButton deliverySi;
    @FXML
    private RadioButton deliveryNo;

    private ToggleGroup tipoCucinaGroup;
    private ToggleGroup prenGroup;
    private ToggleGroup deliveryGroup;
    private Stage stage;
    private Parent root;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Ristorante ristorante;
    private Parent previousRoot;

    @FXML
    public void initialize() {
        tipoCucinaGroup = new ToggleGroup();
        if (italianaCheck != null) italianaCheck.setToggleGroup(tipoCucinaGroup);
        if (hamburgerCheck != null) hamburgerCheck.setToggleGroup(tipoCucinaGroup);
        if (asiaticaCheck != null) asiaticaCheck.setToggleGroup(tipoCucinaGroup);
        if (sudamericanaCheck != null) sudamericanaCheck.setToggleGroup(tipoCucinaGroup);

        prenGroup = new ToggleGroup();
        if (prenotazioneSi != null)
            prenotazioneSi.setToggleGroup(prenGroup);
        if (prenotazioneNo != null)
            prenotazioneNo.setToggleGroup(prenGroup);

        deliveryGroup = new ToggleGroup();
        if (deliverySi != null)
            deliverySi.setToggleGroup(deliveryGroup);
        if (deliveryNo != null)
            deliveryNo.setToggleGroup(deliveryGroup);

        clearErrors();
        populateFieldsIfReady();
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
    private void onCancellaClicked(ActionEvent event) {
        // Senza ristorante selezionato non posso cancellare
        if (ristorante == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Ristorante non selezionato");
            alert.setContentText("Torna indietro e seleziona un ristorante.");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Conferma cancellazione");
        confirm.setHeaderText("Vuoi davvero cancellare questo ristorante?");
        confirm.setContentText("Questa operazione è definitiva.");
        var result = confirm.showAndWait();

        if (result.isEmpty() || result.get() != javafx.scene.control.ButtonType.OK) {
            return;
        }

        try {
            out.writeObject("deleteRestaurant");
            out.writeObject(ristorante.getId());
            out.flush();

            ServerResponse response;
            try {
                response = (ServerResponse) in.readObject();
            } catch (ClassNotFoundException ex) {
                throw new IOException("Risposta del server non valida", ex);
            }

            if ("OK".equals(response.status)) {
                Alert ok = new Alert(Alert.AlertType.INFORMATION);
                ok.setTitle("Cancellazione ristorante");
                ok.setHeaderText("Ristorante cancellato con successo!");
                ok.showAndWait();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("ristoranti.fxml"));
                Parent root = loader.load();

                RistorantiController controller = loader.getController();
                controller.setConnectionSocket(socket, in, out);
                controller.setPreviousRoot(((Node)event.getSource()).getScene().getRoot());

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.getScene().setRoot(root);

            } else {
                Alert err = new Alert(Alert.AlertType.ERROR);
                err.setTitle("Errore cancellazione");
                err.setHeaderText("Non è stato possibile cancellare il ristorante");
                err.setContentText(response.getPayload() != null ? response.getPayload().toString() : "Riprova più tardi");
                err.showAndWait();
            }

        } catch (IOException e) {
            e.printStackTrace();
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setTitle("Errore di connessione");
            err.setHeaderText("Errore durante la cancellazione");
            err.setContentText("Riprova più tardi.");
            err.showAndWait();
        }
    }

    @FXML
    private void onSalvaClicked(ActionEvent event) throws IOException {
        clearErrors();
        boolean valid = true;

        if (ristorante == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Ristorante non selezionato");
            alert.setContentText("Torna indietro e seleziona un ristorante.");
            alert.showAndWait();
            return;
        }

        String nome = nomeField.getText().trim();
        String nazione = nazioneField.getText().trim();
        String citta = cittaField.getText().trim();
        String indirizzo = indirizzoField.getText().trim();
        String latStr = latitudineField.getText().trim();
        String lonStr = longitudineField.getText().trim();
        String prezzoStr = prezzoField.getText().trim();
        boolean cucinaSelezionata = italianaCheck.isSelected() ||
                hamburgerCheck.isSelected() ||
                asiaticaCheck.isSelected() ||
                sudamericanaCheck.isSelected();

        RadioButton deliverySelected = (RadioButton) deliveryGroup.getSelectedToggle();
        RadioButton prenSelected = (RadioButton) prenGroup.getSelectedToggle();

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

        Double lat = parseDoubleOrNull(latStr);
        if (lat == null || lat < -90 || lat > 90) {
            setError(latitudineField, latitudineError, "Latitudine non valida (-90 a 90)");
            valid = false;
        }

        Double lon = parseDoubleOrNull(lonStr);
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

        if (prezzoStr.isEmpty() || !prezzoStr.matches("\\d+")) {
            setError(prezzoField, prezzoError, "Inserisci un prezzo valido (solo intero, es. 15)");
            valid = false;
        }

        if (!valid)
            return;

        boolean delivery = deliverySelected == deliverySi;
        boolean pren = prenSelected == prenotazioneSi;
        String tipoCucina = buildTipologiaCucina();
        int prezzo = Integer.parseInt(prezzoStr);

        out.writeObject("updateRestaurant");
        out.writeObject(ristorante.getId());
        out.writeObject(nome);
        out.writeObject(nazione);
        out.writeObject(citta);
        out.writeObject(indirizzo);
        out.writeObject(lat);
        out.writeObject(lon);
        out.writeObject(delivery);
        out.writeObject(pren);
        out.writeObject(tipoCucina);
        out.writeObject(prezzo);
        out.flush();

        ServerResponse response;
        try {
            response = (ServerResponse) in.readObject();
        } catch (ClassNotFoundException ex) {
            throw new IOException("Risposta del server non valida", ex);
        }

        if ("OK".equals(response.status)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Modifica dati del ristorante");
            alert.setHeaderText("Dati del ristorante modificati con successo!");
            alert.showAndWait();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("ristoranti.fxml"));
            root = loader.load();

            RistorantiController controller = loader.getController();
            controller.setConnectionSocket(socket, in, out);
            controller.setPreviousRoot(((Node)event.getSource()).getScene().getRoot());

            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore di modifica del ristorante");
            alert.setHeaderText("Modifica dei dati del ristorante non riuscita");
            alert.setContentText(
                    response.getPayload() != null ? response.getPayload().toString() : "Riprova più tardi...");
            alert.showAndWait();
        }
    }

    // --- UTIL ---

    private void setError(TextField field, Label label, String message) {
        if (label != null)
            label.setText(message);
        if (field != null)
            field.setStyle("-fx-border-color: red; -fx-border-width: 2;");
    }

    private void resetBorder(TextField field) {
        if (field != null)
            field.setStyle("");
    }

    private Double parseDoubleOrNull(String s) {
        if (s == null || s.isBlank())
            return null;
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

    private void clearErrors() {
        if (nomeError != null)
            nomeError.setText("");
        if (nazioneError != null)
            nazioneError.setText("");
        if (cittaError != null)
            cittaError.setText("");
        if (indirizzoError != null)
            indirizzoError.setText("");
        if (latitudineError != null)
            latitudineError.setText("");
        if (longitudineError != null)
            longitudineError.setText("");
        if (prezzoError != null)
            prezzoError.setText("");
        if (cucinaError != null)
            cucinaError.setText("");
        if (prenotazioneError != null)
            prenotazioneError.setText("");
        if (deliveryError != null)
            deliveryError.setText("");

        resetBorder(nomeField);
        resetBorder(nazioneField);
        resetBorder(cittaField);
        resetBorder(indirizzoField);
        resetBorder(latitudineField);
        resetBorder(longitudineField);
        resetBorder(prezzoField);
    }

    public void setRistorante(Ristorante r) {
        this.ristorante = r;
        populateFieldsIfReady();
    }

    private void populateFieldsIfReady() {
        if (nomeField == null)
            return;
        if (ristorante == null)
            return;

        Platform.runLater(() -> {
            nomeField.setText(compilaCampo(ristorante.getNome()));
            nazioneField.setText(compilaCampo(ristorante.getNazione()));
            cittaField.setText(compilaCampo(ristorante.getCitta()));
            indirizzoField.setText(compilaCampo(ristorante.getIndirizzo()));
            latitudineField.setText(Double.toString(ristorante.getLat()));
            longitudineField.setText(Double.toString(ristorante.getLon()));
            prezzoField.setText(Integer.toString((int) ristorante.getPrezzo())); // se prezzo è double cambia

            // Tipo cucina: nel tuo create è separato da ;
            String tipoCucina = ristorante.getTipoCucina();

            italianaCheck.setSelected(false);
            hamburgerCheck.setSelected(false);
            asiaticaCheck.setSelected(false);
            sudamericanaCheck.setSelected(false);

            if (tipoCucina != null && !tipoCucina.isBlank()) {
                String[] tipi = tipoCucina.split(";");
                for (String tipo : tipi) {
                    switch (tipo.trim().toLowerCase()) {
                        case "italiana":
                            italianaCheck.setSelected(true);
                            break;
                        case "hamburger":
                            hamburgerCheck.setSelected(true);
                            break;
                        case "asiatica":
                            asiaticaCheck.setSelected(true);
                            break;
                        case "sudamericana":
                            sudamericanaCheck.setSelected(true);
                            break;
                    }
                }
            }

            // Prenotazione
            if (ristorante.isPrenotazione())
                prenotazioneSi.setSelected(true);
            else
                prenotazioneNo.setSelected(true);

            // Delivery
            if (ristorante.isDelivery())
                deliverySi.setSelected(true);
            else
                deliveryNo.setSelected(true);
        });
    }

    private String compilaCampo(String s) {
        return s == null ? "" : s;
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
