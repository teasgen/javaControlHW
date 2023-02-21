package com.dices.smirnov.vladislav;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerTests {
    @Test
    public void creatingPlayerInstanceGettersTest() {
        Player player = new Player("name", "teamLabel");
        assertEquals("name", player.getPlayerName());
        assertEquals(0, player.getScore());
    }
    @Test
    public void checkCorrectnessToString() {
        Player player = new Player("name", "teamLabel");
        assertEquals("Player{score=0, name='name'}", player.toString());
    }
    @Test
    public void checkScoreChangingWhenRunOnePlayerGame() throws InterruptedException {
        Player player = new Player("name", "teamLabel");
        Main.croupier.setTeam("teamLabel");
        Main.croupier.setPlayer("teamLabel", "name");
        int scoreWas = player.getScore();
        player.start();     // sleep more than 100 ms
        Thread.sleep(90);
        player.interrupt(); // will be executed faster than 100 ms
        int scoreNew = player.getScore();
        System.out.println(scoreWas + " " + scoreNew);
        assertTrue(scoreNew - scoreWas <= 6 * 6 && scoreNew - scoreWas >= 6);
    }
    @Test
    public void checkScoreChangingWhenRunTwoPlayerGameConsequently() throws InterruptedException {
        Player player = new Player("name", "teamLabel");
        Player player2 = new Player("name2", "teamLabel");
        Main.croupier.setTeam("teamLabel");
        Main.croupier.setPlayer("teamLabel", "name");
        int scoreWas = player.getScore();
        player.start();     // sleep more than 100 ms
        Thread.sleep(90);
        player.interrupt(); // will be executed faster than 100 ms
        int scoreNew = player.getScore();
    }
}
