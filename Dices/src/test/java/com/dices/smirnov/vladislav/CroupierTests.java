package com.dices.smirnov.vladislav;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CroupierTests {
    @Test
    public void whenAddingNewTeam() {
        Croupier croupier = new Croupier();
        croupier.setTeam("label");
        assertTrue(croupier.getTeams().contains(new Team("label")));
    }
    @Test
    public void whenAddingNewPlayer() {
        Croupier croupier = new Croupier();
        croupier.setTeam("label");
        croupier.setPlayer("name", "label");
        assertTrue(croupier.getTeam("label").getSquad().contains(new Player("name", "label")));
    }
    @Test
    public void whenAddingNewPlayerToTeamWhichDoesntExist() {
        Croupier croupier = new Croupier();
        croupier.setTeam("label");
        PrintStream standardOut = System.out;
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));
        croupier.setPlayer("name", "label2");
        assertEquals("Team with label: " + "label2" + " doesn't exist\n", outputStreamCaptor.toString());
        System.setOut(standardOut);
    }
    @Test
    public void tryingToGetExistingTeam() {
        Croupier croupier = new Croupier();
        String label = "label";
        croupier.setTeam(label);
        Team team = new Team(label);
        assertEquals(team, croupier.getTeam(label));
    }
    @Test
    public void tryingToGetAbsentTeam() {
        Croupier croupier = new Croupier();
        PrintStream standardOut = System.out;
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));
        croupier.getTeam("label2");
        assertEquals("Team with label: " + "label2" + " doesn't exist\n", outputStreamCaptor.toString());
        System.setOut(standardOut);
    }
    @Test
    public void findingSeveralWinnersInExistingTable() {
        Croupier croupier = new Croupier();
        croupier.getResultTable().put("team1", 10);
        croupier.getResultTable().put("team2", 20);
        croupier.getResultTable().put("team3", 15);
        croupier.getResultTable().put("team4", 20);
        List<String> expected = new ArrayList<>(List.of("team2", "team4"));
        List<String> ans = croupier.findWinners();
        assertTrue(expected.size() == ans.size() && expected.containsAll(ans) && ans.containsAll(expected));
    }
}
