package com.teasgen.keyraces.client;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class InitialViewModel {
    public static final int PORT = 5619;
    public static final String ADDRESS = "localhost";
    public static final String NAME = "Vlad";
    private final StringProperty address = new SimpleStringProperty();
    private final StringProperty port = new SimpleStringProperty();
    private final StringProperty name = new SimpleStringProperty();
    public InitialViewModel() {}

    public StringProperty addressProperty() {
        return address;
    }
    public StringProperty portProperty() {
        return port;
    }
    public StringProperty nameProperty() {
        return name;
    }
    public String getAddress() {
        return address.get();
    }

    public String getName() {
        return name.get();
    }

    public String getPort() {
        return port.get();
    }
    private static Integer parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    public void fillBlankOrIncorrectValues() {
        if (getAddress() == null)
            address.set(ADDRESS);

        Integer portInt = parseInt(getPort());
        if (portInt == null)
            portInt = InitialViewModel.PORT;
        port.set(String.valueOf(portInt));

        if (getName() == null)
            name.set(NAME);
    }
}
