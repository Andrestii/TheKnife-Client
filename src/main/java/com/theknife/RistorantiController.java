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

/**
 * Controller JavaFX della schermata che mostra i ristoranti associati
 * all'utente (ristoratore).
 * <p>
 * Recupera dal server la lista dei ristoranti dell'utente e li visualizza come
 * "tile" cliccabili.
 * Consente inoltre la navigazione verso la creazione di un nuovo ristorante e
 * verso la modifica
 * di un ristorante selezionato.
 * </p>
 */
public class RistorantiController {

    @FXML
    private FlowPane listaRistoranti;

    private Stage stage;
    private Parent root;
    private Parent previousRoot;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    @FXML
    private void initialize() {
        listaRistoranti.getChildren().clear();
        listaRistoranti.setHgap(20);
        listaRistoranti.setVgap(20);
    }

    @FXML
    private void onBackClicked(ActionEvent e) throws IOException {
        try {
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            if (previousRoot != null)
                stage.getScene().setRoot(previousRoot);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void onCreaRistoranteClicked(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("creaRistorante.fxml"));
        root = loader.load();

        CreaRistoranteController controller = loader.getController();
        controller.setConnectionSocket(socket, in, out);
        controller.setPreviousRoot(((Node) e.getSource()).getScene().getRoot());

        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    public void setConnectionSocket(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        this.socket = socket;
        this.in = in;
        this.out = out;

        // UI pronta, carico dati dal server
        Platform.runLater(this::loadMyRestaurants);
    }

    public void setPreviousRoot(Parent previousRoot) {
        this.previousRoot = previousRoot;
    }

    private void loadMyRestaurants() {
        if (out == null || in == null)
            return;

        try {
            listaRistoranti.getChildren().clear();

            String username = SessioneUtente.getInstance().getUsername();

            out.writeObject("getMyRestaurants");
            out.writeObject(username);
            out.flush();

            Object obj = in.readObject();
            if (!(obj instanceof ServerResponse)) {
                System.out.println("[CLIENT] Risposta non valida dal server: " + obj);
                showEmptyMessage("Errore nel caricamento dei ristoranti");
                return;
            }

            ServerResponse resp = (ServerResponse) obj;

            if (!"OK".equals(resp.getStatus())) {
                showEmptyMessage("Errore nel caricamento dei ristoranti");
                return;
            }

            @SuppressWarnings("unchecked")
            List<Ristorante> lista = (List<Ristorante>) resp.getPayload();

            if (lista == null || lista.isEmpty()) {
                showEmptyMessage("Nessun ristorante ancora. Creane uno col +");
                return;
            }

            // Crea i quadratini
            for (Ristorante r : lista) {
                listaRistoranti.getChildren().add(createRestaurantTile(r));
            }

        } catch (Exception e) {
            e.printStackTrace();
            showEmptyMessage("Errore nel caricamento dei ristoranti");
        }
    }

    private void showEmptyMessage(String msg) {
        Label label = new Label(msg);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        listaRistoranti.getChildren().setAll(label);
    }

    private VBox createRestaurantTile(Ristorante r) {
        // Creo la card del ristorante
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
                        "-fx-border-width: 3;");

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
                        "-fx-border-width: 1;");

        // Nome del ristorante
        Label nome = new Label(r.getNome());
        nome.setWrapText(true);
        nome.setMaxWidth(130);
        nome.setAlignment(Pos.CENTER);
        nome.setStyle("-fx-text-fill: rgb(47,98,84); -fx-font-weight: bold; -fx-font-size: 14px;");

        tile.getChildren().addAll(imgBox, nome);

        tile.setOnMouseClicked(ev -> {
            try {
                System.out.println("[CLIENT] Cliccato ristorante: " + r.getId() + " - " + r.getNome());

                FXMLLoader loader = new FXMLLoader(getClass().getResource("editRistorante.fxml"));
                Parent root = loader.load();

                EditRistoranteController controller = loader.getController();
                controller.setConnectionSocket(socket, in, out);
                controller.setPreviousRoot(((Node) ev.getSource()).getScene().getRoot());
                controller.setRistorante(r); // dico a EditRistoranteController quale ristorante modificare

                Stage stage = (Stage) tile.getScene().getWindow();
                stage.getScene().setRoot(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return tile;
    }
}
