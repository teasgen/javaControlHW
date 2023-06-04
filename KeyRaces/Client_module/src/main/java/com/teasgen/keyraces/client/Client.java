package com.teasgen.keyraces.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class Client extends Application {
    private static final int PORT = 5619;
    public static final String FXML_FILE_PATH = "/main-view.fxml";
    public static void main(String[] args) {
        ClientHandler clientHandler = new ClientHandler(PORT);
        clientHandler.start();
        Application.launch(Client.class, args);
        Scanner scanner = new Scanner(System.in);
        while (!scanner.hasNext() && !"exit".equals(scanner.next())) {}
        clientHandler.running = false;
    }

    /**
     * @param stage the primary stage
     * @throws Exception if smth went wrong
     */
    @Override
    public void start(Stage stage) throws Exception {
        VBox vBox = loadLayout();
        Scene scene= new Scene(vBox);
        stage.setScene(scene);
        stage.show();
    }
    private static VBox loadLayout() throws IOException {
        URL url = Client.class.getResource(FXML_FILE_PATH);
        if (url == null) {
            throw new IllegalStateException("Cannot find '" + FXML_FILE_PATH + "'");
        }

        return FXMLLoader.load(url);
    }
}