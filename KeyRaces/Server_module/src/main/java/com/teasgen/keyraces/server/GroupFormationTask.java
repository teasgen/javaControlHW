package com.teasgen.keyraces.server;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Class for waiting N seconds
 * When the time is over it closes the group and starts the game
 */
public class GroupFormationTask extends TimerTask {

    /**
     * Current group
     */
    GroupServer.Group group;
    Timer timer;
    InputStream inputStream;
    List<String> textNames = new ArrayList<>();
    Random random = new Random();
    int waitingTime = 5000;
    /**
     * Just constructor
     * @param group instance of tracked group
     */
    public GroupFormationTask(GroupServer.Group group, Timer timeToStart) {
        this.group = group;
        this.timer = timeToStart;

        textNames.add("text1");
        textNames.add("text2");

        inputStream = GroupServer.class.getResourceAsStream(getRandomTextPath());
    }
    /**
     * The action to be performed by this timer task.
     * Choose new text randomly and sends it to the group
     * Then waiting 5 seconds and starts the game for all players
     */
    @Override
    public void run() {
        this.timer.cancel();
        group.close();
        group.sendMessageToAllMembers("5 seconds left", 1);
        String text = readTextFromInputStream();
        group.setTextLength(text.length());
        group.sendMessageToAllMembers(text, 4);
        try {
            Thread.sleep(waitingTime); // TODO: change THIS TIME
        } catch (InterruptedException e) {
            System.out.println("The server was disconnected");
        }

        for (GroupServer.ClientThread clientThread : group.getClients())
            clientThread.setTimer();
    }

    /**
     * @return File input stream
     */
    String readTextFromInputStream() {
        try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8)) {
            return scanner.useDelimiter("\\A").next();
        }
    }

    /**
     * @return path to new text
     */
    String getRandomTextPath() {
        int num = random.nextInt(textNames.size()) + 1;
        return "/texts/text" + num;
    }
}