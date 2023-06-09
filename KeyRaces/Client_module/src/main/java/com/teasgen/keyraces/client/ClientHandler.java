package com.teasgen.keyraces.client;

import com.teasgen.keyraces.server.ClientStats;
import com.teasgen.keyraces.server.GroupServer;
import com.teasgen.keyraces.server.GroupStats;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

/**
 * Handles the client-side communication with the server.
 * Manages the connection, sends/receives data, and updates the client's view model.
 */
public class ClientHandler extends Thread {

    private final Integer port;
    private final String address;
    private final String name;
    public volatile boolean running;
    private final ArrayList<Integer> timeToEnd = new ArrayList<>();
    private String text;
    private final ClientViewModel clientViewModel;
    private String currentWinner;
    private int winnerSpeed;
    Socket socket;
    ObjectOutputStream out;
    ObjectInputStream in;

    /**
     * Constructs a ClientHandler object.
     *
     * @param address        the server address
     * @param port           the server port
     * @param name           the client name
     * @param clientViewModel the view model for the client
     */
    public ClientHandler(String address, Integer port, String name, ClientViewModel clientViewModel) {
        this.address = address;
        this.port = port;
        this.name = name;
        this.running = true;
        this.clientViewModel = clientViewModel;
        for (int i = 0; i < 3; ++i)
            timeToEnd.add(Integer.MIN_VALUE);
    }

    /**
     * The main logic of the client handler.
     * Handles communication with the server, receives data, and updates the client's view model accordingly.
     */
    @Override
    public void run() {
        try {
            socket = new Socket(this.address, this.port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            out.writeObject(this.name);
            out.flush();
            try {
                while (this.running) {
                    int action = in.readInt();
                    switch (action) {
                        case 1 -> {
                            String message = (String) in.readObject();
                            if (!"".equals(message))
                                System.out.println(message);
                        }
                        case 2 -> {
                            GroupStats groupStats = (GroupStats) in.readObject();
                            out.writeObject(new ClientStats(
                                    clientViewModel.getTotalNumber(),
                                    clientViewModel.getErrorsNumber(),
                                    1)
                            );
                            statsToViewModelDuringGame(groupStats);
                        }
                        case 3 -> {
                            System.out.println("END GAME");
                            GroupStats finalStats = (GroupStats) in.readObject();
                            System.out.println(finalStats.names);
                            statsToViewModelEndGame(finalStats);

                            out.writeObject(new ClientStats(
                                    clientViewModel.getTotalNumber(),
                                    clientViewModel.getErrorsNumber(),
                                    -1)
                            );
                        }
                        case 4 -> {
                            Platform.runLater(() -> clientViewModel.setShowAll(true));
                            text = (String) in.readObject();
                            Platform.runLater(() -> clientViewModel.setText(text));
                        }
                        case 5 -> {
                            text = (String) in.readObject();
                            Platform.runLater(() -> clientViewModel.setText("Starts in " + text + " seconds"));
                        }
                        default -> System.out.println("Wtf");
                    }
                    if (action > 5 || action == 3)
                        break;
                }
            } catch (SocketException e) {
                System.out.println("The connection was lost");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("""
                    This server is unavailable now.
                    Please reboot the application with correct values
                    ie: Address={localhost}, port={5619}""");
        } finally {
            running = false;
        }
    }

    /**
     * Formats the group stats into a table string.
     *
     * @param groupStats the group statistics
     * @return the formatted table string
     */
    String formTable(GroupStats groupStats) {
        Integer elapsedTime = (GroupServer.GAME_DURATION / 1000) - groupStats.remainTime;
        Set<ArrayList<Integer>> table = new TreeSet<>(new ArrayListComparator());
        for (int i = 0; i < groupStats.names.size(); ++i) {
            if (timeToEnd.get(i) == Integer.MIN_VALUE && groupStats.totalCount.get(i) == groupStats.textLength)
                timeToEnd.set(i, elapsedTime);
            int curTime = timeToEnd.get(i) == Integer.MIN_VALUE ? elapsedTime : timeToEnd.get(i);
            if (groupStats.disconnected.get(i))
                continue;
            int correctNumber = groupStats.totalCount.get(i);
            table.add(new ArrayList<>(Arrays.asList(
                    correctNumber,
                    groupStats.errorCount.get(i),
                    curTime == 0 ? 0 : (int) (((float) correctNumber / curTime) * 60),
                    i
            )));
        }
        StringBuilder res = new StringBuilder();
        int i = 0;
        for (ArrayList<Integer> player : table) {
            int curInx = player.get(3);
            if (i == 0) {
                currentWinner = groupStats.itsMe.get(curInx) ? "I" : groupStats.names.get(curInx);
                winnerSpeed = player.get(2);
            }
            res.append(groupStats.itsMe.get(curInx) ? "Its me" : groupStats.names.get(curInx))
                    .append(" ")
                    .append(String.format("%.2f", ((float) player.get(0) / groupStats.textLength) * 100))
                    .append("%, ")
                    .append(player.get(1))
                    .append(" mistakes, ")
                    .append(player.get(2))
                    .append(" sym/min\n");
            ++i;
        }
        return res.toString();
    }

    /**
     * Updates the client's view model during the game phase.
     *
     * @param groupStats the group statistics during the game
     */
    private void statsToViewModelDuringGame(GroupStats groupStats) {
        String res = formTable(groupStats);
        Platform.runLater(() -> {
            clientViewModel.setTime("Remain time: " + groupStats.remainTime);
            clientViewModel.setTable(res);
            clientViewModel.setDisabled(false);
        });
    }

    /**
     * Updates the client's view model at the end of the game.
     *
     * @param groupStats the final group statistics
     */
    private void statsToViewModelEndGame(GroupStats groupStats) {
        String res = formTable(groupStats);
        Platform.runLater(() -> {
            clientViewModel.setTable(res);
            clientViewModel.setDisabled(true);
            clientViewModel.setEndgame("Game over");
            clientViewModel.setText(currentWinner + " won the game with speed: " + winnerSpeed + " sym/min!");
        });
    }

    /**
     * Custom comparator for sorting ArrayLists in descending order.
     */
    static class ArrayListComparator implements Comparator<ArrayList<Integer>> {
        @Override
        public int compare(ArrayList<Integer> list1, ArrayList<Integer> list2) {
            int first = list1.get(0);
            int second = list2.get(0);
            if (first == second)
                return list1.toString().compareTo(list2.toString());
            return Integer.compare(second, first);
        }
    }

    /**
     * Closes the client handler and releases any resources used.
     */
    public void close() {
        running = false;
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException | NullPointerException e) {
            System.out.println("Already closed");
        }
    }
}
