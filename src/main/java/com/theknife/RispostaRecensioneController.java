package com.theknife;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * Controller JavaFX della schermata per inserire, modificare o eliminare la
 * risposta del ristoratore
 * a una {@link Recensione}.
 * <p>
 * Visualizza i dettagli della recensione e invia al server i comandi per
 * salvare/eliminare la risposta
 * tramite socket. Al ritorno può eseguire un callback di refresh della
 * schermata precedente.
 * </p>
 */
public class RispostaRecensioneController {

    @FXML
    private Label lblTitolo;
    @FXML
    private Label lblStelle;
    @FXML
    private Label lblRecensione;
    @FXML
    private TextArea txtRisposta;
    @FXML
    private Button btnDelete;
    @FXML
    private Label lblStatus;

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private Parent previousRoot;

    private Ristorante ristorante;
    private Recensione recensione;
    private String usernameRecensore;

    private Runnable onBackRefresh;

    public void setConnectionSocket(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        this.socket = socket;
        this.in = in;
        this.out = out;
    }

    public void setPreviousRoot(Parent previousRoot) {
        this.previousRoot = previousRoot;
    }

    public void setData(Ristorante r, Recensione rec, String usernameRecensore) {
        this.ristorante = r;
        this.recensione = rec;
        this.usernameRecensore = usernameRecensore;

        lblTitolo.setText("Risposta a @" + (usernameRecensore == null ? "utente" : usernameRecensore));
        lblStelle.setText(stars(rec.getStelle()) + " (" + rec.getStelle() + "/5)");
        lblRecensione.setText(rec.getTesto() == null ? "" : rec.getTesto());

        String existing = rec.getRisposta();
        txtRisposta.setText(existing == null ? "" : existing);

        boolean has = existing != null && !existing.trim().isEmpty();
        btnDelete.setVisible(has);
        btnDelete.setManaged(has);

        lblStatus.setText("");
    }

    public void setOnBackRefresh(Runnable r) {
        this.onBackRefresh = r;
    }

    @FXML
    private void onBackClicked(ActionEvent e) {
        try {
            if (onBackRefresh != null)
                onBackRefresh.run();
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            if (previousRoot != null)
                stage.getScene().setRoot(previousRoot);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void onSaveClicked(ActionEvent e) {
        if (recensione == null || out == null || in == null)
            return;

        String risposta = txtRisposta.getText();
        if (risposta == null)
            risposta = "";
        risposta = risposta.trim();

        if (risposta.isEmpty()) {
            lblStatus.setText("Scrivi una risposta prima di salvare (oppure usa elimina).");
            return;
        }

        try {
            String ownerUsername = SessioneUtente.getInstance().getUsername();

            out.writeObject("addAnswer");
            out.writeObject(ownerUsername);
            out.writeObject(recensione.getId());
            out.writeObject(risposta);
            out.flush();

            ServerResponse resp = (ServerResponse) in.readObject();
            if (!"OK".equals(resp.getStatus())) {
                lblStatus.setText(String.valueOf(resp.getPayload()));
                return;
            }

            // aggiorno stato locale UI
            recensione.setRisposta(risposta);
            btnDelete.setVisible(true);
            btnDelete.setManaged(true);
            lblStatus.setText("Risposta salvata!");

            if (onBackRefresh != null)
                onBackRefresh.run();
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            if (previousRoot != null)
                stage.getScene().setRoot(previousRoot);

        } catch (Exception ex) {
            ex.printStackTrace();
            lblStatus.setText("Errore nel salvataggio");
        }
    }

    @FXML
    private void onDeleteClicked(ActionEvent e) {
        if (recensione == null || out == null || in == null)
            return;

        try {
            String ownerUsername = SessioneUtente.getInstance().getUsername();

            out.writeObject("deleteAnswer");
            out.writeObject(ownerUsername);
            out.writeObject(recensione.getId());
            out.flush();

            ServerResponse resp = (ServerResponse) in.readObject();
            if (!"OK".equals(resp.getStatus())) {
                lblStatus.setText(String.valueOf(resp.getPayload()));
                return;
            }

            recensione.setRisposta(null);
            txtRisposta.setText("");
            btnDelete.setVisible(false);
            btnDelete.setManaged(false);
            lblStatus.setText("Risposta eliminata!");

            if (onBackRefresh != null)
                onBackRefresh.run();
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            if (previousRoot != null)
                stage.getScene().setRoot(previousRoot);

        } catch (Exception ex) {
            ex.printStackTrace();
            lblStatus.setText("Errore nell'eliminazione");
        }
    }

    private static String stars(int n) {
        int x = Math.max(0, Math.min(5, n));
        return "★★★★★".substring(0, x) + "☆☆☆☆☆".substring(0, 5 - x);
    }
}
