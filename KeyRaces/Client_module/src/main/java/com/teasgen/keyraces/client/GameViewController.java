package com.teasgen.keyraces.client;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

/**
 * The controller for the game view.
 * Handles user interactions and updates the view based on the view model.
 */
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

    /**
     * Reset the scene to its initial state
     */
    private void clearScene() {
        tryAgain.setVisible(false);
        myText.setAlignment(Pos.CENTER);
        inputText.clear();
        tableTimePreview.setText("Players");
        lastMistake = false;
    }

    /**
     * Initializes the game view controller.
     * Sets up event handlers and binds properties between the view and the view model.
     */
    @FXML
    private void initialize() {
        lastMistake = false;
        tryAgain.setVisible(false);
        myText.setAlignment(Pos.CENTER);
        inputText.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.length() > oldValue.length()) {
                int curInx = newValue.length() - 1;
                if (curInx >= viewModel.getText().length() || !viewModel.getText().substring(0, curInx + 1).equals(newValue)) {
                    if (!lastMistake)
                        viewModel.increaseErrorsNumber();
                    lastMistake = true;
                    warningsAboutText.setText("YOU'VE MADE A MISTAKE!");
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
                myText.setAlignment(Pos.CENTER);
                tableTimePreview.setText("Final table");
            }
        });
        tryAgain.setOnMouseClicked(event -> {
            viewModel.setWantTryAgain(true);
            clearScene();
        });
        myText.textProperty().addListener(((observableValue, s, t1) -> {
            if (t1 != null && !t1.startsWith("Starts in")
                    && textLabel != null && !"Game over".equals(textLabel.getText()) && textLabel.getText() != null) {
                myText.setAlignment(Pos.TOP_LEFT);
//                System.out.println("HERE: {" + t1 + "}{" + textLabel.getText() + "};");
            }
        }));
    }

    /**
     * Sets the view model for the game view.
     * According to MVVM binds view model properties to the GUI properties
     * @param viewModel the view model to be set
     */
    public void setViewModel(ClientViewModel viewModel) {
        this.viewModel = viewModel;
        remainTime.textProperty().bindBidirectional(viewModel.timeProperty());
        table.textProperty().bind(viewModel.tableProperty());
        myText.textProperty().bind(viewModel.textProperty());
        textLabel.textProperty().bind(viewModel.endgameProperty());

        inputText.disableProperty().bind(viewModel.disabledProperty());

        inputText.visibleProperty().bind(viewModel.showAllProperty());
        textLabel.visibleProperty().bind(viewModel.showAllProperty());
        table.visibleProperty().bind(viewModel.showAllProperty());
        remainTime.visibleProperty().bind(viewModel.showAllProperty());
        tableTimePreview.visibleProperty().bind(viewModel.showAllProperty());
        warningsAboutText.visibleProperty().bind(viewModel.showAllProperty());
    }
}
