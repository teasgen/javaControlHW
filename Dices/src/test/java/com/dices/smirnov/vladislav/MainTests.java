package com.dices.smirnov.vladislav;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

@Isolated
public class MainTests {
    @Test
    public void gettingWrongConsoleArgumentsCount() {
        PrintStream standardOut = System.out;
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));
        Main.main(new String[]{"a", "b"});
        assertEquals("Wrong number of arguments\n", outputStreamCaptor.toString());
        System.setOut(standardOut);
    }
    @Test
    public void gettingWrongFirstConsoleArgument() {
        PrintStream standardOut = System.out;
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));
        Main.main(new String[]{"a"});
        assertEquals("Incorrect first argument!\n", outputStreamCaptor.toString());
        System.setOut(standardOut);
    }
    @Test
    public void areAllPlayersGettingStarted() {
        PrintStream standardOut = System.out;
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));
        Main.main(new String[]{"2"});
        for (Team team : Main.getCroupier().getTeams()) {
            for (Player player : team.getSquad()) {
                if (player.isInterrupted()) {
                    fail();
                    return;
                }
                player.interrupt();
            }
        }
        for (Team team : Main.getCroupier().getTeams()) {
            for (Player player : team.getSquad()) {
                try {
                    player.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        assertTrue(true);
        Main.getCroupier().clear();
        System.setOut(standardOut);
    }
}
