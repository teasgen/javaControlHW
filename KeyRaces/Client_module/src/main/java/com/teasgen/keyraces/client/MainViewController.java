package com.teasgen.keyraces.client;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class MainViewController {
    @FXML
    public TextField nameHost;
    @FXML
    public Text inputHost;
    @FXML
    private void initialize() {
        inputHost.setText("Kek");
        nameHost.textProperty().addListener(((observable, oldValue, newValue) -> {
            String name = !newValue.isBlank() ? newValue : "ALL";
            inputHost.setText(name);
        }));
    }
}
