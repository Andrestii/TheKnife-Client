package com.theknife;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("welcome"), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        String resourcePath = fxml + ".fxml";
        System.out.println("Tentativo di caricamento FXML: " + resourcePath);
        var resource = App.class.getResource(resourcePath);
        if (resource == null) {
            System.out.println("ERRORE: Risorsa non trovata: " + resourcePath);
            throw new IOException("Risorsa non trovata: " + resourcePath);
        }
        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        try {
            return fxmlLoader.load();
        } catch (Exception e) {
            System.out.println("ERRORE nel caricamento di " + resourcePath + ": " + e.getMessage());
            throw e;
        }
    }

    public static void main(String[] args) {
        launch();
    }

}