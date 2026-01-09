/**
 * Autori del progetto:
 *
 * - Lorenzo De Paoli
 *   Matricola: 753577
 *   Sede: VA
 *
 * - Andrea Onesti
 *   Matricola: 754771
 *   Sede: VA
 *
 * - Weili Wu
 *   Matricola: 752602
 *   Sede: VA
 */
package com.theknife;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller della schermata "Le mie recensioni".
 * Recupera dal server le recensioni inserite dall'utente e le mostra in lista,
 * permettendo l'apertura della schermata di modifica.
 */
public class MyRecensioniController {

    @FXML
    private Label lblTitolo;
    @FXML
    private VBox boxRecensioni;
    @FXML
    private Label lblStatus;

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private Parent previousRoot;

    public void setConnectionSocket(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        this.socket = socket;
        this.in = in;
        this.out = out;

        Platform.runLater(this::loadMyReviews);
    }

    public void setPreviousRoot(Parent previousRoot) {
        this.previousRoot = previousRoot;
    }

    @FXML
    private void onBackClicked(ActionEvent e) {
        try {
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            if (previousRoot != null)
                stage.getScene().setRoot(previousRoot);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadMyReviews() {
        if (out == null || in == null)
            return;

        try {
            boxRecensioni.getChildren().clear();
            lblStatus.setText("");

            String username = SessioneUtente.getInstance().getUsername();

            // 1) mie recensioni (con idRistorante SETTATO DAL SERVER!)
            out.writeObject("getMyReviews");
            out.writeObject(username);
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
                lblStatus.setText("Non hai ancora lasciato recensioni");
                return;
            }

            // 2) nomi ristoranti nello stesso ordine (ORDER BY rec.id DESC)
            out.writeObject("getMyReviewRestaurantNames");
            out.writeObject(username);
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
            List<String> nomiRistoranti = (List<String>) resp2.getPayload();

            int n = Math.min(recensioni.size(), nomiRistoranti != null ? nomiRistoranti.size() : 0);
            for (int i = 0; i < n; i++) {
                boxRecensioni.getChildren().add(createMyReviewTile(nomiRistoranti.get(i), recensioni.get(i)));
            }

            if (n == 0)
                lblStatus.setText("Errore: nomi ristoranti non disponibili");

        } catch (Exception ex) {
            ex.printStackTrace();
            lblStatus.setText("Errore nel caricamento delle tue recensioni");
        }
    }

    private VBox createMyReviewTile(String nomeRistorante, Recensione rec) {
        VBox tile = new VBox(8);
        tile.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-color: rgb(47,98,84);" +
                        "-fx-border-width: 3;");
        tile.setPadding(new Insets(14));
        tile.setMaxWidth(820);

        HBox header = new HBox(10);

        Label lblRistorante = new Label(nomeRistorante == null ? "Ristorante" : nomeRistorante);
        lblRistorante.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: rgb(47,98,84);");

        Label lblStelle = new Label(stars(rec.getStelle()) + " (" + rec.getStelle() + "/5)");
        lblStelle.setStyle("-fx-font-size: 14px; -fx-text-fill: #333; -fx-font-weight: bold;");

        Button btnModifica = new Button("Modifica");
        btnModifica.setStyle(
                "-fx-background-color: rgb(47,98,84);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 6 12;");
        btnModifica.setOnAction(e -> openEditRecensione(e, rec, nomeRistorante));

        header.getChildren().addAll(lblRistorante, lblStelle, btnModifica);

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
                            "-fx-border-width: 1;");
            boxRisposta.setPadding(new Insets(10));

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

    private void openEditRecensione(ActionEvent e, Recensione rec, String nomeRistorante) {
        try {
            if (rec.getIdRistorante() <= 0) {
                lblStatus.setText("Errore: idRistorante non disponibile");
                return;
            }

            // Creo un ristorante "minimo" perché EditRecensioneController vuole un
            // Ristorante
            // e usa solo getId() e getNome() per titolo e query getMyReview.
            Ristorante rMin = new Ristorante(
                    rec.getIdRistorante(),
                    nomeRistorante == null ? "" : nomeRistorante,
                    "", "", "",
                    0.0, 0.0,
                    0,
                    false, false,
                    "",
                    "");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("editRecensione.fxml"));
            Parent root = loader.load();

            EditRecensioneController c = loader.getController();
            c.setConnectionSocket(socket, in, out);
            c.setPreviousRoot(((Node) e.getSource()).getScene().getRoot());
            c.setOnBackRefresh(() -> Platform.runLater(this::loadMyReviews));
            c.setRistorante(rMin);

            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);

        } catch (Exception ex) {
            ex.printStackTrace();
            lblStatus.setText("Errore apertura pagina modifica");
        }
    }

    private static String stars(int n) {
        int x = Math.max(0, Math.min(5, n));
        return "★★★★★".substring(0, x) + "☆☆☆☆☆".substring(0, 5 - x);
    }
}
