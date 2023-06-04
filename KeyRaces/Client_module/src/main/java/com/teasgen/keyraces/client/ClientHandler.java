package com.teasgen.keyraces.client;

import com.teasgen.keyraces.server.ClientStats;
import com.teasgen.keyraces.server.GroupStats;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler extends Thread {
    private final int port;
    public volatile boolean running;
    private volatile int totalNumber;
    private volatile int errorsNumber;
    public ClientHandler(int port) {
        this.port = port;
        this.running = true;
        this.totalNumber = 0;
        this.errorsNumber = 0;
    }
    @Override
    public void run() {
        try (Socket socket = new Socket("localhost", this.port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            out.writeObject("Vlad");
            out.flush();
            while (this.running) {
                int action = in.readInt();
                switch (action) {
                    case 1 -> System.out.println(in.readObject());
                    case 2 -> {
                        GroupStats groupStats = (GroupStats) in.readObject();
                        System.out.println(groupStats.remainTime);
                        // TODO: send to GUI
                        out.writeObject(new ClientStats(totalNumber, errorsNumber));
                    }
                    case 3 -> {
                        GroupStats finalStats = (GroupStats) in.readObject();
                        System.out.println(finalStats.names);
                        // TODO: send to GUI
                    }
                    default -> System.out.println("Wtf");
                }
                if (action == 3)
                    break;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("The connection was lost");
        } finally {
            running = false;
        }
    }
}
