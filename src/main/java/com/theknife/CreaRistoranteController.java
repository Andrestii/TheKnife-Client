package com.theknife;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

import javafx.event.ActionEvent;

public class CreaRistoranteController {

    @FXML
    private TextField nomeField;

    @FXML
    private TextField cittaField;

    @FXML
    private TextField indirizzoField;

    @FXML
    private TextField telefonoField;

    @FXML
    private CheckBox italianaCheck;

    @FXML
    private CheckBox hamburgerCheck;

    @FXML
    private CheckBox asiaticaCheck;

    @FXML
    private CheckBox sudamericanaCheck;

    @FXML
    private RadioButton deliverySi;

    @FXML
    private RadioButton deliveryNo;

    @FXML
    private RadioButton prenotazioneSi;

    @FXML
    private RadioButton prenotazioneNo;

    @FXML
    private Button btnCrea;

    @FXML
    private ToggleGroup deliveryGroup;

    @FXML
    private ToggleGroup prenGroup;

    @FXML
    public void initialize() {
        // Assicura che solo uno dei due toggle sia selezionabile
        deliveryGroup = new ToggleGroup();
        deliverySi.setToggleGroup(deliveryGroup);
        deliveryNo.setToggleGroup(deliveryGroup);

        prenGroup = new ToggleGroup();
        prenotazioneSi.setToggleGroup(prenGroup);
        prenotazioneNo.setToggleGroup(prenGroup);
    }

    @FXML
    private void onBackClicked(ActionEvent event) throws IOException {
       App.setRoot("ristoranti");
    }

    @FXML
    private void onCreaClicked(ActionEvent event) {

        String nome = nomeField.getText().trim();
        String citta = cittaField.getText().trim();
        String indirizzo = indirizzoField.getText().trim();
        String telefono = telefonoField.getText().trim();

        boolean cucinaItaliana = italianaCheck.isSelected();
        boolean cucinaHamburger = hamburgerCheck.isSelected();
        boolean cucinaAsiatica = asiaticaCheck.isSelected();
        boolean cucinaSudamericana = sudamericanaCheck.isSelected();

        RadioButton deliverySelected = (RadioButton) deliveryGroup.getSelectedToggle();
        RadioButton prenSelected = (RadioButton) prenGroup.getSelectedToggle();

        // VALIDAZIONI SEMPLICI
        if (nome.isEmpty() || citta.isEmpty() || indirizzo.isEmpty() || telefono.isEmpty()) {
            showAlert("Errore", "Compila tutti i campi obbligatori.");
            return;
        }

        if (deliverySelected == null) {
            showAlert("Errore", "Seleziona se il ristorante ha delivery.");
            return;
        }

        if (prenSelected == null) {
            showAlert("Errore", "Seleziona se il ristorante accetta prenotazioni online.");
            return;
        }

        boolean delivery = deliverySelected == deliverySi;
        boolean prenotazioni = prenSelected == prenotazioneSi;

        // DEBUG DATA
        System.out.println("--- DATI RISTORANTE ---");
        System.out.println("Nome: " + nome);
        System.out.println("Città: " + citta);
        System.out.println("Indirizzo: " + indirizzo);
        System.out.println("Telefono: " + telefono);
        System.out.println("Cucine:");
        if (cucinaItaliana) System.out.println("- Italiana");
        if (cucinaHamburger) System.out.println("- Hamburger");
        if (cucinaAsiatica) System.out.println("- Asiatica");
        if (cucinaSudamericana) System.out.println("- Sudamericana");
        System.out.println("Delivery: " + (delivery ? "Sì" : "No"));
        System.out.println("Prenotazioni online: " + (prenotazioni ? "Sì" : "No"));

        // QUI potresti chiamare il tuo servizio / DAO
        // RistoranteDAO.salva(...);

        showAlert("Successo", "Ristorante creato correttamente!");
    }

    private void showAlert(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.show();
    }
}
