package com.dices.smirnov.vladislav;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private final String label;
    private final List<Player> squad = new ArrayList<>();
    public String getLabel() { return label; }
    public List<Player> getSquad() { return squad; }
    Team(String s) {
        label = s;
    }

    @Override
    public String toString() {
        return "Team{" +  "label='" + label + "'}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass())
            return false;
        return label.equals(((Team) obj).getLabel());
    }
}
