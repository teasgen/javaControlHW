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
                // System.out.println(teamLabel + ", " + name + " enter at sync: ");
                int currentScore = 0;
                for (int i = 0; i < 6; ++i) {
                    currentScore += rnd.nextInt(6) + 1;
                }
                score += currentScore;
                synchronized (Main.croupier.getResultTable()) {
                    Croupier croupier = Main.croupier;
                    if (Main.croupier.getResultTable().containsKey(teamLabel)) {
                        Integer teamScore = croupier.getResultTable().get(teamLabel);
                        teamScore += currentScore;
                        Main.croupier.getResultTable().put(teamLabel, teamScore);
                    } else {
                        Main.croupier.getResultTable().putIfAbsent(teamLabel, currentScore);
                    }
                }
                // System.out.println(teamLabel + ", " + name + " make move: " + score);
            }
            try {
                // System.out.println(teamLabel + ", " + name + " sleeping...");
                Thread.sleep(rnd.nextInt(100, 1001));
                // System.out.println(teamLabel + ", " + name + " wake up!!!");
                // synchronized (Main.croupier.getTeam(teamLabel)) {
                //     Main.croupier.getTeam(teamLabel).wait();
                //}
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
