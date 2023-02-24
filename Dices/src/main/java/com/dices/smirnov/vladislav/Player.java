package com.dices.smirnov.vladislav;

import java.util.Random;

/**
 * Player thread
 * @author Vlad Smirnov
 */
public class Player extends Thread {
    /**
     * Current player score
     */
    private int score;
    /**
     * Player name
     */
    private final String name;
    /**
     * Team name the player is a member of
     */
    private final String teamLabel;
    /**
     * Random generator
     */
    private final Random rnd = new Random();

    /**
     * Constructor
     * @param playerName as same as {@link Player#name}
     * @param label      as same as {@link Player#teamLabel}
     */
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

    /**
     * @return player name
     */
    public String getPlayerName() {
        return name;
    }

    /**
     * @return current player score
     */
    public int getScore() { return score; }

    /**
     * Sums 6 random numbers in [1, 6]
     * @return sum
     */
    public int makePlay() {
        int currentScore = 0;
        for (int i = 0; i < 6; ++i) {
            currentScore += rnd.nextInt(6) + 1;
        }
        return currentScore;
    }

    /**
     * Override main Thread method
     * <p>In infinite loop calculates current score, wait until the result table wont be reachable and rewrite it</p>
     * <p>Sleep for [100, 1000]ms</p>
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                synchronized (Main.getCroupier().getTeam(teamLabel)) {
                    int currentScore = makePlay();
                    score += currentScore;
                    synchronized (Main.getCroupier().getResultTable()) {
                        Croupier croupier = Main.getCroupier();
                        if (Main.getCroupier().getResultTable().containsKey(teamLabel)) {
                            Integer teamScore = croupier.getResultTable().get(teamLabel);
                            Main.getCroupier().getResultTable().put(teamLabel, teamScore + currentScore);
                        } else {
                            Main.getCroupier().getResultTable().putIfAbsent(teamLabel, currentScore);
                        }
                    }
                }
            } catch (NullPointerException e) {
                System.out.println(name + ", " + teamLabel);
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
