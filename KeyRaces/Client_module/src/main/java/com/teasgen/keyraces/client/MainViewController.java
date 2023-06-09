package com.teasgen.keyraces.client;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.net.URL;

import static com.teasgen.keyraces.client.Client.STYLES_FILE_PATH;

/**
 * The controller for the main view.
 * Handles user interactions and navigates to other views.
 */
public class MainViewController {
    @FXML
    public TextField serverAddressField;
    @FXML
    public TextField portField;
    @FXML
    public TextField nameField;
    @FXML
    public Label serverAddressLabel;
    @FXML
    public Label portLabel;
    @FXML
    public Label nameLabel;
    @FXML
    public Button aboutGame;
    @FXML
    public Button playGame;
    private Object lock;
    private String GAME_FXML_PATH="/game-view.fxml";
    private ClientViewModel clientViewModel;
    private InitialViewModel initialViewModel;

    /**
     * Initializes the main view controller.
     */
    @FXML
    private void initialize() {
    }
    /**
     * Handles the event when the Play button is clicked.
     * Notifies the lock object and navigates to the game view.
     *
     * @param actionEvent the action event triggered by the Play button
     * @throws Exception if an error occurs while loading the game view
     */
    @FXML
    public void handlePlayButton(ActionEvent actionEvent) throws Exception {
        synchronized (lock) {
            lock.notifyAll();
        }
        URL url = Client.class.getResource(GAME_FXML_PATH);
        if (url == null) {
            throw new IllegalStateException("Cannot find '" + GAME_FXML_PATH + "'");
        }
        FXMLLoader loader = new FXMLLoader(url);
        FlowPane root = loader.load();
        GameViewController controller = loader.getController();
        controller.setViewModel(clientViewModel);

        Stage window = (Stage) playGame.getScene().getWindow();
        Scene scene = new Scene(root, 1400, 1100);
        scene.getStylesheets().add(Client.class.getResource(STYLES_FILE_PATH).toExternalForm());
        window.setScene(scene);
    }
    /**
     * Handles the event when the About Game button is clicked.
     *
     * @param actionEvent the action event triggered by the About Game button
     */
    @FXML
    public void handleAboutGameButton(ActionEvent actionEvent) {
        final String nameValue = """
                This game was designed by
                Vladislav aka @teasgen Smirnov
                        
                To play - just write the presented text as fast as you can
                """;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(nameValue);
        alert.showAndWait();
    }
    /**
     * Sets the lock object.
     *
     * @param lock the lock object to be set
     */
    public void setLock(Object lock) {
        this.lock = lock;
    }

    /**
     * Sets the view models for the main view.
     *
     * @param clientViewModel  the view model for the client
     * @param initialViewModel the initial view model
     */
    public void setViewModel(ClientViewModel clientViewModel, InitialViewModel initialViewModel) {
        this.clientViewModel = clientViewModel;
        this.initialViewModel = initialViewModel;

        this.serverAddressField.textProperty().bindBidirectional(initialViewModel.addressProperty());
        this.nameField.textProperty().bindBidirectional(initialViewModel.nameProperty());
        this.portField.textProperty().bindBidirectional(initialViewModel.portProperty());
    }
}
