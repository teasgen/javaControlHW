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
     * The prize for each winner
     */
    private double prize = 0.0;
    /**
     * The winner score;
     */
    private int score = 0;
    /**
     * The list of current game winners
     */
    private List<String> winners = new ArrayList<>();
    /**
     * The random for prize generation
     */
    private final Random rnd = new Random();
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
     * Generates random number in [1_000_000; 10_000_000]. Finds winner-teams and sets prize as generated number divide by quantity of winner-teams
     */
    public void generatePrize() {
        int totalPrize = rnd.nextInt(1_000_000, 10_000_001);
        winners = findWinners();
        prize = (double) totalPrize / winners.size();
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
     * @return Prize for each winner when the game has ended, otherwise 0.0
     */
    public double getPrize() {
        return prize;
    }

    /**
     * @return Winner score when the game has ended, otherwise 0.0
     */
    public int getScore() {
        return score;
    }

    /**
     * Set all variables to default values
     */
    public void clear() {
        teams.clear();
        resultTable.clear();
        winners.clear();
        prize = 0.0;
        score = 0;
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
        score = max;
        return winners;
    }

    /**
     * @return String: pretty view of teams with the highest score
     */
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

    /**
     * @return String: pretty view of all teams, their players and corresponding scores
     */
    public String getTotalResultsInPrettyView() {
        StringBuilder res = new StringBuilder();
        res.append(this);
        for (Team team : teams) {
            res.append(team.toString()).append(":\n");
            for (Player player : team.getSquad())
                res.append('\t').append(player.toString()).append("\n");
        }
        return res.toString();
    }
}