package com.theknife;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class HomeController {

    @FXML private TextField searchField;
    @FXML private TextField positionField;
    @FXML private VBox filtersBox;

    // Campi filtri
    @FXML private CheckBox pizzaCheck;
    @FXML private CheckBox burgerCheck;
    @FXML private CheckBox asiaticaCheck;
    @FXML private CheckBox sudamericanaCheck;
    @FXML private TextField prezzoMinField;
    @FXML private TextField prezzoMaxField;
    @FXML private RadioButton deliverySi;
    @FXML private RadioButton deliveryNo;
    @FXML private RadioButton prenotazioniSi;
    @FXML private RadioButton prenotazioniNo;
    @FXML private Slider votoSlider;
    @FXML private Label votoValueLabel;

    private ToggleGroup deliveryGroup;
    private ToggleGroup prenotazioniGroup;

    @FXML
    public void initialize() {
        // Imposta il campo posizione in base ai dati salvati
        String luogo = SessioneUtente.getInstance().getLuogo();
        if (luogo != null && !luogo.isEmpty()) {
            positionField.setText(luogo);
        } else {
            positionField.setText("Posizione");
        }

        // Gruppi radio per le opzioni sì/no
        deliveryGroup = new ToggleGroup();
        prenotazioniGroup = new ToggleGroup();

        if (deliverySi != null && deliveryNo != null) {
            deliverySi.setToggleGroup(deliveryGroup);
            deliveryNo.setToggleGroup(deliveryGroup);
        }

        if (prenotazioniSi != null && prenotazioniNo != null) {
            prenotazioniSi.setToggleGroup(prenotazioniGroup);
            prenotazioniNo.setToggleGroup(prenotazioniGroup);
        }

        // Slider voto → aggiorna etichetta valore in tempo reale
        if (votoSlider != null && votoValueLabel != null) {
            votoSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                double voto = Math.round(newVal.doubleValue() * 2) / 2.0; // Arrotonda a 0.5
                votoValueLabel.setText(String.format("%.1f", voto));
            });
        }
    }

    @FXML
    private void onSearchClicked() {
        System.out.println("Ricerca avviata per: " + searchField.getText());
    }

    @FXML
    private void onFilterClicked() {
        if (filtersBox != null) {
            boolean isVisible = filtersBox.isVisible();
            filtersBox.setVisible(!isVisible);
            filtersBox.setManaged(!isVisible);
        }
    }

    // Clic sull'icona utente → apre la schermata corretta in base al ruolo
    @FXML
    private void onUserIconClicked() throws Exception {
        SessioneUtente sessione = SessioneUtente.getInstance();
        switch (sessione.getRuolo()) {
            case GUEST:
                App.setRoot("welcome");
                break;
            case CLIENTE:
                App.setRoot("menuCliente");
                break;
            case RISTORATORE:
                App.setRoot("menuRistoratore");
                break;
            default:
                break;
        }
    }
}
