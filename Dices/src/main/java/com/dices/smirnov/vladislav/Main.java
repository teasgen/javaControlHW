package com.dices.smirnov.vladislav;

import java.util.*;

/**
 * Main class for program execution
 * @author Vlad Smirnov
 */
public class Main {
    /**
     * The croupier
     * Manages the whole process
     */
    public static final Croupier croupier = new Croupier();
    /**
     * Number of teams
     */
    static int teamsNumber;
    /**
     * Once at what time the croupier should print result table
     */
    public static int CROUPIER_REQUEST_TIME = 10_000;
    /**
     * How long the game lasts
     */
    public static int END_GAME_TIME = 35_000;

    /**
     * The croupier getter
     * @return Modifiable current croupier
     */
    public static Croupier getCroupier() { return croupier; }

    /**
     * Main method, which manages the whole program
     * @param args console arguments
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Wrong number of arguments");
            return;
        }
        try {
            teamsNumber = Integer.parseInt(args[0]);
            if (teamsNumber < 0 || teamsNumber > 10)
                throw new NumberFormatException("Wrong number of teams");
        } catch (NumberFormatException e) {
            System.out.println("Incorrect first argument!");
            return;
        }
        singleGame();
    }

    /**
     * This method is a single game.
     * <p>Creates {@link Main#teamsNumber} teams of 3 people with random unique names and start threads</p>
     * <p>Creates timers, which will execute every {@link Main#CROUPIER_REQUEST_TIME}, {@link Main#END_GAME_TIME} milliseconds</p>
     */
    public static void singleGame() {
        System.out.println("\nThe new party has begun!");
        NamesGenerator generator = new NamesGenerator();
        for (int i = 0; i < teamsNumber; ++i) {
            String teamName = generator.takeUniqueTeamName();
            croupier.setTeam(teamName);
            for (int j = 0; j < 3; ++j) {
                String name = generator.takeUniquePlayerName();
                System.out.println("-> Hello, I'm " + name + " from '" + teamName + "'");
                croupier.setPlayer(name, teamName).start();
            }
        }

        Timer croupierTimer = new Timer();
        Timer endGameTimer = new Timer();
        TimerTask printResultTableTask = new CroupierTimer();
        TimerTask endGameTask = new EndTimer(croupierTimer, endGameTimer);
        croupierTimer.schedule(printResultTableTask, new Date(), CROUPIER_REQUEST_TIME);
        endGameTimer.schedule(endGameTask, END_GAME_TIME);
    }

    /**
     * Class for printing result table every {@link Main#CROUPIER_REQUEST_TIME} ms
     * <p>When called not the first time prints winners table</p>
     */
    static class CroupierTimer extends TimerTask {
        /**
         * The number of calls
         */
        private int timesCalled = 0;

        /**
         * Overrides run method
         */
        @Override
        public void run() {
            if (timesCalled++ == 0)
                return;
            synchronized (croupier.getResultTable()) {
                System.out.println(croupier);
            }
        }
    }

    /**
     * @return information about author
     */
    static String aboutAuthor() {
    return  """
            Автор: Смирнов Владислав, БПИ211
            Если Вы нашли ошибку или есть вопросы, то напишите мне:
                Телеграм: @teasgen
                Почта:    vmsmirnov@edu.hse.ru""";
    }

    /**
     * Class for game ending task after {@link Main#END_GAME_TIME} ms
     * <p>Interrupts all player threads and both timers</p>
     * <p>Finds winners and distributes prize between winner-teams</p>
     * <p>Asks user for continuing the game, in positive case calls {@link Main#singleGame()}, otherwise exits</p>
     */
    static class EndTimer extends TimerTask {
        /**
         * The instance of Timer, which executes every {@link Main#CROUPIER_REQUEST_TIME} ms
         */
        Timer croupierTimer;
        /**
         * The instance of Timer, which executes every {@link Main#END_GAME_TIME} ms
         */
        Timer endGameTimer;

        /**
         * Constructor, takes timers
         * @param croupier as same as {@link EndTimer#croupierTimer}
         * @param ending   as same as {@link EndTimer#endGameTimer}
         */
        EndTimer(Timer croupier, Timer ending) {
            croupierTimer = croupier;
            endGameTimer = ending;
        }

        /**
         * Overrides run method
         */
        @Override
        public void run() {
            System.out.println("The game ended");
            croupierTimer.cancel();
            endGameTimer.cancel();
            for (Team team : croupier.getTeams()) {
                for (Player player : team.getSquad()) {
                    player.interrupt();
                }
            }

            Random random = new Random();
            int prize = random.nextInt(1_000_000, 10_000_001);
            System.out.println(croupier);
            var winners = croupier.findWinners();
            int score = croupier.getResultTable().get(winners.get(0));
            double prizeForOneTeam = (double) prize / teamsNumber;
            System.out.println("Each team will get: " + String.format("%.2f", prizeForOneTeam) + "¥");
            for (String winner : winners) {
                Team team = croupier.getTeam(winner);
                System.out.println("Team: '" + team.getLabel() + "'");
                for (Player player : team.getSquad()) {
                    System.out.println("\t" + player.getPlayerName() + " gets " +
                            String.format("%.2f", prizeForOneTeam * ((double) player.getScore() / score)) + "¥");
                }
            }

            for (Team team : croupier.getTeams()) {
                for (Player player : team.getSquad()) {
                    System.out.println("-> " + player.getPlayerName() + ": goodbye!");
                }
            }
            System.out.print("Do you want to continue game? 'yes' or 'no': ");
            String ans = new Scanner(System.in).next();
            if (ans.equalsIgnoreCase("no") || ans.equalsIgnoreCase("n")) {
                System.out.println(aboutAuthor());
            } else if (ans.equalsIgnoreCase("yes") || ans.equalsIgnoreCase("y")) {
                croupier.clear();
                singleGame();
            } else {
                System.out.println("Incorrect answer");
                System.out.println(aboutAuthor());
            }
        }
    }

}
