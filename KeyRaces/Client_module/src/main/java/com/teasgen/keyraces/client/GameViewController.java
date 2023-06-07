package com.teasgen.keyraces.client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class GameViewController {
    @FXML
    public Label remainTime;
    @FXML
    public Label tableTimePreview;
    @FXML
    public Label table;
    @FXML
    public TextField inputText;
    @FXML
    public Label myText;

    private ClientViewModel viewModel;
    @FXML
    private void initialize() {
        inputText.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.length() > oldValue.length()) {
                viewModel.increaseTotalNumber();
                int curInx = newValue.length() - 1;
                if (viewModel.getText().charAt(curInx) != newValue.charAt(curInx))
                    viewModel.increaseErrorsNumber();
            }
        });
    }

    public void setViewModel(ClientViewModel viewModel) {
        this.viewModel = viewModel;
        remainTime.textProperty().bind(viewModel.timeProperty());
        table.textProperty().bind(viewModel.tableProperty());
        myText.textProperty().bind(viewModel.textProperty());
        inputText.disableProperty().bind(viewModel.disabledProperty());
    }
}
