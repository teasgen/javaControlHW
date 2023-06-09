package com.teasgen.keyraces.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * The GroupServer class represents a server that manages groups and clients for a key racing game.
 * It handles client connections, group formation, client communication, and game statistics.
 */
public class GroupServer {
    private static final int PORT = 5619;
    // TODO: change THIS TIME
    public static final int GROUP_CREATION_TIME = 30_000; // 30 secs
    public static final int GAME_DURATION = 180_000;      // 3 minutes
    public static final int TIME_STEP = 1_000;            // 1 sec
    public volatile List<Group> groups;
    private static final Random rnd = new Random();
    private static final Object lock = new Object();

    /**
     * Constructs a GroupServer object.
     * Initializes the list of groups.
     */
    public GroupServer() {
        groups = new ArrayList<>();
    }
    /**
     * Starts the GroupServer by creating a server socket and accepting client connections.
     * Manages the formation of groups and starts the client threads.
     */
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
     * Finds which group new client can be connected to
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

    /**
     * The ClientThread class represents a thread for handling client communication and game statistics.
     */
    public class ClientThread extends Thread {
        private String playerName;
        private final Socket clientSocket;
        private int totalCnt;
        private int errorCnt;
        private volatile boolean running;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private final Timer sendStats = new Timer();
        /**
         * Constructs a ClientThread object with the specified client socket.
         *
         * @param clientSocket the client socket
         */
        public ClientThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }
        /**
         * Executes the client thread's logic for handling communication and game statistics.
         */
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
                    if (clientStats.time() == -1) {
                        running = false;
                        System.out.println("Client " + playerName + " was disconnected");
                        synchronized (group.clients) {
                            group.removeClient(this);
                            if (group.isEmpty()) {
                                groups.remove(group);
                            } else {
                                group.sendMessageToAllMembers("The player " + this.playerName + " from your group was disconnected", 1);
                            }
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
        /**
         * Sets up the timer for sending countdown time.
         */
        public void setTimer() {
            sendStats.schedule(new ClientStatisticsTimer(this), 0, TIME_STEP);
        }
        /**
         * Closes the input/output streams and the client socket.
         */
        public void close() {
            try {
                out.close();
                in.close();
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("The Pipe has already been closed");
            }
        }

        /**
         * Removes the client from the group and handles disconnection.
         */
        private void remove() {
            running = false;
            System.out.println("Client " + playerName + " was disconnected");
            Group group = getClientGroup(this);
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

    /**
     * The Group class represents a group of clients playing together.
     */
    public class Group {
        private static final int MAX_CLIENTS = 3;
        private final List<ClientThread> clients;
        private volatile boolean opened;
        private int textLength;
        /**
         * True if all players have already written the text
         * Otherwise false
         */
        private boolean genius;
        /**
         * Constructs a Group object.
         * Initializes the list of clients and sets the group as opened.
         */
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
        /**
         * Adds a client to the group.
         * If the group is full, it will be closed for new clients.
         *
         * @param client the client to add
         */
        public synchronized void addClient(ClientThread client) {
            if (!opened)
                throw new IllegalStateException();
            clients.add(client);
            if (isFull())
                opened = false;
        }
        /**
         * Removes a client from the group.
         *
         * @param client the client to remove
         */
        public void removeClient(ClientThread client) {
            clients.remove(client);
        }
        /**
         * Sends a message to all members of the group.
         *
         * @param message the message to send
         * @param code    the code associated with the message
         */
        public void sendMessageToAllMembers(String message, int code) {
            synchronized (clients) {
                for (ClientThread clientThread : clients) {
                    try {
                        clientThread.out.writeInt(code);
                        clientThread.out.writeObject(message);
                    } catch (IOException | NullPointerException e) {
                        System.out.println("Connection to " + clientThread.playerName + " was lost");
                    }
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
    /**
     * The ClientStatisticsTimer class is responsible for sending client statistics at regular intervals.
     */
    public class ClientStatisticsTimer extends TimerTask {
        GroupServer.ClientThread client;
        int time;
        public ClientStatisticsTimer(GroupServer.ClientThread client) {
            this.client = client;
            this.time = GAME_DURATION;
        }

        /**
         * Executes the timer task by sending client statistics to the associated client.
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