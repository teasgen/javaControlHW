package com.teasgen.keyraces.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Client extends Application {
    private static final int PORT = 5619;
    private static final String FXML_FILE_PATH = "/main-view.fxml";
    private static final String STYLES_FILE_PATH = "/style.css";
    private static final Object lock = new Object();
    public static void main(String[] args) {
        Thread fxThread = new Thread(() -> Application.launch(Client.class, args));
        fxThread.start();

        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        ClientHandler clientHandler = new ClientHandler(PORT);
        clientHandler.start();
        while (fxThread.isAlive()) {}
        clientHandler.running = false;
    }

    /**
     * @param stage the primary stage
     * @throws Exception if smth went wrong
     */
    @Override
    public void start(Stage stage) throws Exception {
        URL url = loadLayout();
        FXMLLoader loader = new FXMLLoader(url);
        VBox vBox = loader.load();
        MainViewController controller = loader.getController();

        controller.getServerAddressProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Server Address: " + newValue);
        });
        controller.getPortProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Port: " + newValue);
        });
        controller.getNameProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Name: " + newValue);
        });
        controller.setLock(lock);

        Scene scene = new Scene(vBox);
        scene.getStylesheets().add(Client.class.getResource(STYLES_FILE_PATH).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
    private static URL loadLayout() throws IOException {
        URL url = Client.class.getResource(FXML_FILE_PATH);
        if (url == null) {
            throw new IllegalStateException("Cannot find '" + FXML_FILE_PATH + "'");
        }
        return url;
    }
}