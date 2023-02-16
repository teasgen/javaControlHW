package com.dices.smirnov.vladislav;

import java.util.ArrayList;
import java.util.Random;

class Player extends Thread {
    private int score;
    private final String name;
    private final Random rnd = new Random();
    Player(String playerName) {
        name = playerName;
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
        synchronized (Main.getCroupier().getPlayersTeam(this)) {
            System.out.println(name + " enter in sync: " + Main.getCroupier().getPlayersTeam(this));
            int currentScore = 0;
            for (int i = 0; i < 6; ++i) {
                currentScore += rnd.nextInt(6) + 1;
            }
            synchronized (Main.getCroupier().getResultTable()) {
                Croupier croupier = Main.getCroupier();
                String teamLabel = croupier.getPlayersTeamLabel(this);
                if (Main.getCroupier().getResultTable().containsKey(teamLabel)) {
                    Integer teamScore = croupier.getResultTable().get(teamLabel);
                    teamScore += currentScore;
                    Main.getCroupier().getResultTable().put(teamLabel, teamScore);
                } else {
                    Main.getCroupier().getResultTable().putIfAbsent(teamLabel, currentScore);
                }
            }
            try {
                Main.getCroupier().getPlayersTeam(this).wait(rnd.nextInt(100, 1001));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            score += currentScore;
            System.out.println(name + " exit");
            Main.getCroupier().getPlayersTeam(this).notify();
        }
    }
}

public record Team (
    String label,
    ArrayList<Player> squad
){
    public Team {
        if (label == null)
            throw new IllegalArgumentException("label == null");
    }

    @Override
    public String toString() {
        return "Team{" +  "label='" + label + '}';
    }

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
