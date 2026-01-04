package com.theknife;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RisultatiRistorantiController {

    @FXML private FlowPane listaRisultati;

    private Stage stage;
    private Parent root;

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    // filtri ricevuti dalla pagina di ricerca
    private String nome;
    private String citta;
    private String tipoCucina;
    private Integer prezzoMin;
    private Integer prezzoMax;
    private Boolean delivery;
    private Boolean prenotazione;

    @FXML
    private void initialize() {
        listaRisultati.getChildren().clear();
        listaRisultati.setHgap(20);
        listaRisultati.setVgap(20);
    }

    public void setConnectionSocket(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        this.socket = socket;
        this.in = in;
        this.out = out;
    }

    /**
     * Chiamalo PRIMA di mostrare la pagina (o subito dopo), poi lui carica i risultati.
     * L’ordine parametri deve combaciare con il server: nome, citta, tipoCucina, prezzoMin, prezzoMax, delivery, prenotazione
     */
    public void setSearchParams(String nome, String citta, String tipoCucina,
                                Integer prezzoMin, Integer prezzoMax,
                                Boolean delivery, Boolean prenotazione) {

        this.nome = nome;
        this.citta = citta;
        this.tipoCucina = tipoCucina;
        this.prezzoMin = prezzoMin;
        this.prezzoMax = prezzoMax;
        this.delivery = delivery;
        this.prenotazione = prenotazione;

        Platform.runLater(this::loadResults);
    }

    @FXML
    private void onBackClicked(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));
        root = loader.load();

        HomeController controller = loader.getController();
        controller.setConnectionSocket(socket, in, out);

        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    private void loadResults() {
        if (out == null || in == null) {
            showEmptyMessage("Connessione non disponibile");
            return;
        }

        try {
            listaRisultati.getChildren().clear();

            out.writeObject("searchRestaurants");
            out.writeObject(nome);
            out.writeObject(citta);
            out.writeObject(tipoCucina);
            out.writeObject(prezzoMin);
            out.writeObject(prezzoMax);
            out.writeObject(delivery);
            out.writeObject(prenotazione);
            out.flush();

            Object obj = in.readObject();
            if (!(obj instanceof ServerResponse)) {
                System.out.println("[CLIENT] Risposta non valida dal server: " + obj);
                showEmptyMessage("Errore nel caricamento dei risultati");
                return;
            }

            ServerResponse resp = (ServerResponse) obj;
            if (!"OK".equals(resp.getStatus())) {
                showEmptyMessage("Errore nel caricamento dei risultati");
                return;
            }

            @SuppressWarnings("unchecked")
            List<Ristorante> lista = (List<Ristorante>) resp.getPayload();

            if (lista == null || lista.isEmpty()) {
                showEmptyMessage("Nessun ristorante trovato");
                return;
            }

            for (Ristorante r : lista) {
                listaRisultati.getChildren().add(createRestaurantTile(r));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            showEmptyMessage("Errore nel caricamento dei risultati");
        }
    }

    private void showEmptyMessage(String msg) {
        Label label = new Label(msg);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        listaRisultati.getChildren().setAll(label);
    }

    private VBox createRestaurantTile(Ristorante r) {
        VBox tile = new VBox(10);
        tile.setAlignment(Pos.CENTER);
        tile.setPadding(new Insets(10));
        tile.setPrefSize(150, 190);
        tile.setMaxSize(150, 190);
        tile.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-radius: 12;" +
            "-fx-border-color: rgb(47,98,84);" +
            "-fx-border-width: 3;"
        );

        ImageView img = new ImageView(new Image(getClass().getResourceAsStream("restaurant.png")));
        img.setFitWidth(110);
        img.setFitHeight(90);
        img.setPreserveRatio(true);

        StackPane imgBox = new StackPane(img);
        imgBox.setPrefSize(110, 90);
        imgBox.setMaxSize(110, 90);
        imgBox.setStyle(
            "-fx-background-color: #f2f2f2;" +
            "-fx-background-radius: 10;" +
            "-fx-border-radius: 10;" +
            "-fx-border-color: #cccccc;" +
            "-fx-border-width: 1;"
        );

        Label nomeLbl = new Label(r.getNome());
        nomeLbl.setWrapText(true);
        nomeLbl.setMaxWidth(130);
        nomeLbl.setAlignment(Pos.CENTER);
        nomeLbl.setStyle("-fx-text-fill: rgb(47,98,84); -fx-font-weight: bold; -fx-font-size: 14px;");

        tile.getChildren().addAll(imgBox, nomeLbl);

        // Click: qui puoi aprire una pagina dettagli/ristorante se ce l’hai
        tile.setOnMouseClicked(ev -> {
            System.out.println("[CLIENT] Cliccato risultato: " + r.getId() + " - " + r.getNome());
            // Esempio (se esiste): apriSchedaRistorante(r);
        });

        return tile;
    }

    /**
     * Piccola interfaccia “ponte” opzionale:
     * se i controller delle pagine precedenti la implementano, puoi passare la socket senza cast specifici.
     */
    public interface ConnectionAware {
        void setConnectionSocket(Socket socket, ObjectInputStream in, ObjectOutputStream out);
    }
}
