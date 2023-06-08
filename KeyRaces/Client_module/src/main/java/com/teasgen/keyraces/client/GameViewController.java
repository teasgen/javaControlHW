package com.teasgen.keyraces.client;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

public class GameViewController {
    @FXML
    public Label remainTime;
    @FXML
    public Label tableTimePreview;
    @FXML
    public Label table;
    @FXML
    public TextArea inputText;
    @FXML
    public Label myText;
    @FXML
    public Button tryAgain;
    @FXML
    public Label textLabel;
    @FXML
    public Label warningsAboutText;
    private boolean lastMistake;

    private ClientViewModel viewModel;
    @FXML
    private void initialize() {
        tryAgain.setVisible(false);
        myText.setAlignment(Pos.TOP_LEFT);
        inputText.clear();
        tableTimePreview.setText("Players");
        lastMistake = false;
        inputText.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.length() > oldValue.length()) {
                int curInx = newValue.length() - 1;
                if (curInx >= viewModel.getText().length() || !viewModel.getText().substring(0, curInx + 1).equals(newValue)) {
                    if (!lastMistake)
                        viewModel.increaseErrorsNumber();
                    lastMistake = true;
                    warningsAboutText.setText("YOU MADE A MISTAKE!");
                }
                else {
                    lastMistake = false;
                    viewModel.increaseTotalNumber();
                    warningsAboutText.setText("All right");
                }
            } else {
                int curInx = oldValue.length() - 1;
                if (curInx < viewModel.getText().length() && viewModel.getText().substring(0, curInx + 1).equals(oldValue)) {
                    viewModel.decreaseTotalNumber();
                }
            }
        });
        textLabel.textProperty().addListener((observableValue, s, t1) -> {
            if ("Game over".equals(t1)) {
                tableTimePreview.setText("");
                remainTime.setText("");
                tryAgain.setVisible(true);
                warningsAboutText.setText("");
                myText.setAlignment(Pos.valueOf("CENTER"));
                tableTimePreview.setText("Final table");
            }
        });
        tryAgain.setOnMouseClicked(event -> {
            viewModel.setWantTryAgain(true);
            initialize();
        });
    }

    public void setViewModel(ClientViewModel viewModel) {
        this.viewModel = viewModel;
        remainTime.textProperty().bindBidirectional(viewModel.timeProperty());
        table.textProperty().bind(viewModel.tableProperty());
        myText.textProperty().bind(viewModel.textProperty());
        inputText.disableProperty().bind(viewModel.disabledProperty());
        textLabel.textProperty().bind(viewModel.endgameProperty());
    }
}
