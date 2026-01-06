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

public class PreferitiController {

    @FXML private FlowPane listaPreferiti;

    private Stage stage;
    private Parent root;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    @FXML
    private void initialize() {
        listaPreferiti.getChildren().clear();
        listaPreferiti.setHgap(20);
        listaPreferiti.setVgap(20);
    }

    @FXML
    private void onBackClicked(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("menuCliente.fxml"));
        root = loader.load();

        MenuClienteController controller = loader.getController();
        controller.setConnectionSocket(socket, in, out);

        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    public void setConnectionSocket(Socket socket, ObjectInputStream in, ObjectOutputStream out){
        this.socket = socket;
        this.in = in;
        this.out = out;

        Platform.runLater(this::loadFavorites);
    }

    private void loadFavorites() {
        if (out == null || in == null) return;

        try {
            listaPreferiti.getChildren().clear();

            String username = SessioneUtente.getInstance().getUsername();

            out.writeObject("listFavorites");
            out.writeObject(username);
            out.flush();

            Object obj = in.readObject();
            if (!(obj instanceof ServerResponse)) {
                System.out.println("[CLIENT] Risposta non valida dal server: " + obj);
                showEmptyMessage("Errore nel caricamento dei preferiti");
                return;
            }

            ServerResponse resp = (ServerResponse) obj;

            if (!"OK".equals(resp.getStatus())) {
                showEmptyMessage("Errore nel caricamento dei preferiti");
                return;
            }

            @SuppressWarnings("unchecked")
            List<Ristorante> lista = (List<Ristorante>) resp.getPayload();

            if (lista == null || lista.isEmpty()) {
                showEmptyMessage("Nessun preferito ancora");
                return;
            }

            for (Ristorante r : lista) {
                listaPreferiti.getChildren().add(createRestaurantTile(r));
            }

        } catch (Exception e) {
            e.printStackTrace();
            showEmptyMessage("Errore nel caricamento dei preferiti");
        }
    }

    private void showEmptyMessage(String msg) {
        Label label = new Label(msg);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        listaPreferiti.getChildren().setAll(label);
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

        Label nome = new Label(r.getNome());
        nome.setWrapText(true);
        nome.setMaxWidth(130);
        nome.setAlignment(Pos.CENTER);
        nome.setStyle("-fx-text-fill: rgb(47,98,84); -fx-font-weight: bold; -fx-font-size: 14px;");

        tile.getChildren().addAll(imgBox, nome);

        // DA SISTEMARE:
        tile.setOnMouseClicked(ev -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ristorante.fxml"));
                Parent root = loader.load();

                RistoranteController controller = loader.getController();
                controller.setConnectionSocket(socket, in, out);
                controller.setRistorante(r);

                Stage stage = (Stage) tile.getScene().getWindow();
                stage.getScene().setRoot(root);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        

        return tile;
    }
}
