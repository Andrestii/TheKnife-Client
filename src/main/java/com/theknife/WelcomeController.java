package com.theknife;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;

public class WelcomeController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private VBox cardBox;

    @FXML
    private Label titleLabel;

    @FXML
    private VBox linksBox;

    @FXML
    private void onRegisterClicked() throws IOException {
        System.out.println("DEBUG: onRegisterClicked chiamato");
        App.setRoot("registrazione");
    }

    @FXML
    private void onLoginClicked() throws IOException {
        App.setRoot("login");
    }

    @FXML
    private void onGuestClicked() throws IOException {
        App.setRoot("guest");
    }

    @FXML
    public void initialize() {
        // Make the white card size to its content instead of stretching full-width.
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
}
