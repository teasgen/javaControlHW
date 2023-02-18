package com.dices.smirnov.vladislav;

import java.util.*;
import java.util.stream.IntStream;

public class NamesGenerator {
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
    int teamsNamesUsed = 0;
    int playersNamesUsed = 0;
    List<Integer> indexesToChooseNew = new ArrayList<>();
    NamesGenerator() {
        for (int i = 0; i < 30; ++i)
            indexesToChooseNew.add(i);
        Collections.shuffle(indexesToChooseNew);
    }
    public String takeUniqueTeamName() {
        return teamsNames.get(teamsNamesUsed++);
    }
    public String takeUniquePlayerName() {
        return playersNames.get(indexesToChooseNew.get(playersNamesUsed++));
    }
}
