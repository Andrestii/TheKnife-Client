package com.theknife;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RecensioniController {

    @FXML private Label lblTitolo;
    @FXML private VBox boxRecensioni;
    @FXML private Label lblStatus;

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private Ristorante ristorante;
    private Parent previousRoot;

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
        lblTitolo.setText("Recensioni - " + (r != null ? r.getNome() : ""));
        Platform.runLater(this::loadReviews);
    }

    @FXML
    private void onBackClicked(ActionEvent e) {
        try {
            Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            if (previousRoot != null) stage.getScene().setRoot(previousRoot);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadReviews() {
        if (ristorante == null || out == null || in == null) return;

        try {
            boxRecensioni.getChildren().clear();
            lblStatus.setText("");

            // 1) prendo recensioni
            out.writeObject("viewReviews");
            out.writeObject(ristorante.getId());
            out.flush();

            Object obj1 = in.readObject();
            if (!(obj1 instanceof ServerResponse)) {
                lblStatus.setText("Risposta non valida");
                return;
            }
            ServerResponse resp1 = (ServerResponse) obj1;
            if (!"OK".equals(resp1.getStatus())) {
                lblStatus.setText(String.valueOf(resp1.getPayload()));
                return;
            }

            @SuppressWarnings("unchecked")
            List<Recensione> recensioni = (List<Recensione>) resp1.getPayload();

            if (recensioni == null || recensioni.isEmpty()) {
                lblStatus.setText("Ancora nessuna recensione");
                return;
            }

            // 2) prendo usernames nello stesso ordine
            out.writeObject("viewReviewUsernames");
            out.writeObject(ristorante.getId());
            out.flush();

            Object obj2 = in.readObject();
            if (!(obj2 instanceof ServerResponse)) {
                lblStatus.setText("Risposta non valida");
                return;
            }
            ServerResponse resp2 = (ServerResponse) obj2;
            if (!"OK".equals(resp2.getStatus())) {
                lblStatus.setText(String.valueOf(resp2.getPayload()));
                return;
            }

            @SuppressWarnings("unchecked")
            List<String> usernames = (List<String>) resp2.getPayload();

            // sicurezza: se per qualche motivo la lunghezza non combacia
            int n = Math.min(recensioni.size(), usernames != null ? usernames.size() : 0);

            for (int i = 0; i < n; i++) {
                boxRecensioni.getChildren().add(createReviewTile(usernames.get(i), recensioni.get(i)));
            }

            if (n == 0) {
                lblStatus.setText("Errore: usernames non disponibili");
            } else if (recensioni.size() != n) {
                lblStatus.setText("Attenzione: alcune recensioni non sono state visualizzate");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            lblStatus.setText("Errore nel caricamento recensioni");
        }
    }

    private VBox createReviewTile(String username, Recensione rec) {
        VBox tile = new VBox(8);
        tile.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-radius: 12;" +
            "-fx-border-color: rgb(47,98,84);" +
            "-fx-border-width: 3;"
        );
        tile.setPadding(new javafx.geometry.Insets(14));
        tile.setMaxWidth(820);

        HBox header = new HBox(10);

        Label lblUser = new Label("@" + (username == null ? "utente" : username));
        lblUser.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: rgb(47,98,84);");

        Label lblStelle = new Label(stars(rec.getStelle()) + " (" + rec.getStelle() + "/5)");
        lblStelle.setStyle("-fx-font-size: 14px; -fx-text-fill: #333; -fx-font-weight: bold;");

        header.getChildren().addAll(lblUser, lblStelle);

        Label lblTesto = new Label(rec.getTesto() == null ? "" : rec.getTesto());
        lblTesto.setWrapText(true);
        lblTesto.setStyle("-fx-font-size: 14px; -fx-text-fill: #222;");

        tile.getChildren().addAll(header, lblTesto);

        if (rec.getRisposta() != null && !rec.getRisposta().trim().isEmpty()) {
            VBox boxRisposta = new VBox(6);
            boxRisposta.setStyle(
                "-fx-background-color: #f2f2f2;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;" +
                "-fx-border-color: #cccccc;" +
                "-fx-border-width: 1;"
            );
            boxRisposta.setPadding(new javafx.geometry.Insets(10));

            Label titolo = new Label("Risposta del ristorante:");
            titolo.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333;");

            Label lblRisposta = new Label(rec.getRisposta());
            lblRisposta.setWrapText(true);
            lblRisposta.setStyle("-fx-font-size: 13px; -fx-text-fill: #333;");

            boxRisposta.getChildren().addAll(titolo, lblRisposta);
            tile.getChildren().add(boxRisposta);
        }

        return tile;
    }

    private static String stars(int n) {
        int x = Math.max(0, Math.min(5, n));
        return "★★★★★".substring(0, x) + "☆☆☆☆☆".substring(0, 5 - x);
    }
}
