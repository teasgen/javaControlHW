package com.dices.smirnov.vladislav;

import java.util.*;

public class Croupier {
    private final List<Team> teams = new ArrayList<>();
    private final Map<String, Integer> resultTable = new HashMap<>();
    public Player setPlayer(String teamLabel, String playerName) {
        Player player = new Player(playerName, teamLabel);
        teams.get(teams.indexOf(new Team(teamLabel))).getSquad().add(player);
        return player;
    }
    public Team setTeam(String label) {
        Team team = new Team(label);
        teams.add(team);
        return team;
    }

    public synchronized Team getTeam(String label) {
        for (Team team : teams) {
            if (team.getLabel().equals(label))
                return team;
        }
        throw new IllegalArgumentException("Team with label: " + label + "doesn't exists");
    }
    public List<Team> getTeams() {
        return Collections.unmodifiableList(teams);
    }

    public Map<String, Integer> getResultTable() {
        return resultTable;
    }

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

    public String teamsToString() {
        StringBuilder res = new StringBuilder();
        for (Team team : teams) {
            res.append(team.toString()).append(":\n");
            for (Player player : team.getSquad())
                res.append('\t').append(player.toString()).append("\n");
        }
        return res.toString();
    }
}
