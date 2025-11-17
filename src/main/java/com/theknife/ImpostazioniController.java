package com.theknife;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImpostazioniController {

    @FXML private TextField nomeField;
    @FXML private TextField cognomeField;
    @FXML private TextField dataNascitaField;
    @FXML private TextField domicilioField;
    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private ImageView userIcon;


    @FXML
    public void initialize() {

        SessioneUtente sessione = SessioneUtente.getInstance();

        // Imposta immagine utente in base al ruolo
        if (sessione.getRuolo() == Ruolo.CLIENTE) {
            userIcon.setImage(new Image(getClass().getResourceAsStream("/com/theknife/clientIcon.png")));
        } else if (sessione.getRuolo() == Ruolo.RISTORATORE) {
            userIcon.setImage(new Image(getClass().getResourceAsStream("/com/theknife/chefIcon.png")));
        }

        nomeField.setText(sessione.getNome());
        cognomeField.setText(sessione.getCognome());
        dataNascitaField.setText(sessione.getDataNascita());
        domicilioField.setText(sessione.getLuogo());
        usernameField.setText(sessione.getUsername());
        passwordField.setText(sessione.getPassword());
    }

    @FXML
    private void onConfermaClicked() throws Exception {

        SessioneUtente sessione = SessioneUtente.getInstance();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma modifiche");
        alert.setHeaderText("Confermi le modifiche ai tuoi dati?");
        alert.setContentText("Le informazioni verranno aggiornate.");
        
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {

            sessione.setNome(nomeField.getText());
            sessione.setCognome(cognomeField.getText());
            sessione.setDataNascita(dataNascitaField.getText());
            sessione.setLuogo(domicilioField.getText());
            sessione.setUsername(usernameField.getText());
            sessione.setPassword(passwordField.getText());

            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setTitle("Successo");
            ok.setHeaderText("Dati aggiornati con successo!");
            ok.show();
        }
    }

    @FXML
    private void onIndietroClicked() throws Exception {

        SessioneUtente sessione = SessioneUtente.getInstance();

        switch (sessione.getRuolo()) {
            case CLIENTE:
                App.setRoot("menuCliente");
                break;

            case RISTORATORE:
                App.setRoot("menuRistoratore");
                break;

            default:
                App.setRoot("welcome");
                break;
        }
    }
}
