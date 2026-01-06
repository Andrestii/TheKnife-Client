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
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class WelcomeController {

    @FXML private BorderPane rootPane;
    @FXML private VBox cardBox;
    @FXML private Label titleLabel;
    @FXML private VBox linksBox;

    private Stage stage;
    private Parent root;
    private Parent previousRoot;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    @FXML
    private void onRegisterClicked(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("registrazione.fxml"));
        root = loader.load();

        RegistrazioneController controller = loader.getController();
        controller.setConnectionSocket(socket, in, out);
        controller.setPreviousRoot(((Node)e.getSource()).getScene().getRoot());

        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    @FXML
    private void onLoginClicked(ActionEvent e) throws IOException {
        System.out.println(socket); // Per testare se il socket è passato correttamente

        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        root = loader.load();

        LoginController controller = loader.getController();
        controller.setConnectionSocket(socket, in, out);
        controller.setPreviousRoot(((Node)e.getSource()).getScene().getRoot());

        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    @FXML
    private void onGuestClicked(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("guest.fxml"));
        root = loader.load();

        GuestController controller = loader.getController();
        controller.setConnectionSocket(socket, in, out);
        controller.setPreviousRoot(((Node)e.getSource()).getScene().getRoot());

        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    @FXML
    public void initialize() {
        if (cardBox != null) {
            // Prevent VBox from forcing the card to grow
            VBox.setVgrow(cardBox, Priority.NEVER);

            // Let the card compute its preferred width from children
            cardBox.setPrefWidth(Region.USE_COMPUTED_SIZE);

            // Ensure the card doesn't expand beyond its preferred size
            cardBox.setMaxWidth(Region.USE_PREF_SIZE);

            // Optional minimum for usability
            cardBox.setMinWidth(200);
        }
    }

    public void setConnectionSocket(Socket socket, ObjectInputStream in, ObjectOutputStream out){
        this.socket = socket;
        this.in = in;
        this.out = out;
    }
    
    public void setPreviousRoot(Parent previousRoot) {
        this.previousRoot = previousRoot;
    }
}
