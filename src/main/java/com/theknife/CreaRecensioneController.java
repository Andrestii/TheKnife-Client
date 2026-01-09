package com.theknife;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * Controller della schermata di creazione di una recensione.
 * Gestisce l'inserimento di stelle e testo e invia la richiesta al server
 * tramite socket, tornando poi alla schermata precedente.
 */
public class CreaRecensioneController {

    @FXML
    private Label lblTitolo;
    @FXML
    private Spinner<Integer> spStelle;
    @FXML
    private TextArea txtTesto;
    @FXML
    private Label lblStatus;

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Parent previousRoot;

    private Ristorante ristorante;
    private Runnable onBackRefresh;

    @FXML
    private void initialize() {
        spStelle.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 5));
    }

    public void setConnectionSocket(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        this.socket = socket;
        this.in = in;
        this.out = out;
    }

    public void setRistorante(Ristorante r) {
        this.ristorante = r;
        lblTitolo.setText("Recensisci: " + (r != null ? r.getNome() : ""));
    }

    public void setPreviousRoot(Parent previousRoot) {
        this.previousRoot = previousRoot;
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
    private void onSubmitClicked(ActionEvent e) {
        if (ristorante == null || out == null || in == null)
            return;

        String testo = txtTesto.getText() == null ? "" : txtTesto.getText().trim();
        int stelle = spStelle.getValue();

        if (testo.isBlank()) {
            lblStatus.setText("Inserisci un testo per la recensione");
            return;
        }

        try {
            String username = SessioneUtente.getInstance().getUsername();

            out.writeObject("addReview");
            out.writeObject(username);
            out.writeObject(ristorante.getId());
            out.writeObject(stelle);
            out.writeObject(testo);
            out.flush();

            Object obj = in.readObject();
            if (obj instanceof ServerResponse) {
                ServerResponse resp = (ServerResponse) obj;
                if ("OK".equals(resp.getStatus())) {
                    // lblStatus.setText("Recensione inviata!");
                    if (onBackRefresh != null)
                        onBackRefresh.run();
                    Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                    if (previousRoot != null)
                        stage.getScene().setRoot(previousRoot);
                } else {
                    lblStatus.setText(String.valueOf(resp.getPayload()));
                }
            } else {
                lblStatus.setText("Risposta non valida dal server");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            lblStatus.setText("Errore durante l'invio della recensione");
        }
    }
}
