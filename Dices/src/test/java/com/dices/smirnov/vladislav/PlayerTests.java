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
    public void checkScoreChangingWhenRunSinglePlayerGameOneTime() throws InterruptedException {
        Player player = new Player("name", "teamLabel");
        Main.croupier.clear();
        Main.croupier.setTeam("teamLabel");
        Main.croupier.setPlayer("name", "teamLabel");
        int scoreWas = player.getScore();
        player.start();     // sleep more than 100 ms
        Thread.sleep(90);
        player.interrupt(); // will be executed faster than 100 ms
        int scoreNew = player.getScore();
        assertTrue(scoreNew - scoreWas <= 6 * 6 && scoreNew - scoreWas >= 6);
        Main.croupier.clear();
    }
    @Test
    public void checkScoreChangingWhenResultTableRewriting() throws InterruptedException {
        String name = "name";
        String label = "teamLabel";
        Player player = new Player(name, label);
        Main.croupier.clear();
        Main.croupier.setTeam(label);
        Main.croupier.setPlayer(name, label);
        Main.croupier.getResultTable().putIfAbsent(label, 42); // add some score -> thread will fall into containsKey
        int scoreWas = player.getScore();
        player.start();     // sleep more than 100 ms
        Thread.sleep(90);
        player.interrupt(); // will be executed faster than 100 ms
        int scoreNew = player.getScore();
        assertEquals(scoreNew - scoreWas, Main.croupier.getResultTable().get(label) - 42);
        Main.croupier.clear();
    }
}
