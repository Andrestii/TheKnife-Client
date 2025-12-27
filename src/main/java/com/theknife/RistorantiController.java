package com.theknife;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class RistorantiController {

    @FXML private FlowPane listaRistoranti;

    private Stage stage;
    private Scene scene;
    private Parent root;

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    @FXML
    private void initialize() {
        // Per ora non ci sono ristoranti --> lista vuota
        // Quando li avrai nel DB, qui li caricheremo dinamicamente
    }

    @FXML
    private void onBackClicked(ActionEvent e) throws IOException {
        //App.setRoot("menuRistoratore");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("menuRistoratore.fxml"));
        root = loader.load();

        MenuRistoratoreController controller = loader.getController();
        controller.setConnectionSocket(socket, in, out);

        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    @FXML
    private void onCreaRistoranteClicked(ActionEvent e) throws IOException {
        //App.setRoot("creaRistorante");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("creaRistorante.fxml"));
        root = loader.load();

        CreaRistoranteController controller = loader.getController();
        controller.setConnectionSocket(socket, in, out);

        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);

        // creerai poi la pagina creaRistorante.fxml
    }

    public void setConnectionSocket(Socket socket, ObjectInputStream in, ObjectOutputStream out){
        this.socket = socket;
        this.in = in;
        this.out = out;
    }
}
