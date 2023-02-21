package com.dices.smirnov.vladislav;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NamesGeneratorTests {
    @Test
    public void tryToGenerateNewTeamLabel() {
        NamesGenerator gen = new NamesGenerator();
        for (int i = 0; i < gen.teamsNames.size(); ++i)
            assertTrue(gen.teamsNames.contains(gen.takeUniqueTeamName()));
        assertEquals(10, gen.teamsNames.size());
    }
    @Test
    public void tryToGenerateNewPlayerName() {
        NamesGenerator gen = new NamesGenerator();
        for (int i = 0; i < gen.playersNames.size(); ++i)
            assertTrue(gen.playersNames.contains(gen.takeUniquePlayerName()));
        assertEquals(30, gen.playersNames.size());
    }
    @Test
    public void generatorInit() {
        NamesGenerator gen = new NamesGenerator();
        for (int i = 0; i < gen.teamsNames.size(); ++i)
            gen.takeUniqueTeamName();
        for (int i = 0; i < gen.playersNames.size(); ++i)
            gen.takeUniquePlayerName();
        gen.init();
        assertEquals(0, gen.teamsNamesUsed);
        assertEquals(0, gen.playersNamesUsed);
    }
}
