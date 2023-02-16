package com.dices.smirnov.vladislav;

import java.util.ArrayList;
import java.util.Random;

class Player extends Thread {
    private int score;
    private final String name;
    private final String teamLabel;
    private final Random rnd = new Random();
    Player(String playerName, String label) {
        name = playerName;
        teamLabel = label;
    }

    @Override
    public String toString() {
        return "Player{" +
                "score=" + score +
                ", name='" + name + '\'' +
                '}';
    }

    public String getPlayerName() {
        return name;
    }

    @Override
    public void run() {
        synchronized (Main.croupier.getTeam(teamLabel)) {
            System.out.println(name + " enter in sync: " + Main.croupier.getTeam(teamLabel));
            int currentScore = 0;
            for (int i = 0; i < 6; ++i) {
                currentScore += rnd.nextInt(6) + 1;
            }
            synchronized (Main.croupier.getResultTable()) {
                Croupier croupier = Main.croupier;
                String teamLabel = croupier.getPlayersTeamLabel(this);
                if (Main.croupier.getResultTable().containsKey(teamLabel)) {
                    Integer teamScore = croupier.getResultTable().get(teamLabel);
                    teamScore += currentScore;
                    Main.croupier.getResultTable().put(teamLabel, teamScore);
                } else {
                    Main.croupier.getResultTable().putIfAbsent(teamLabel, currentScore);
                }
            }
            try {
                Main.croupier.getTeam(teamLabel).wait(rnd.nextInt(100, 1001));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            score += currentScore;
            System.out.println(name + " exit");
            Main.croupier.getTeam(teamLabel).notify();
        }
    }
}

public class Team {
    final String label;
    final ArrayList<Player> squad = new ArrayList<>();
    Team(String s) {
        label = s;
    }

//    @Override
//    public String toString() {
//        return "Team{" +  "label='" + label + '}';
//    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass().equals(String.class)) // equals to String by label
            return this.label.equals(obj);

        if (obj.getClass() != this.getClass())
            return false;
        else // equals to another Team
            return this.label.equals(((Team) obj).label);
    }
}
