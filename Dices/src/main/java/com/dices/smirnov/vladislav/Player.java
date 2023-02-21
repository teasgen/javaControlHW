package com.dices.smirnov.vladislav;

import java.util.Random;

public class Player extends Thread {
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

    public int getScore() { return score; }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (Main.croupier.getTeam(teamLabel)) {
                int currentScore = 0;
                for (int i = 0; i < 6; ++i) {
                    currentScore += rnd.nextInt(6) + 1;
                }
                score += currentScore;
                synchronized (Main.croupier.getResultTable()) {
                    Croupier croupier = Main.croupier;
                    if (Main.croupier.getResultTable().containsKey(teamLabel)) {
                        Integer teamScore = croupier.getResultTable().get(teamLabel);
                        Main.croupier.getResultTable().put(teamLabel, teamScore + currentScore);
                    } else {
                        Main.croupier.getResultTable().putIfAbsent(teamLabel, currentScore);
                    }
                }
            }
            try {
                Thread.sleep(rnd.nextInt(100, 1001));
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass())
            return false;
        return name.equals(((Player) obj).getPlayerName());
    }
}
