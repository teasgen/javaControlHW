package com.dices.smirnov.vladislav;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadPoolExecutor;

public class Main {
    public static final Croupier croupier = new Croupier();
    public static void main(String[] args) throws InterruptedException {
        croupier.setTeam("first");
        croupier.setPlayer("first", "1");
        croupier.setPlayer("first", "2");
        croupier.setPlayer("first", "3");
        for (Team team : croupier.getTeams()) {
            for (Player player : team.squad) {
                player.start();
            }
            team.start();
        }
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                synchronized (croupier.getResultTable()) {
                    System.out.println(croupier);
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, new Date(), 1000);
    }
}


