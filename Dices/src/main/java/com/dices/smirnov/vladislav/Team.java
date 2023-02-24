package com.dices.smirnov.vladislav;

import java.util.ArrayList;
import java.util.List;

/**
 * Class stores several players
 * @author Vlad Smirnov
 */
public class Team {
    /**
     * The team name
     */
    private final String label;
    /**
     * List of team players
     */
    private final List<Player> squad = new ArrayList<>();

    /**
     * Team name getter
     * @return {@link Team#label}
     */
    public String getLabel() { return label; }

    /**
     * Team players getter
     * @return {@link Team#squad}
     */
    public List<Player> getSquad() { return squad; }

    /**
     * Constructor
     * @param s team name
     */
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
