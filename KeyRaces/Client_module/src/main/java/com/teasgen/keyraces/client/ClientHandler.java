package com.teasgen.keyraces.client;

import com.teasgen.keyraces.server.ClientStats;
import com.teasgen.keyraces.server.GroupServer;
import com.teasgen.keyraces.server.GroupStats;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

public class ClientHandler extends Thread {
    private final int port;
    private final String address;
    private final String name;
    public volatile boolean running;
    private String text;
    private final ClientViewModel clientViewModel;
    public ClientHandler(String address, int port, String name, ClientViewModel clientViewModel) {
        this.address = address;
        this.port = port;
        this.name = name;
        this.running = true;
        this.clientViewModel = clientViewModel;
    }
    @Override
    public void run() {
        try (Socket socket = new Socket(this.address, this.port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            out.writeObject(this.name);
            out.flush();
            while (this.running) {
                int action = in.readInt();
                switch (action) {
                    case 1 -> System.out.println(in.readObject());
                    case 2 -> {
                        GroupStats groupStats = (GroupStats) in.readObject();
                        System.out.println(groupStats.remainTime);
                        out.writeObject(new ClientStats(
                                clientViewModel.getTotalNumber(),
                                clientViewModel.getErrorsNumber())
                        );
                        statsToViewModel(groupStats);
                    }
                    case 3 -> {
                        GroupStats finalStats = (GroupStats) in.readObject();
                        System.out.println(finalStats.names);
                        // TODO: send to GUI
                    }
                    case 4 -> {
                        text = (String) in.readObject();
                        Platform.runLater(() -> clientViewModel.setText(text));
                    }
                    default -> System.out.println("Wtf");
                }
                if (action > 4 || action == 3)
                    break;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("The connection was lost");
        } finally {
            running = false;
        }
    }

    private void statsToViewModel(GroupStats groupStats) {
        int elapsedTime = (GroupServer.GAME_DURATION / 1000) - groupStats.remainTime;
        Set<ArrayList<Integer>> table = new TreeSet<>(new ArrayListComparator());
        for (int i = 0; i < groupStats.names.size(); ++i) {
            if (groupStats.disconnected.get(i))
                continue;
            int correctNumber = groupStats.totalCount.get(i) - groupStats.errorCount.get(i);
            table.add(new ArrayList<>(Arrays.asList(
                    correctNumber,
                    groupStats.errorCount.get(i),
                    elapsedTime == 0 ? 0 : (int) ((float) correctNumber / elapsedTime * 60),
                    i
            )));
        }
        StringBuilder res = new StringBuilder();
        for (ArrayList<Integer> player : table) {
            int curInx = player.get(3);
            res.append(groupStats.itsMe.get(curInx) ? "Its me" : groupStats.names.get(curInx))
                    .append(" ")
                    .append(String.format("%.2f", (float) player.get(0) / groupStats.textLength * 100))
                    .append("%, ")
                    .append(player.get(1))
                    .append(" mistakes, ")
                    .append(player.get(2))
                    .append(" sym/min\n");
        }
        System.out.println(res);
        Platform.runLater(() -> {
            clientViewModel.setTime(String.valueOf(groupStats.remainTime));
            clientViewModel.setTable(res.toString());
            clientViewModel.setDisabled(false);
        });
    }

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
}
