package com.theknife;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class RisultatiRistorantiController {

    @FXML private FlowPane listaRisultati;

    private Stage stage;
    private Parent root;
    private Parent previousRoot;

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private final Object ioLock = new Object();
    private final Set<Integer> favoriteIds = ConcurrentHashMap.newKeySet();

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
    
    public void setPreviousRoot(Parent previousRoot) {
        this.previousRoot = previousRoot;
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

        Platform.runLater(this::loadFavoritesThenResults);
    }

    @FXML
    private void onBackClicked(ActionEvent e) throws IOException {
        try {
            Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            if (previousRoot != null) stage.getScene().setRoot(previousRoot);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadResults() {
        if (out == null || in == null) {
            showEmptyMessage("Connessione non disponibile");
            return;
        }

        try {
            listaRisultati.getChildren().clear();

            synchronized (ioLock) {
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
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            showEmptyMessage("Errore nel caricamento dei risultati");
        }
    }


    private void loadFavoritesThenResults() {
        // Se guest: niente icone, ma carico comunque i risultati
        if (SessioneUtente.getInstance().getRuolo() == Ruolo.GUEST) {
            loadResults();
            return;
        }

        new Thread(() -> {
            try {
                String username = SessioneUtente.getInstance().getUsername();

                synchronized (ioLock) {
                    out.writeObject("listFavorites");
                    out.writeObject(username);
                    out.flush();

                    Object obj = in.readObject();
                    if (obj instanceof ServerResponse) {
                        ServerResponse resp = (ServerResponse) obj;
                        if ("OK".equals(resp.getStatus())) {
                            @SuppressWarnings("unchecked")
                            List<Ristorante> list = (List<Ristorante>) resp.getPayload();

                            favoriteIds.clear();
                            if (list != null) {
                                for (Ristorante r : list) favoriteIds.add(r.getId());
                            }
                        }
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            Platform.runLater(this::loadResults);
        }).start();
    }

    private void showEmptyMessage(String msg) {
        Label label = new Label(msg);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        listaRisultati.getChildren().setAll(label);
    }

    private StackPane createRestaurantTile(Ristorante r) {
        HBox tile = new HBox(12);
        tile.setAlignment(Pos.CENTER_LEFT);
        tile.setPadding(new Insets(12));
        tile.setPrefSize(600, 200);
        tile.setMaxSize(600, 200);
        tile.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-radius: 12;" +
            "-fx-border-color: rgb(47,98,84);" +
            "-fx-border-width: 3;"
        );

        // Immagine a sinistra
        ImageView img = new ImageView(new Image(getClass().getResourceAsStream("restaurant.png")));
        img.setFitWidth(200);
        img.setFitHeight(180);
        img.setPreserveRatio(true);

        StackPane imgBox = new StackPane(img);
        imgBox.setPrefSize(200, 180);
        imgBox.setMaxSize(200, 180);
        imgBox.setStyle(
            "-fx-background-color: #f2f2f2;" +
            "-fx-background-radius: 10;" +
            "-fx-border-radius: 10;" +
            "-fx-border-color: #cccccc;" +
            "-fx-border-width: 1;"
        );

        // Testi a destra
        Label nomeLbl = new Label(r.getNome());
        nomeLbl.setWrapText(true);
        nomeLbl.setMaxWidth(380);
        nomeLbl.setStyle("-fx-text-fill: rgb(47,98,84); -fx-font-weight: bold; -fx-font-size: 25px;");

        Label indirizzoLbl = new Label(r.getIndirizzo());
        indirizzoLbl.setWrapText(true);
        indirizzoLbl.setMaxWidth(380);
        indirizzoLbl.setStyle("-fx-text-fill: #444; -fx-font-size: 20px;");

        Label cucinaLbl = new Label(capitalizeFirst(r.getTipoCucina()));
        cucinaLbl.setWrapText(true);
        cucinaLbl.setMaxWidth(380);
        cucinaLbl.setStyle("-fx-text-fill: #666; -fx-font-size: 20px; -fx-font-style: italic;");

        VBox textBox = new VBox(20, nomeLbl, indirizzoLbl, cucinaLbl);
        textBox.setAlignment(Pos.CENTER_LEFT);

        tile.getChildren().addAll(imgBox, textBox);

        tile.setOnMouseClicked(ev -> {
            System.out.println("[CLIENT] Cliccato risultato: " + r.getId() + " - " + r.getNome());
        });

        // --- Wrapper to overlay the favorite icon ---
        StackPane wrapper = new StackPane();
        wrapper.setPrefSize(600, 200);
        wrapper.setMaxSize(600, 200);

        wrapper.getChildren().add(tile); // aggiungi sempre il tile

        // Mostra icona SOLO se non sei guest
        if (SessioneUtente.getInstance().getRuolo() != Ruolo.GUEST) {
            ImageView favIcon = new ImageView();
            favIcon.setFitWidth(28);
            favIcon.setFitHeight(28);
            favIcon.setPreserveRatio(true);
            favIcon.setStyle("-fx-cursor: hand;");

            StackPane.setAlignment(favIcon, Pos.TOP_RIGHT);
            StackPane.setMargin(favIcon, new Insets(10, 10, 0, 0));

            // 1) icona iniziale: bianca se NON è nei preferiti, rossa se lo è
            boolean isFav = favoriteIds.contains(r.getId());
            setFavoriteIconImage(favIcon, isFav);

            // 2) click: toggle add/remove + aggiorna icona
            favIcon.setOnMouseClicked(ev -> {
                ev.consume();
                toggleFavorite(r.getId(), favIcon);
            });
            wrapper.getChildren().add(favIcon);
        }

        tile.setOnMouseClicked(ev -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ristorante.fxml"));
                Parent root = loader.load();

                RistoranteController controller = loader.getController();
                RisultatiRistorantiController self = this;
                controller.setConnectionSocket(socket, in, out);
                controller.setPreviousRoot(((Node)ev.getSource()).getScene().getRoot());
                controller.setRistorante(r);
                controller.setOnBackRefresh(() -> self.refreshFavoriteState());

                Stage stage = (Stage) tile.getScene().getWindow();
                stage.getScene().setRoot(root);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        return wrapper;
    }

    private String capitalizeFirst(String s) {
        if (s == null) return "-";
        s = s.trim();
        if (s.isEmpty()) return "-";
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private void setFavoriteIconImage(ImageView icon, boolean isFavorite) {
        String file = isFavorite ? "favoriteIconRed.png" : "favoriteIconWhite.png";
        icon.setImage(new Image(getClass().getResourceAsStream(file)));
    }

    private void toggleFavorite(int idRistorante, ImageView icon) {
        boolean currentlyFav = favoriteIds.contains(idRistorante);
        String command = currentlyFav ? "removeFavorite" : "addFavorite";
        String username = SessioneUtente.getInstance().getUsername();

        new Thread(() -> {
            try {
                synchronized (ioLock) {
                    out.writeObject(command);
                    out.writeObject(username);
                    out.writeObject(idRistorante);
                    out.flush();

                    Object obj = in.readObject();
                    if (obj instanceof ServerResponse) {
                        ServerResponse resp = (ServerResponse) obj;
                        if ("OK".equals(resp.getStatus())) {
                            if (currentlyFav) favoriteIds.remove(idRistorante);
                            else favoriteIds.add(idRistorante);

                            Platform.runLater(() -> setFavoriteIconImage(icon, !currentlyFav));
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }
    
    public void refreshFavoriteState() {
        Platform.runLater(this::loadFavoritesThenResults);
    }


}
