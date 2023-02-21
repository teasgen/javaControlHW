package com.dices.smirnov.vladislav;

import java.util.*;

public class Main {
    public static final Croupier croupier = new Croupier();
    public static int CROUPIER_REQUEST_TIME = 1_000;
    public static int END_GAME_TIME = 10_000;
    static Timer croupierTimer = new Timer();
    static Timer endGameTimer = new Timer();
    static class CroupierTimer extends TimerTask {
        @Override
        public void run() {
            synchronized (croupier.getResultTable()) {
                System.out.println(croupier.teamsToString());
                System.out.println(croupier);
            }
        }
    }
    static class EndTimer extends TimerTask {
        int teamsNumber;
        EndTimer(int t) {
            teamsNumber = t;
        }
        @Override
        public void run() {
            System.out.println("The game ended");
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
            double prizeForOneTeam = (double) prize / teamsNumber;
            System.out.println("Winners, each team will get: " + String.format("%.2f", prizeForOneTeam) + "¥");
            for (String winner : winners) {
                Team team = croupier.getTeam(winner);
                System.out.println("Team: '" + team.getLabel() + "'");
                for (Player player : team.getSquad()) {
                    System.out.println("\t" + player.getPlayerName() + " gets " +
                            String.format("%.2f", prizeForOneTeam * ((double) player.getScore() / score)) + "¥");
                }
            }
            for (Team team : croupier.getTeams()) {
                for (Player player : team.getSquad()) {
                    System.out.println(player.getPlayerName() + ": goodbye!");
                }
            }
        }
    }
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Wrong number of arguments");
            return;
        }
        int t;
        try {
            t = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Incorrect number of teams!");
            return;
        }
        NamesGenerator generator = new NamesGenerator();
        for (int i = 0; i < t; ++i) {
            String teamName = generator.takeUniqueTeamName();
            croupier.setTeam(teamName);
            for (int j = 0; j < 3; ++j)
                croupier.setPlayer(generator.takeUniquePlayerName(), teamName).start();
        }
        TimerTask printResultTableTask = new CroupierTimer();
        TimerTask endGameTask = new EndTimer(t);
        croupierTimer.schedule(printResultTableTask, new Date(), CROUPIER_REQUEST_TIME);
        endGameTimer.schedule(endGameTask, END_GAME_TIME);
    }
}
