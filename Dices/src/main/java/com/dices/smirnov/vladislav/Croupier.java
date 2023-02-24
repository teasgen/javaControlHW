package com.dices.smirnov.vladislav;

import java.util.*;

/**
 * The croupier
 * <p>Can manage teams and control result table</p>
 * @author Vlad Smirnov
 */
public class Croupier {
    /**
     * All teams
     */
    private final List<Team> teams = new ArrayList<>();
    /**
     * Result table - key is team label, value is sum of players scores
     */
    private final Map<String, Integer> resultTable = new HashMap<>();

    /**
     * Player setter
     * @param playerName {@link Player#getPlayerName()}
     * @param teamLabel  {@link Team#getLabel()}
     * @return player instance
     */
    public Player setPlayer(String playerName, String teamLabel) {
        Player player = new Player(playerName, teamLabel);
        try {
            teams.get(teams.indexOf(new Team(teamLabel))).getSquad().add(player);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Team with label: " + teamLabel + " doesn't exist");
        }
        return player;
    }

    /**
     * Team setter
     * @param label {@link Team#getLabel()}
     */
    public void setTeam(String label) {
        Team team = new Team(label);
        teams.add(team);
    }

    /**
     * @param label {@link Team#getLabel()}
     * @return team with current label
     */
    public synchronized Team getTeam(String label) {
        for (Team team : teams)
            if (team.getLabel().equals(label))
                return team;
        System.out.println("Team with label: " + label + " doesn't exist");
        return null;
    }

    /**
     * @return {@link Croupier#teams}
     */
    public List<Team> getTeams() {
        return Collections.unmodifiableList(teams);
    }

    /**
     * @return {@link Croupier#resultTable}
     */
    public Map<String, Integer> getResultTable() {
        return resultTable;
    }

    /**
     * Clear all variables
     */
    public void clear() {
        teams.clear();
        resultTable.clear();
    }

    /**
     * @return list of teams with the highest score
     */
    public List<String> findWinners() {
        Integer max = -1;
        List<String> winners = new ArrayList<>();
        for (Map.Entry<String, Integer> team : resultTable.entrySet()) {
            if (team.getValue() > max) {
                winners = new ArrayList<>(List.of(team.getKey()));
                max = team.getValue();
            } else if (team.getValue().equals(max)) {
                winners.add(team.getKey());
            }
        }
        return winners;
    }

    @Override
    public String toString() {
        List<String> winners = this.findWinners();
        StringBuilder res = new StringBuilder();
        res.append("----------------\nWinners table:\n");
        for (String winner : winners) {
            res.append("\t'").append(winner).append("': ").append(resultTable.get(winner)).append(" points\n");
        }
        return res.append("----------------\n").toString();
    }
}
