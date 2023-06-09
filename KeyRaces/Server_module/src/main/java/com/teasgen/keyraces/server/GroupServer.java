package com.teasgen.keyraces.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class GroupServer {
    private static final int PORT = 5619;
    // TODO: change THIS TIME
    public static final int GROUP_CREATION_TIME = 30_000; // 30 secs
    public static final int GAME_DURATION = 180_000;      // 3 minutes
    public static final int TIME_STEP = 1_000;         // 1 sec
    public volatile List<Group> groups;
    private static final Random rnd = new Random();
    private static final Object lock = new Object();

    public GroupServer() {
        groups = new ArrayList<>();
    }
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("The server was started at port " + PORT);
            while (true) {
                ClientThread newClient;
                synchronized (lock) {
                    Socket clientSocket = serverSocket.accept();
                    newClient = new ClientThread(clientSocket);
                    System.out.println("New client connected");
                    Group availableGroup = getAvailableGroup();
                    if (availableGroup == null) {
                        availableGroup = new Group();
                        groups.add(availableGroup);
                        Timer groupFormationTimer = new Timer();
                        Timer timeToStartTask = new Timer();
                        timeToStartTask.schedule(new TimeToStartTask(availableGroup), 0, TIME_STEP);
                        groupFormationTimer.schedule(new GroupFormationTask(availableGroup, timeToStartTask), GROUP_CREATION_TIME);
                    }
                    availableGroup.addClient(newClient);
                }
                newClient.start();
            }
        } catch (IOException e) {
            System.out.println("The server was disabled");
        }
    }

    /**
     * find which group new client can be connected
     * @return group if there are some unfilled groups, otherwise null
     */
    public Group getAvailableGroup() {
        for (Group group : groups) {
            if (group.isOpened())
                return group;
        }
        return null;
    }

    /**
     * @param clientThread client Socket
     * @return group the client with this socket belongs to
     */
    public Group getClientGroup(ClientThread clientThread) {
        for (Group group : groups) {
            if (group.getClients().contains(clientThread))
                return group;
        }
        return null;
    }

    public class ClientThread extends Thread {
        private String playerName;
        private final Socket clientSocket;
        private int totalCnt;
        private int errorCnt;
        private volatile boolean running;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private final Timer sendStats = new Timer();
        public ClientThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }
        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());
                this.running = true;
                playerName = (String) in.readObject();
                System.out.println(playerName);
                Group group = getClientGroup(this);
                group.sendMessageToAllMembers("Now your group consists of:\n" + group, 1);

                while (true) {
                    ClientStats clientStats = (ClientStats) in.readObject();
                    totalCnt = clientStats.totalNumber();
                    errorCnt = clientStats.errorsNumber();
                    System.out.println(totalCnt + " " + errorCnt + " " + clientStats.time());
                    if (clientStats.time() == -1) {
                        running = false;
                        System.out.println("Client " + playerName + " was disconnected");
                        group.removeClient(this);
                        if (group.isEmpty()) {
                            groups.remove(group);
                        } else {
                            group.sendMessageToAllMembers("The player " + this.playerName + " from your group was disconnected", 1);
                        }
                        System.out.println("Groups: ");
                        for (Group group1 : groups) {
                            for (ClientThread clientThread : group1.clients)
                                System.out.println(clientThread.playerName);
                        }
                        break;
                    }
                }
            } catch (IOException e) {
                remove();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } finally {
                close();
            }
        }
        public void setTimer() {
            sendStats.schedule(new ClientStatisticsTimer(this), 0, TIME_STEP);
        }
        public void close() {
            try {
                out.close();
                in.close();
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("The Pipe has already been closed");
            }
        }

        private void remove() {
            running = false;
            System.out.println("Client " + playerName + " was disconnected");
            Group group = getClientGroup(this);
            System.out.println("kek remove");
            if (group == null)
                return;
            group.removeClient(this);
            if (group.isEmpty()) {
                groups.remove(group);
            } else {
                group.sendMessageToAllMembers("The player " + this.playerName + " from your group was disconnected", 1);
            }
        }

        public int getTotalCnt() {
            return totalCnt;
        }

        public int getErrorCnt() {
            return errorCnt;
        }

        public boolean isRunning() {
            return running;
        }
        public String getPlayerName() {
            return playerName;
        }
    }

    public class Group {
        private static final int MAX_CLIENTS = 3;
        private final List<ClientThread> clients;
        private volatile boolean opened;
        private int textLength;
        /**
         * true if all players have already written the text
         * otherwise false
         */
        private boolean genius;
        public Group() {
            clients = new ArrayList<>();
            opened = true;
        }

        public boolean isOpened() {
            return opened;
        }

        public void close() {
            opened = false;
        }

        public List<ClientThread> getClients() {
            return clients;
        }
        public boolean isEmpty() {
            return clients.isEmpty();
        }
        public boolean isFull() {
            return clients.size() == MAX_CLIENTS;
        }
        public synchronized void addClient(ClientThread client) {
            if (!opened)
                throw new IllegalStateException();
            clients.add(client);
            if (isFull())
                opened = false;
        }
        public void removeClient(ClientThread client) {
            clients.remove(client);
        }
        public void sendMessageToAllMembers(String message, int code) {
            for (ClientThread clientThread : clients) {
                try {
                    clientThread.out.writeInt(code);
                    clientThread.out.writeObject(message);
                } catch (IOException | NullPointerException e) {
                    System.out.println("Connection to " + clientThread.playerName + " was lost");
                }
            }
        }

        @Override
        public String toString() {
            StringBuilder res = new StringBuilder();
            for (ClientThread clientThread : clients)
                res.append(clientThread.playerName).append("; ");
            return res.toString();
        }

        public int getTextLength() {
            return textLength;
        }

        public void setTextLength(int textLength) {
            this.textLength = textLength;
        }

        public boolean isGenius() {
            return genius;
        }

        public void setGenius(boolean genius) {
            this.genius = genius;
        }

    }
    public class ClientStatisticsTimer extends TimerTask {
        GroupServer.ClientThread client;
        int time;
        public ClientStatisticsTimer(GroupServer.ClientThread client) {
            this.client = client;
            this.time = GAME_DURATION;
        }

        /**
         * The action to be performed by this timer task.
         */
        @Override
        public void run() {
            Group group = getClientGroup(client);
            if (group == null) {
                client.sendStats.cancel();
                return;
            }
            GroupStats current = new GroupStats(group, time / 1000, client);
            time -= TIME_STEP;
            try {
                if (time == 0 || group.isGenius()) {
                    System.out.println(group.isGenius());
                    client.out.writeInt(3);
                    client.out.writeObject(current);
                    client.out.flush();
                    throw new IOException("the game ended"); // need to cancel the timer
                } else {
                    client.out.writeInt(2);
                    client.out.writeObject(current);
                    client.out.flush();
                }
            } catch (IOException e) {
                client.sendStats.cancel();
            }
        }
    }
    public static void main(String[] args) {
        GroupServer server = new GroupServer();
        server.start();
    }
}