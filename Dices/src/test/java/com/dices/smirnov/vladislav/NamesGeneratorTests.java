package com.dices.smirnov.vladislav;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class NamesGeneratorTests {
    @Test
    public void tryToGenerateNewTeamLabel() {
        Set<String> set = new HashSet<>();
        NamesGenerator gen = new NamesGenerator();
        for (int i = 0; i < 10; ++i)
            set.add(gen.takeUniqueTeamName());
        assertEquals(10, set.size());
    }
    @Test
    public void tryToGenerateNewPlayerName() {
        Set<String> set = new HashSet<>();
        NamesGenerator gen = new NamesGenerator();
        for (int i = 0; i < gen.playersNames.size(); ++i)
            set.add(gen.takeUniquePlayerName());
        assertEquals(30, set.size());
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
