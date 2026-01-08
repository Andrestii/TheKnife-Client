package com.theknife;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class EditRecensioneController {

    @FXML private Label lblTitolo;
    @FXML private Spinner<Integer> spStelle;
    @FXML private TextArea txtTesto;
    @FXML private Label lblStatus;

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private Ristorante ristorante;
    private Parent previousRoot;

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

    public void setPreviousRoot(Parent previousRoot) {
        this.previousRoot = previousRoot;
    }

    public void setRistorante(Ristorante r) {
        this.ristorante = r;
        lblTitolo.setText("Modifica recensione: " + (r != null ? r.getNome() : ""));
        Platform.runLater(this::loadMyReview);
    }

    public void setOnBackRefresh(Runnable r) {
        this.onBackRefresh = r;
    }

    private void loadMyReview() {
        if (ristorante == null || out == null || in == null) return;

        try {
            String username = SessioneUtente.getInstance().getUsername();

            out.writeObject("getMyReview");
            out.writeObject(username);
            out.writeObject(ristorante.getId());
            out.flush();

            Object obj = in.readObject();
            if (obj instanceof ServerResponse) {
                ServerResponse resp = (ServerResponse) obj;
                if ("OK".equals(resp.getStatus()) && resp.getPayload() instanceof Recensione) {
                    Recensione rec = (Recensione) resp.getPayload();
                    spStelle.getValueFactory().setValue(rec.getStelle());
                    txtTesto.setText(rec.getTesto() == null ? "" : rec.getTesto());
                    lblStatus.setText("");
                } else {
                    lblStatus.setText(String.valueOf(resp.getPayload()));
                }
            } else {
                lblStatus.setText("Risposta non valida dal server");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            lblStatus.setText("Errore nel caricamento della recensione");
        }
    }

    @FXML
    private void onBackClicked(ActionEvent e) {
        try {
            if (onBackRefresh != null) onBackRefresh.run();
            Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            if (previousRoot != null) stage.getScene().setRoot(previousRoot);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void onSaveClicked(ActionEvent e) {
        if (ristorante == null || out == null || in == null) return;

        String testo = txtTesto.getText() == null ? "" : txtTesto.getText().trim();
        int stelle = spStelle.getValue();

        if (testo.isBlank()) {
            lblStatus.setText("Inserisci un testo per la recensione");
            return;
        }

        try {
            String username = SessioneUtente.getInstance().getUsername();

            out.writeObject("editReview");
            out.writeObject(username);
            out.writeObject(ristorante.getId());
            out.writeObject(stelle);
            out.writeObject(testo);
            out.flush();

            Object obj = in.readObject();
            if (obj instanceof ServerResponse) {
                ServerResponse resp = (ServerResponse) obj;
                if ("OK".equals(resp.getStatus())) {
                    lblStatus.setText("Recensione aggiornata!");
                } else {
                    lblStatus.setText(String.valueOf(resp.getPayload()));
                }
            } else {
                lblStatus.setText("Risposta non valida dal server");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            lblStatus.setText("Errore durante il salvataggio");
        }
    }

    @FXML
    private void onDeleteClicked(ActionEvent e) {
        if (ristorante == null || out == null || in == null) return;

        try {
            String username = SessioneUtente.getInstance().getUsername();

            out.writeObject("deleteReview");
            out.writeObject(username);
            out.writeObject(ristorante.getId());
            out.flush();

            Object obj = in.readObject();
            if (obj instanceof ServerResponse) {
                ServerResponse resp = (ServerResponse) obj;

                if ("OK".equals(resp.getStatus())) {
                    if (onBackRefresh != null) onBackRefresh.run();
                    Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
                    if (previousRoot != null) stage.getScene().setRoot(previousRoot);
                } else {
                    lblStatus.setText(String.valueOf(resp.getPayload()));
                }
            } else {
                lblStatus.setText("Risposta non valida dal server");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            lblStatus.setText("Errore durante l'eliminazione");
        }
    }

}
