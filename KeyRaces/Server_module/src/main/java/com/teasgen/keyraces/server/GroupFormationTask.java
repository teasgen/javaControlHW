package com.teasgen.keyraces.server;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Scanner;
import java.util.TimerTask;

/**
 * Class for waiting N seconds
 * When the time is over it closes the group and starts the game
 */
public class GroupFormationTask extends TimerTask {

    /**
     * Current group
     */
    GroupServer.Group group;

    /**
     * Just constructor
     * @param group instance of tracked group
     */
    public GroupFormationTask(GroupServer.Group group) {
        this.group = group;
    }
    /**
     * The action to be performed by this timer task.
     */
    @Override
    public void run() {
        group.close();
        group.sendMessageToAllMembers("5 seconds left: ", 1);
        InputStream inputStream = GroupServer.class.getResourceAsStream("/texts/text1");
        if (inputStream == null) {
            return;
        }
        String text = new Scanner(inputStream, StandardCharsets.UTF_8).useDelimiter("\\A").next();
        System.out.println(text);
        group.setTextLength(text.length());
        group.sendMessageToAllMembers(text, 4);
        try {
            Thread.sleep(2000); // TODO: change THIS TIME
        } catch (InterruptedException e) {
            System.out.println("The server was disconnected");
        }
        group.setStartTime(new Date());
        synchronized (group.lock) { // START THE GAME FOR THIS GROUP
            group.lock.notifyAll();
        }
    }
}