package com.teasgen.keyraces.client;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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
    public void handlePlayButton(ActionEvent actionEvent) {
        synchronized (lock) {
            lock.notifyAll();
        }
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
}
