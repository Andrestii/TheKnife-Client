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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe principale dell'applicazione client TheKnife.
 * Avvia l'interfaccia grafica JavaFX, inizializza la connessione
 * con il server e passa il socket al controller iniziale.
 */
public class App extends Application {

    private static Scene scene;

    /**
     * Metodo di avvio dell'applicazione JavaFX.
     * Carica la schermata iniziale, crea la connessione al server
     * e inizializza il controller con gli stream di comunicazione.
     *
     * @param stage stage principale dell'applicazione
     * @throws IOException se il caricamento dell'FXML fallisce
     */
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("welcome.fxml"));

        scene = new Scene(fxmlLoader.load(), 640, 480);
        stage.setTitle("TheKnife");
        stage.setScene(scene);

        InetAddress addr = InetAddress.getByName(null);
        System.out.println("addr = " + addr);
        Socket socket = new Socket(addr, 2345);

        try {
            System.out.println("Client connected: socket = " + socket);

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            WelcomeController controller = fxmlLoader.getController();
            controller.setConnectionSocket(socket, in, out);
        } catch (IOException e) {

        }

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