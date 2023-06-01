package com.teasgen.keyraces.client;

import java.io.*;
import java.net.Socket;
import com.teasgen.keyraces.server.GroupStats;

public class Client {
    private static final int PORT = 5619;
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            out.writeObject("Vlad");
            out.flush();
            while (true) {
                Object now;
                try {
                    now = in.readObject();
                    String s = (String) now;
                    System.out.println(s);
                } catch (ClassCastException e) {
                    GroupStats groupStats = (GroupStats) in.readObject();
                    System.out.println(groupStats.remainTime);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("The connection was lost");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}