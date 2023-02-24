package com.dices.smirnov.vladislav;

import java.util.*;

/**
 * Class for generating unique team labels and person names
 * @author Vlad Smirnov
 */
public class NamesGenerator {
    /**
     * 10 unique team labels
     */
    public final List<String> teamsNames = new ArrayList<>(
            List.of(
                    "Team 1",
                    "Team 2",
                    "Team 3",
                    "Team 4",
                    "Team 5",
                    "Team 6",
                    "Team 7",
                    "Team 8",
                    "Team 9",
                    "Team 10"
            )
    );
    /**
     * 30 unique players names
     */
    public final List<String> playersNames = new ArrayList<>(
            List.of(
                "Robert",
                        "John",
                        "Michael",
                        "David",
                        "William",
                        "Richard",
                        "Joseph",
                        "Thomas",
                        "Charles",
                        "Christopher",
                        "Daniel",
                        "Matthew",
                        "Anthony",
                        "Mark",
                        "Patricia",
                        "Jennifer",
                        "Linda",
                        "Elizabeth",
                        "Barbara",
                        "Susan",
                        "Jessica",
                        "Sarah",
                        "Karen",
                        "Lisa",
                        "Nancy",
                        "Betty",
                        "Margaret",
                        "Sandra",
                        "James",
                        "Mary"
            )
    );
    /**
     * Number of used teams names
     */
    int teamsNamesUsed = 0;
    /**
     * Number of used players names
     */
    int playersNamesUsed = 0;
    /**
     * The random permutation of numbers between [0, 29] - for easy unique names generation
     */
    List<Integer> indexesToChooseNew = new ArrayList<>();

    /**
     * Constructor
     * Makes {@link NamesGenerator#indexesToChooseNew}
     */
    NamesGenerator() {
        for (int i = 0; i < 30; ++i)
            indexesToChooseNew.add(i);
        Collections.shuffle(indexesToChooseNew);
    }

    /**
     * @return next unique team name
     */
    public String takeUniqueTeamName() {
        return teamsNames.get(teamsNamesUsed++);
    }

    /**
     * @return next unique player name
     */
    public String takeUniquePlayerName() {
        return playersNames.get(indexesToChooseNew.get(playersNamesUsed++));
    }

    /**
     * Resets {@link NamesGenerator#teamsNamesUsed} and {@link NamesGenerator#playersNamesUsed}
     */
    public void init() {
        teamsNamesUsed = 0;
        playersNamesUsed = 0;
    }
}
