package com.theknife;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class RistoranteController {

    @FXML private ImageView imgRistorante;

    @FXML private Label lblNome;
    @FXML private Label lblTipoCucina;
    @FXML private Label lblIndirizzo;
    @FXML private Label lblCittaNazione;
    @FXML private Label lblLatLon;
    @FXML private Label lblPrezzo;
    @FXML private Label lblPrenotazione;
    @FXML private Label lblDelivery;

    @FXML private Button btnScriviRecensione;
    @FXML private Button btnPreferiti;
    @FXML private Label lblStatus;

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private Ristorante ristorante;
    private Parent previousRoot;

    private boolean isFavorite = false;
    private boolean hasReviewed = false;
    private boolean isOwner = false;

    @FXML
    private void initialize() {
        imgRistorante.setImage(new Image(getClass().getResourceAsStream("restaurant.png")));
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
        renderRistorante();

        applyUiPolicy(); // applica guest/non-guest subito

        Platform.runLater(() -> {
            syncOwnerFromServer(); // setta isOwner
            applyUiPolicy();       // riapplica con isOwner aggiornato
            syncButtonsFromServer(); // preferiti/recensione (se li usi)
        });
    }

    public void refreshReviewAndFavoriteState() {
        // reset opzionale UI mentre carichi
        // setStatus("");

        Platform.runLater(() -> {
            syncOwnerFromServer();
            applyUiPolicy();
            syncButtonsFromServer();   // rilegge hasReviewed/isFavorite e aggiorna testo bottoni
        });
    }

    private void renderRistorante() {
        if (ristorante == null) return;

        lblNome.setText(nvl(ristorante.getNome(), "-"));
        lblTipoCucina.setText(nvl(capitalizeFirst(ristorante.getTipoCucina()), "Tipologia non specificata"));
        lblIndirizzo.setText(nvl(ristorante.getIndirizzo(), "Indirizzo non disponibile"));

        lblCittaNazione.setText(nvl(capitalizeFirst(ristorante.getCitta()), "-") + ", " + nvl(capitalizeFirst(ristorante.getNazione()), "-"));

        // Nella tua classe sono double primitivi: mostro sempre
        lblLatLon.setText(String.format("%.6f, %.6f", ristorante.getLat(), ristorante.getLon()));

        lblPrezzo.setText(ristorante.getPrezzo() + " €");

        lblPrenotazione.setText(ristorante.isPrenotazione() ? "Prenotazione disponibile" : "Prenotazione non disponibile");
        lblDelivery.setText(ristorante.isDelivery() ? "Delivery disponibile" : "Delivery non disponibile");

        updateButtonsText();
    }

    private void syncButtonsFromServer() {
        if (out == null || in == null || ristorante == null) return;

        try {
            String username = SessioneUtente.getInstance().getUsername();

            // 1) isFavorite
            out.writeObject("isFavorite");
            out.writeObject(username);
            out.writeObject(ristorante.getId());
            out.flush();

            Object obj1 = in.readObject();
            if (obj1 instanceof ServerResponse && "OK".equals(((ServerResponse)obj1).getStatus())) {
                Object payload = ((ServerResponse)obj1).getPayload();
                if (payload instanceof Boolean) isFavorite = (Boolean) payload;
            }

            // 2) hasReviewed
            out.writeObject("hasReviewed");
            out.writeObject(username);
            out.writeObject(ristorante.getId());
            out.flush();

            Object obj2 = in.readObject();
            if (obj2 instanceof ServerResponse && "OK".equals(((ServerResponse)obj2).getStatus())) {
                Object payload = ((ServerResponse)obj2).getPayload();
                if (payload instanceof Boolean) hasReviewed = (Boolean) payload;
            }

            updateButtonsText();

        } catch (Exception e) {
            e.printStackTrace();
            setStatus("Impossibile sincronizzare stato recensione/preferiti");
        }
    }

    private void updateButtonsText() {
        btnPreferiti.setText(isFavorite ? "Rimuovi dai preferiti" : "Aggiungi ai preferiti");
        btnScriviRecensione.setText(hasReviewed ? "Modifica recensione" : "Scrivi recensione");
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

    @FXML
    private void onViewReviewsClicked(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("recensioni.fxml"));
            Parent root = loader.load();

            RecensioniController controller = loader.getController();
            controller.setConnectionSocket(socket, in, out);
            controller.setRistorante(ristorante);
            controller.setPreviousRoot(((Node)e.getSource()).getScene().getRoot());

            Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);

        } catch (Exception ex) {
            ex.printStackTrace();
            setStatus("Errore apertura recensioni");
        }
    }

    @FXML
    private void onWriteOrEditReviewClicked(ActionEvent e) {
        if (ristorante == null) return;

        Ruolo ruolo = SessioneUtente.getInstance().getRuolo();
        if (ruolo == null || ruolo == Ruolo.GUEST) return;
        if (ruolo == Ruolo.RISTORATORE && isOwner) return;


        try {
            String fxml = hasReviewed ? "editRecensione.fxml" : "creaRecensione.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            Object ctrl = loader.getController();
            RistoranteController self = this;
            if (ctrl instanceof CreaRecensioneController) {
                CreaRecensioneController c = (CreaRecensioneController) ctrl;
                c.setConnectionSocket(socket, in, out);
                c.setRistorante(ristorante);
                c.setPreviousRoot(((Node)e.getSource()).getScene().getRoot());
                c.setOnBackRefresh(() -> self.refreshReviewAndFavoriteState());
            } else if (ctrl instanceof EditRecensioneController) {
                EditRecensioneController c = (EditRecensioneController) ctrl;
                c.setConnectionSocket(socket, in, out);
                c.setRistorante(ristorante);
                c.setPreviousRoot(((Node)e.getSource()).getScene().getRoot());
                c.setOnBackRefresh(() -> self.refreshReviewAndFavoriteState());
            }


            Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);

        } catch (Exception ex) {
            ex.printStackTrace();
            setStatus("Errore apertura pagina recensione");
        }
    }

    @FXML
    private void onToggleFavoriteClicked(ActionEvent e) {
        if (ristorante == null || out == null || in == null) return;

        Ruolo ruolo = SessioneUtente.getInstance().getRuolo();
        if (ruolo == null || ruolo == Ruolo.GUEST) return;


        try {
            String username = SessioneUtente.getInstance().getUsername();

            if (!isFavorite) {
                out.writeObject("addFavorite");
                out.writeObject(username);
                out.writeObject(ristorante.getId());
                out.flush();

                Object obj = in.readObject();
                if (obj instanceof ServerResponse && "OK".equals(((ServerResponse)obj).getStatus())) {
                    isFavorite = true;
                    setStatus("Aggiunto ai preferiti");
                } else if (obj instanceof ServerResponse) {
                    setStatus(String.valueOf(((ServerResponse)obj).getPayload()));
                }
            } else {
                out.writeObject("removeFavorite");
                out.writeObject(username);
                out.writeObject(ristorante.getId());
                out.flush();

                Object obj = in.readObject();
                if (obj instanceof ServerResponse && "OK".equals(((ServerResponse)obj).getStatus())) {
                    isFavorite = false;
                    setStatus("Rimosso dai preferiti");
                } else if (obj instanceof ServerResponse) {
                    setStatus(String.valueOf(((ServerResponse)obj).getPayload()));
                }
            }

            updateButtonsText();

        } catch (Exception ex) {
            ex.printStackTrace();
            setStatus("Errore aggiornando i preferiti");
        }
    }

    private void setStatus(String msg) {
        lblStatus.setText(msg == null ? "" : msg);
    }

    private static String nvl(String s, String def) { // Null Value Logic
        return (s == null || s.trim().isEmpty()) ? def : s;
    }

    private void hideBtn(Node n) {
        n.setVisible(false);
        n.setManaged(false);
    }

    private void showBtn(Node n) {
        n.setVisible(true);
        n.setManaged(true);
    }

    private void syncOwnerFromServer() { // Chiede al server “sono owner di questo ristorante?”
        if (out == null || in == null || ristorante == null) return;

        Ruolo ruolo = SessioneUtente.getInstance().getRuolo();
        if (ruolo == null || ruolo == Ruolo.GUEST) return;

        if (ruolo != Ruolo.RISTORATORE) return;

        try {
            String username = SessioneUtente.getInstance().getUsername();
            if (username == null || username.trim().isEmpty()) return;

            out.writeObject("isOwnerOfRestaurant");
            out.writeObject(username);
            out.writeObject(ristorante.getId());
            out.flush();

            Object obj = in.readObject();
            if (obj instanceof ServerResponse && "OK".equals(((ServerResponse) obj).getStatus())) {
                Object payload = ((ServerResponse) obj).getPayload();
                if (payload instanceof Boolean) isOwner = (Boolean) payload;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void applyUiPolicy() {
        Ruolo ruolo = SessioneUtente.getInstance().getRuolo();

        boolean isGuest = (ruolo == null || ruolo == Ruolo.GUEST);
        if (isGuest) {
            hideBtn(btnScriviRecensione);
            hideBtn(btnPreferiti);
            return;
        }

        // default visibili
        showBtn(btnScriviRecensione);
        showBtn(btnPreferiti);

        // ristoratore: se è owner del ristorante, niente recensione
        if (ruolo == Ruolo.RISTORATORE && isOwner) {
            hideBtn(btnScriviRecensione);
        }
    }

    private String capitalizeFirst(String s) {
        if (s == null) return "-";
        s = s.trim();
        if (s.isEmpty()) return "-";
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

}