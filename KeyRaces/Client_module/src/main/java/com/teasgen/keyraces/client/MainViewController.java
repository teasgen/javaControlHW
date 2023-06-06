package com.teasgen.keyraces.client;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;

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

    private SimpleStringProperty serverAddressProperty;
    private SimpleStringProperty portProperty;
    private SimpleStringProperty nameProperty;
    private Object lock;
    private String GAME_FXML_PATH="/game-view.fxml";
    private ClientViewModel viewModel;

    @FXML
    private void initialize() {
        serverAddressProperty = new SimpleStringProperty();
        portProperty = new SimpleStringProperty();
        nameProperty = new SimpleStringProperty();

        // Bind the properties to the text field values
        serverAddressProperty.bind(serverAddressField.textProperty());
        portProperty.bind(portField.textProperty());
        nameProperty.bind(nameField.textProperty());

        serverAddressField.textProperty().addListener(((observable, oldValue, newValue) -> {
            String name = !newValue.isBlank() ? newValue : "ALL";
            serverAddressLabel.setText(name);
        }));
    }
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
        AnchorPane root = loader.load();
        GameViewController controller = loader.getController();
        controller.setViewModel(viewModel);

        Stage window = (Stage) playGame.getScene().getWindow();
        window.setScene(new Scene(root, 750, 500));
    }
    @FXML
    public void handleAboutGameButton(ActionEvent actionEvent) {
    }
    public SimpleStringProperty getServerAddressProperty() {
        return serverAddressProperty;
    }
    public SimpleStringProperty getPortProperty() {
        return portProperty;
    }
    public SimpleStringProperty getNameProperty() {
        return nameProperty;
    }

    public void setLock(Object lock) {
        this.lock = lock;
    }

    public void setViewModel(ClientViewModel viewModel) {
        this.viewModel = viewModel;
    }
}
