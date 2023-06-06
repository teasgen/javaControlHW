package com.teasgen.keyraces.client;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.Timer;
import java.util.TimerTask;

public class GameViewController {
    @FXML
    public Label remainTime;

    private ClientViewModel viewModel;
    @FXML
    private void initialize() {
    }

    public void setViewModel(ClientViewModel viewModel) {
        this.viewModel = viewModel;
        remainTime.textProperty().bind(viewModel.timeProperty());
    }
}
