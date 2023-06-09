package com.teasgen.keyraces.server;

import java.util.TimerTask;

import static com.teasgen.keyraces.server.GroupServer.GROUP_CREATION_TIME;

/**
 * The TimeToStartTask class represents a timer task that handles the countdown
 * for group start time in the GroupServer.
 */
public class TimeToStartTask extends TimerTask {
    GroupServer.Group group;
    int times;
    /**
     * Constructs a TimeToStartTask object with the specified group.
     *
     * @param group the Group object representing the group
     */
    public TimeToStartTask(GroupServer.Group group) {
        this.group = group;
        this.times = GROUP_CREATION_TIME / 1000;
    }
    /**
     * Executes the task of the timer.
     * Sends a countdown message to all group members and decreases the remaining time.
     */
    @Override
    public void run() {
        if (times == 0)
            return;
        group.sendMessageToAllMembers(String.valueOf(times), 5);
        --times;
    }
}