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
    public synchronized void checkScoreChangingWhenRunSinglePlayer() {
        Player player = new Player("name", "teamLabel");
        int score = player.makePlay();
        assertTrue(score <= 6 * 6 && score >= 6);
    }
}
