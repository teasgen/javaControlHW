package com.teasgen.keyraces.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;


/**
 * The main class for the client application.
 * Starts the JavaFX application, handles client lifecycle, and sets up the UI.
 */
public class Client extends Application {

    private static final String FXML_FILE_PATH = "/main-view.fxml";
    public static final String STYLES_FILE_PATH = "/style.css";
    private static final Object lock = new Object();
    private static final ClientViewModel clientViewModel = new ClientViewModel();
    private static final InitialViewModel initialViewModel = new InitialViewModel();

    /**
     * The entry point of the client application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Thread fxThread = new Thread(() -> Application.launch(Client.class, args));
        fxThread.start();

        while (fxThread.isAlive() && !initialViewModel.isStart()) {
            // Wait until the user press game button
        }

        initialViewModel.fillBlankOrIncorrectValues();
        while (fxThread.isAlive()) {
            Platform.runLater(clientViewModel::reset);
            ClientHandler clientHandler = new ClientHandler(
                    initialViewModel.getAddress(),
                    Integer.parseInt(initialViewModel.getPort()),
                    initialViewModel.getName(),
                    clientViewModel
            );

            clientHandler.start();
            while (fxThread.isAlive() && !clientViewModel.wantTryAgainProperty().get()) {
                // Wait for user try again action or JavaFX application to exit
            }
            clientHandler.close();
            if (fxThread.isAlive()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Starts the JavaFX application and sets up the primary stage.
     *
     * @param stage the primary stage
     * @throws Exception if an exception occurs during application startup
     */
    @Override
    public void start(Stage stage) throws Exception {
        URL url = loadLayout();
        FXMLLoader loader = new FXMLLoader(url);
        VBox vBox = loader.load();
        MainViewController controller = loader.getController();

        controller.setLock(lock);
        controller.setViewModel(clientViewModel, initialViewModel);

        Scene scene = new Scene(vBox);
        scene.getStylesheets().add(Client.class.getResource(STYLES_FILE_PATH).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Loads the FXML layout file for the main view.
     *
     * @return the URL of the loaded layout file
     * @throws IllegalStateException if the layout file cannot be found
     */
    private static URL loadLayout() {
        URL url = Client.class.getResource(FXML_FILE_PATH);
        if (url == null) {
            throw new IllegalStateException("Cannot find '" + FXML_FILE_PATH + "'");
        }
        return url;
    }
}
