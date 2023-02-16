package com.dices.smirnov.vladislav;

import javax.swing.*;
import javax.swing.plaf.TableHeaderUI;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static final Croupier croupier = new Croupier();
    public static void main(String[] args) throws InterruptedException {
        croupier.setTeam("first");
        croupier.setPlayer("first", "vlad");
        croupier.setPlayer("first", "olya");
        croupier.setPlayer("first", "nikita");
        croupier.getTeams().get(0).squad.get(0).start();
        croupier.getTeams().get(0).squad.get(1).start();
        croupier.getTeams().get(0).squad.get(2).start();
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


