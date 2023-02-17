package com.dices.smirnov.vladislav;

import java.util.*;

public class Main {
    public static final Croupier croupier = new Croupier();
    public static int CROUPIER_REQUEST_TIME = 10_000;
    public static int END_GAME_TIME = 35_000;
    public static void main(String[] args) throws InterruptedException {
        int t = 2;
        croupier.setTeam("first");
        croupier.setTeam("second");
        croupier.setPlayer("first", "1f");
        croupier.setPlayer("first", "2f");
        croupier.setPlayer("first", "3f");
        croupier.setPlayer("second", "1s");
        croupier.setPlayer("second", "2s");
        croupier.setPlayer("second", "3s");
        for (Team team : croupier.getTeams())
            for (Player player : team.getSquad())
                player.start();
        Thread threadInvitePlayerToTheTable = new Thread(() -> {
            while (!Thread.interrupted()) {
                for (int i = 0; i < croupier.getTeams().size(); ++i) {
                    synchronized (croupier.getTeams().get(i)) {
                        croupier.getTeams().get(i).notifyAll();
                    }
                }
            }
        });
        threadInvitePlayerToTheTable.start();
        TimerTask printResultTableTask = new TimerTask() {
            @Override
            public void run() {
                synchronized (croupier.getResultTable()) {
                    System.out.println(croupier.teamsToString());
                    System.out.println(croupier);
                }
            }
        };
        Timer croupierTimer = new Timer();
        Timer endGameTimer = new Timer();
        croupierTimer.schedule(printResultTableTask, new Date(), CROUPIER_REQUEST_TIME);
        TimerTask endGameTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("The game ended");
                threadInvitePlayerToTheTable.interrupt();
                croupierTimer.cancel();
                endGameTimer.cancel();
                for (Team team : croupier.getTeams()) {
                    for (Player player : team.getSquad()) {
                        player.interrupt();
                    }
                }
                Random random = new Random();
                int prize = random.nextInt(1_000_000, 10_000_001);
                System.out.println(croupier);
                var winners = croupier.findWinners();
                int score = croupier.getResultTable().get(winners.get(0));
                System.out.println("Winners:");
                double prizeForOneTeam = (double) prize / t;
                for (String winner : winners) {
                    Team team = croupier.getTeam(winner);
                    System.out.println("Team: " + team.getLabel());
                    for (Player player : team.getSquad()) {
                        System.out.println("\t" + player.getPlayerName() + " get " +
                                String.format("%.2f", prizeForOneTeam * ((double) player.getScore() / score)) + "¥");
                    }
                }
                for (Team team : croupier.getTeams()) {
                    for (Player player : team.getSquad()) {
                        System.out.println(player.getPlayerName() + ": goodbye!");
                    }
                }
            }
        };
        endGameTimer.schedule(endGameTask, END_GAME_TIME);
    }
}


