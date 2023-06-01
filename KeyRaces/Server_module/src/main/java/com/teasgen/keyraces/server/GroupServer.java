package com.teasgen.keyraces.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class GroupServer {
    private static final int PORT = 5619;
    private static final int GROUP_CREATION_TIME = 1_000; // 30 secs
    private static final int GAME_DURATION = 180_000;      // 3 minutes
    private static final int TIME_STEP = 1_000;         // 1 sec
    public List<Group> groups;
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
        private String name;
        private final Socket clientSocket;
        private int totalCnt;
        private int errorCnt;
        private boolean running;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        public ClientThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }
        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());
                name = (String) in.readObject();
                Group group = getClientGroup(this);
                group.sendMessageToAllMembers("Now your group consists of:\n" + group);
                synchronized (group.lock) { // wait until the game starts
                    group.lock.wait();
                }
                Timer sendStats = new Timer();
                sendStats.schedule(new ClientStatisticsTimer(this), 0, TIME_STEP);
                while (true) {}
            } catch (IOException | InterruptedException e) {
                System.out.println("Client " + clientSocket + " was disconnected");
                Group group = getClientGroup(this);
                if (group == null)
                    throw new RuntimeException();
                group.removeClient(this);
                if (group.isEmpty()) {
                    groups.remove(group);
                } else {
                    // TODO: message to all user in this group about player disconnection
                    for (ClientThread clientThread : group.clients) {
                        try {
                            clientThread.out.writeUTF("The player " + this.name + " from your group was disconnected");
                        } catch (IOException ex) {
                            // this client was disconnected too
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    in.close();
                    out.close();
                    clientSocket.close();
                } catch (IOException e) {
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

        public boolean isConnected() {
            if (!clientSocket.isConnected()) {
                running = false;
                return false;
            }
            return true;
        }
        public ObjectInputStream getIn() {
            return in;
        }
        public ObjectOutputStream getOut() {
            return out;
        }
    }

    public class Group {
        private static final int MAX_CLIENTS = 3;
        private final List<ClientThread> clients;
        private volatile boolean opened;
        public final Object lock;
        private final Date startTime = new Date();
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
                    clientThread.out.writeObject(message);
                } catch (IOException e) {
                    System.out.println("Connection to " + clientThread.name + " was lost");
                }
            }
        }

        @Override
        public String toString() {
            StringBuilder res = new StringBuilder();
            for (ClientThread clientThread : clients)
                res.append(clientThread.name).append(", ");
            return res.toString();
        }

        public Date getStartTime() {
            return startTime;
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
            long time = GAME_DURATION / 1000 - ((new Date()).getTime() - group.startTime.getTime()) / 1000;
            GroupStats current = new GroupStats(group, time, client);
            System.out.println(time);
            try {
                client.out.writeObject(current);
//                client.out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        GroupServer server = new GroupServer();
        server.start();
    }
}