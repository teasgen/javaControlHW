package com.dices.smirnov.vladislav;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TeamTests {
    @Test
    public void creatingTeamInstanceGettersTest() {
        Team team = new Team("label");
        assertEquals("label", team.getLabel());
        assertEquals(new ArrayList<>(), team.getSquad());
    }
    @Test
    public void equalsReturnTrueWhenTeamsWithEqualLabels() {
        Team team1 = new Team("label");
        Team team2 = new Team("label");
        assertEquals(team2, team1);
        assertEquals(team1, team2);
    }
    @Test
    public void equalsReturnFalseWhenTeamsWithDifferentLabels () {
        Team team1 = new Team("label1");
        Team team2 = new Team("label2");
        assertNotEquals(team2, team1);
        assertNotEquals(team1, team2);
    }
    @Test
    public void equalsReturnFalseWhenTryToCompareTeamToNonTeamInstance () {
        Team team = new Team("label1");
        Object x = new Object();
        assertNotEquals(team, x);
    }
    @Test
    public void checkCorrectnessToString() {
        Team team = new Team("label1");
        assertEquals("Team{label='label1'}", team.toString());
    }
}
