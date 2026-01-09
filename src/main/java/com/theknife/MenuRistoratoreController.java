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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controller del menu ristoratore.
 * Gestisce la navigazione tra le funzionalità disponibili per l'utente
 * ristoratore,
 * come home, gestione ristoranti, impostazioni, recensioni e logout.
 */
public class MenuRistoratoreController {

    private Stage stage;
    private Scene scene;
    private Parent root;
    private Parent previousRoot;

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    @FXML
    private Label userNameLabel;

    @FXML
    private void initialize() {
        userNameLabel.setText(SessioneUtente.getInstance().getNome());
    }

    @FXML
    private void onOverlayClicked(ActionEvent e) throws Exception {
        // App.setRoot("home");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));
        root = loader.load();

        HomeController controller = loader.getController();
        controller.setConnectionSocket(socket, in, out);
        controller.setPreviousRoot(((Node) e.getSource()).getScene().getRoot());

        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    @FXML
    private void onCloseMenuClicked(ActionEvent e) throws Exception {
        // App.setRoot("home");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));
        root = loader.load();

        HomeController controller = loader.getController();
        controller.setConnectionSocket(socket, in, out);
        controller.setPreviousRoot(((Node) e.getSource()).getScene().getRoot());

        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    @FXML
    private void onMieiRistorantiClicked(ActionEvent e) throws Exception {
        // App.setRoot("ristoranti");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ristoranti.fxml"));
        root = loader.load();

        RistorantiController controller = loader.getController();
        controller.setConnectionSocket(socket, in, out);
        controller.setPreviousRoot(((Node) e.getSource()).getScene().getRoot());

        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    @FXML
    private void onImpostazioniClicked(ActionEvent e) throws Exception {
        // App.setRoot("impostazioni");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("impostazioni.fxml"));
        root = loader.load();

        ImpostazioniController controller = loader.getController();
        controller.setConnectionSocket(socket, in, out);
        controller.setPreviousRoot(((Node) e.getSource()).getScene().getRoot());

        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    @FXML
    private void onPreferitiClicked(ActionEvent e) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("preferiti.fxml"));
        root = loader.load();

        PreferitiController controller = loader.getController();
        controller.setConnectionSocket(socket, in, out);
        controller.setPreviousRoot(((Node) e.getSource()).getScene().getRoot());

        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    @FXML
    private void onRecensioniClicked(ActionEvent e) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("myRecensioni.fxml"));
        root = loader.load();

        MyRecensioniController controller = loader.getController();
        controller.setConnectionSocket(socket, in, out);
        controller.setPreviousRoot(((Node) e.getSource()).getScene().getRoot());

        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    @FXML
    private void onLogoutClicked(ActionEvent e) throws Exception {
        System.out.println("Logout eseguito");
        SessioneUtente.getInstance().reset();
        // App.setRoot("welcome");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("welcome.fxml"));
        root = loader.load();

        WelcomeController controller = loader.getController();
        controller.setConnectionSocket(socket, in, out);
        controller.setPreviousRoot(((Node) e.getSource()).getScene().getRoot());

        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    public void setConnectionSocket(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        this.socket = socket;
        this.in = in;
        this.out = out;
    }

    public void setPreviousRoot(Parent previousRoot) {
        this.previousRoot = previousRoot;
    }
}
