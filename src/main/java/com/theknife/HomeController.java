package com.theknife;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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

    private Stage stage;
    private Scene scene;
    private Parent root;

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

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
    private void onUserIconClicked(MouseEvent e) throws Exception {
        SessioneUtente sessione = SessioneUtente.getInstance();

        FXMLLoader loader;

        switch (sessione.getRuolo()) {
            case GUEST:
                //App.setRoot("welcome");
                
                loader = new FXMLLoader(getClass().getResource("welcome.fxml"));
                root = loader.load();

                WelcomeController controllerGuest = loader.getController();
                controllerGuest.setConnectionSocket(socket, in, out);

                stage = (Stage)((Node)e.getSource()).getScene().getWindow();
                stage.getScene().setRoot(root);
                break;
            case CLIENTE:
                //App.setRoot("menuCliente");

                loader = new FXMLLoader(getClass().getResource("menuCliente.fxml"));
                root = loader.load();

                MenuClienteController controllerCliente = loader.getController();
                controllerCliente.setConnectionSocket(socket, in, out);

                stage = (Stage)((Node)e.getSource()).getScene().getWindow();
                stage.getScene().setRoot(root);
                break;
            case RISTORATORE:
                //App.setRoot("menuRistoratore");

                loader = new FXMLLoader(getClass().getResource("menuRistoratore.fxml"));
                root = loader.load();

                MenuRistoratoreController controllerRistoratore = loader.getController();
                controllerRistoratore.setConnectionSocket(socket, in, out);

                stage = (Stage)((Node)e.getSource()).getScene().getWindow();
                stage.getScene().setRoot(root);
                break;
            default:
                break;
        }
    }

    public void setConnectionSocket(Socket socket, ObjectInputStream in, ObjectOutputStream out){
        this.socket = socket;
        this.in = in;
        this.out = out;
    }
}
