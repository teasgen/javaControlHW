package com.teasgen.keyraces.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class GroupServer {
    private static final int PORT = 5619;
    // TODO: change THIS TIME
    private static final int GROUP_CREATION_TIME = 1_000; // 30 secs
    private static final int GAME_DURATION = 10_000;      // 3 minutes
    private static final int TIME_STEP = 1_000;         // 1 sec
    public volatile List<Group> groups;
    private static final Random rnd = new Random();
    public GroupServer() {
        groups = new ArrayList<>();
    }
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("The server was started at port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientThread newClient = new ClientThread(clientSocket);
                System.out.println("New client connected");

                Group availableGroup = getAvailableGroup();
                if (availableGroup == null) {
                    availableGroup = new Group();
                    groups.add(availableGroup);
                    Timer groupFormationTimer = new Timer();
                    groupFormationTimer.schedule(new GroupFormationTask(availableGroup), GROUP_CREATION_TIME);
                }
                availableGroup.addClient(newClient);
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
    public synchronized Group getAvailableGroup() {
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
                Group group = getClientGroup(this);
                group.sendMessageToAllMembers("Now your group consists of:\n" + group);
                synchronized (group.lock) { // wait until the game starts
                    group.lock.wait();
                }
                sendStats.schedule(new ClientStatisticsTimer(this), 0, TIME_STEP);
                while (true) {
                    ClientStats clientStats = (ClientStats) in.readObject();
                    totalCnt = clientStats.totalNumber();
                    errorCnt = clientStats.errorsNumber();
                    System.out.println(totalCnt + " " + errorCnt);
                }
            } catch (IOException | InterruptedException e) {
                running = false;
                System.out.println("Client " + playerName + " was disconnected");
                Group group = getClientGroup(this);
                if (group == null)
                    throw new RuntimeException();
                group.removeClient(this);
                if (group.isEmpty()) {
                    groups.remove(group);
                } else {
                    group.sendMessageToAllMembers("The player " + this.playerName + " from your group was disconnected");
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    out.close();
                    in.close();
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Stranger things with client....");
                }
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
        public final Object lock;
        private final Date startTime = new Date();
        private int textLength;
        /**
         * true if all players have already written the text
         * otherwise false
         */
        private boolean genius;
        public Group() {
            clients = new ArrayList<>();
            opened = true;
            lock = new Object();
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
        public synchronized void sendMessageToAllMembers(String message) {
            for (ClientThread clientThread : clients) {
                try {
                    clientThread.out.writeInt(1);
                    clientThread.out.writeObject(message);
                } catch (IOException e) {
                    System.out.println("Connection to " + clientThread.playerName + " was lost");
                }
            }
        }

        @Override
        public String toString() {
            StringBuilder res = new StringBuilder();
            for (ClientThread clientThread : clients)
                res.append(clientThread.playerName).append(", ");
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
        public ClientStatisticsTimer(GroupServer.ClientThread client) {
            this.client = client;
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
            long time = (GAME_DURATION - (new Date()).getTime() + group.startTime.getTime()) / 1000;
            GroupStats current = new GroupStats(group, time, client);
            System.out.println(time);
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