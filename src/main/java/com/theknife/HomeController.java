package com.theknife;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
    @FXML private RadioButton italianaCheck;
    @FXML private RadioButton burgerCheck;
    @FXML private RadioButton asiaticaCheck;
    @FXML private RadioButton sudamericanaCheck;
    @FXML private TextField prezzoMinField;
    @FXML private TextField prezzoMaxField;
    @FXML private RadioButton deliverySi;
    @FXML private RadioButton deliveryNo;
    @FXML private RadioButton prenotazioniSi;
    @FXML private RadioButton prenotazioniNo;
    @FXML private Slider votoSlider;
    @FXML private Label votoValueLabel;

    private ToggleGroup tipoCucinaGroup;
    private ToggleGroup deliveryGroup;
    private ToggleGroup prenotazioniGroup;

    private Stage stage;
    private Scene scene;
    private Parent root;
    private Parent previousRoot;

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

        // Gruppi radio per tipologie cucina
        tipoCucinaGroup = new ToggleGroup();
        if (italianaCheck != null) italianaCheck.setToggleGroup(tipoCucinaGroup);
        if (burgerCheck != null) burgerCheck.setToggleGroup(tipoCucinaGroup);
        if (asiaticaCheck != null) asiaticaCheck.setToggleGroup(tipoCucinaGroup);
        if (sudamericanaCheck != null) sudamericanaCheck.setToggleGroup(tipoCucinaGroup);

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
    private void onSearchClicked(ActionEvent e) throws Exception {
        
        //System.out.println("Ricerca avviata per: " + searchField.getText());

        // 1) Nome e città
        String nome = (searchField != null) ? searchField.getText().trim() : null;
        if (nome != null && nome.isBlank()) nome = null;

        String citta = (positionField != null) ? positionField.getText().trim() : null;
        if (citta != null && (citta.isBlank() || "Posizione".equalsIgnoreCase(citta))) citta = null;

        // 2) Tipo cucina (il server accetta UNA stringa)
        String tipoCucina = null;

        if (italianaCheck != null && italianaCheck.isSelected() && filtersBox.isVisible()) { tipoCucina = "italiana"; }
        if (burgerCheck != null && burgerCheck.isSelected() && filtersBox.isVisible()) { if (tipoCucina == null) tipoCucina = "hamburger"; }
        if (asiaticaCheck != null && asiaticaCheck.isSelected() && filtersBox.isVisible()) { if (tipoCucina == null) tipoCucina = "asiatica"; }
        if (sudamericanaCheck != null && sudamericanaCheck.isSelected() && filtersBox.isVisible()) { if (tipoCucina == null) tipoCucina = "sudamericana"; }

        // 3) Prezzo min/max
        Integer prezzoMin = null;
        Integer prezzoMax = null;

        if (filtersBox != null && filtersBox.isVisible()) {
            prezzoMin = parseNullableInt(prezzoMinField);
            prezzoMax = parseNullableInt(prezzoMaxField);
        }

        // 4) Delivery / Prenotazioni (null = non filtrare)
        Boolean delivery = null;

        if (deliverySi != null && deliverySi.isSelected() && filtersBox.isVisible()) delivery = true;
        else if (deliveryNo != null && deliveryNo.isSelected() && filtersBox.isVisible()) delivery = false;

        Boolean prenotazione = null;

        if (prenotazioniSi != null && prenotazioniSi.isSelected() && filtersBox.isVisible()) prenotazione = true;
        else if (prenotazioniNo != null && prenotazioniNo.isSelected() && filtersBox.isVisible()) prenotazione = false;

        // 5) Voto minimo (null = non filtrare)
        Double votoMin = null;
        
        if (votoSlider != null && filtersBox != null && filtersBox.isVisible()) {
            double v = Math.round(votoSlider.getValue() * 2) / 2.0; // coerente con label
            if (v > 0) votoMin = v;  // se vuoi permettere "nessun filtro" a 0
        }

        System.out.println("[HOME] Ricerca: nome=" + nome + ", citta=" + citta + ", tipo=" + tipoCucina
                + ", min=" + prezzoMin + ", max=" + prezzoMax + ", delivery=" + delivery + ", pren=" + prenotazione + ", votoMin=" + votoMin);

        // 6) Apri pagina risultati e PASSA i parametri
        FXMLLoader loader = new FXMLLoader(getClass().getResource("risultatiRistoranti.fxml"));
        Parent root = loader.load();

        RisultatiRistorantiController controller = loader.getController();
        controller.setConnectionSocket(socket, in, out);
        controller.setPreviousRoot(((Node)e.getSource()).getScene().getRoot());

        // ORDINE IDENTICO AL SERVER:
        controller.setSearchParams(nome, citta, tipoCucina, prezzoMin, prezzoMax, delivery, prenotazione, votoMin);

        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
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
                controllerGuest.setPreviousRoot(((Node)e.getSource()).getScene().getRoot());

                stage = (Stage)((Node)e.getSource()).getScene().getWindow();
                stage.getScene().setRoot(root);
                break;
            case CLIENTE:
                //App.setRoot("menuCliente");

                loader = new FXMLLoader(getClass().getResource("menuCliente.fxml"));
                root = loader.load();

                MenuClienteController controllerCliente = loader.getController();
                controllerCliente.setConnectionSocket(socket, in, out);
                controllerCliente.setPreviousRoot(((Node)e.getSource()).getScene().getRoot());

                stage = (Stage)((Node)e.getSource()).getScene().getWindow();
                stage.getScene().setRoot(root);
                break;
            case RISTORATORE:
                //App.setRoot("menuRistoratore");

                loader = new FXMLLoader(getClass().getResource("menuRistoratore.fxml"));
                root = loader.load();

                MenuRistoratoreController controllerRistoratore = loader.getController();
                controllerRistoratore.setConnectionSocket(socket, in, out);
                controllerRistoratore.setPreviousRoot(((Node)e.getSource()).getScene().getRoot());

                stage = (Stage)((Node)e.getSource()).getScene().getWindow();
                stage.getScene().setRoot(root);
                break;
            default:
                break;
        }
    }

    private Integer parseNullableInt(TextField field) {
        if (field != null) {
            String text = field.getText().trim();
            if (!text.isBlank()) {
                try {
                    return Integer.parseInt(text);
                } catch (NumberFormatException e) {
                    System.out.println("[HOME] Valore non valido per campo intero: " + text);
                }
            }
        }
        return null;
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
