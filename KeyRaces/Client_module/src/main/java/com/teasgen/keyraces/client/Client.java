package com.teasgen.keyraces.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Date;

public class Client extends Application {
    private static final String FXML_FILE_PATH = "/main-view.fxml";
    private static final String STYLES_FILE_PATH = "/style.css";
    private static final Object lock = new Object();
    private static final ClientViewModel clientViewModel = new ClientViewModel();
    private static final InitialViewModel initialViewModel = new InitialViewModel();
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

        initialViewModel.fillBlankOrIncorrectValues();
        while (fxThread.isAlive()) {
            Platform.runLater(clientViewModel::reset);
            System.out.println("New");
            ClientHandler clientHandler = new ClientHandler(
                    initialViewModel.getAddress(),
                    Integer.parseInt(initialViewModel.getPort()),
                    initialViewModel.getName() + new Date(),
                    clientViewModel
            );

            clientHandler.start();
            while (fxThread.isAlive() && !clientViewModel.wantTryAgainProperty().get()) {}
            clientHandler.close();
            System.out.println("Close");
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
     * @param stage the primary stage
     * @throws Exception if smth went wrong
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
    private static URL loadLayout() {
        URL url = Client.class.getResource(FXML_FILE_PATH);
        if (url == null) {
            throw new IllegalStateException("Cannot find '" + FXML_FILE_PATH + "'");
        }
        return url;
    }
}