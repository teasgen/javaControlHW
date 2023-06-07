package com.teasgen.keyraces.client;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ClientViewModel {
    private final StringProperty time = new SimpleStringProperty();
    private final StringProperty table = new SimpleStringProperty();
    private final StringProperty text = new SimpleStringProperty();
    private final BooleanProperty disabled = new SimpleBooleanProperty();
    private int totalNumber;
    private int errorsNumber;

    public ClientViewModel() {
        time.set("Soon start");
        totalNumber = 0;
        errorsNumber = 0;
        disabled.set(true);
    }

    public void setDisabled(Boolean disabled) {
        this.disabled.set(disabled);
    }

    public BooleanProperty disabledProperty() {
        return disabled;
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

    public int getErrorsNumber() {
        return errorsNumber;
    }

    public void increaseErrorsNumber() {
        this.errorsNumber++;
    }

}
