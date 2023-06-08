package com.teasgen.keyraces.client;

import javafx.beans.property.*;

public class ClientViewModel {
    private final StringProperty time = new SimpleStringProperty();
    private final StringProperty table = new SimpleStringProperty();
    private final StringProperty text = new SimpleStringProperty();
    private final BooleanProperty disabled = new SimpleBooleanProperty();
    private final StringProperty endgame = new SimpleStringProperty();
    private final BooleanProperty wantTryAgain = new SimpleBooleanProperty();
    private final BooleanProperty showAll = new SimpleBooleanProperty();
    private int totalNumber;
    private int errorsNumber;
    public ClientViewModel() {
        time.set("Soon start");
        totalNumber = 0;
        errorsNumber = 0;
        disabled.set(true);
        wantTryAgain.set(false);
        showAll.set(false);
    }

    public void setDisabled(Boolean disabled) {
        this.disabled.set(disabled);
    }

    public BooleanProperty disabledProperty() {
        return disabled;
    }
    public void setWantTryAgain(Boolean wantTryAgain) {
        this.wantTryAgain.set(wantTryAgain);
    }

    public BooleanProperty wantTryAgainProperty() {
        return wantTryAgain;
    }
    public void setTime(String time) {
        this.time.set(time);
    }

    public StringProperty timeProperty() {
        return time;
    }

    public StringProperty tableProperty() {
        return table;
    }

    public void setTable(String table) {
        this.table.set(table);
    }

    public void setText(String text) {
        this.text.set(text);
    }

    public StringProperty textProperty() {
        return text;
    }

    public String getText() {
        return text.get();
    }

    public int getTotalNumber() {
        return totalNumber;
    }

    public void increaseTotalNumber() {
        this.totalNumber++;
    }

    public void decreaseTotalNumber() {
        this.totalNumber--;
    }

    public int getErrorsNumber() {
        return errorsNumber;
    }
    public void increaseErrorsNumber() {
        this.errorsNumber++;
    }

    public void setEndgame(String endgame) {
        this.endgame.set(endgame);
    }
    public StringProperty endgameProperty() {
        return endgame;
    }
    public void setShowAll(boolean showAll) {
        this.showAll.set(showAll);
    }

    public BooleanProperty showAllProperty() {
        return showAll;
    }
    public void reset() {
        time.set("Soon start");
        totalNumber = 0;
        errorsNumber = 0;
        disabled.set(true);
        wantTryAgain.set(false);
        showAll.set(false);
        table.set("");
        text.set("");
        endgame.set("Text");
    }
}
