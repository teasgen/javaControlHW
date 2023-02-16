package com.dices.smirnov.vladislav;

import java.util.*;

public class Croupier {
    private final List<Team> teams = new ArrayList<>();
    private final Map<String, Integer> resultTable = new HashMap<>();
    private final Map<String, String> playerTeam = new HashMap<>();
    public Team getPlayersTeam(Player player) {
        String teamLabel = playerTeam.get(player.getPlayerName());
        return teams.get(teams.indexOf(new Team(teamLabel)));
    }
    public void setPlayer(String teamLabel, String playerName) {
        teams.get(teams.indexOf(new Team(teamLabel))).squad.add(new Player(playerName, teamLabel));
        playerTeam.put(playerName, teamLabel);
    }
    public void setTeam(String label) {
        teams.add(new Team(label));
    }

    public Team getTeam(String label) {
        for (Team team : teams) {
            if (team.label.equals(label))
                return team;
        }
        throw new IllegalArgumentException("Team with label: " + label + "doesn't exists");
    }
    public String getPlayersTeamLabel(Player player) {
        return playerTeam.get(player.getPlayerName());
    }
    public List<Team> getTeams() {
        return Collections.unmodifiableList(teams);
    }

    public Map<String, Integer> getResultTable() {
        return resultTable;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("Current table:\n");
        for (Map.Entry<String, Integer> team : resultTable.entrySet()) {
            res.append('\t').append(team.getKey()).append(" have ").append(team.getValue()).append(" points");
        }
        return res.toString();
    }

    public String forPrintingTeams() {
        StringBuilder res = new StringBuilder();
        for (Team team : teams) {
            res.append(team.toString()).append(":\n");
            for (Player player : team.squad)
                res.append('\t').append(player.toString()).append("\n");
        }
        return res.toString();
    }
}
