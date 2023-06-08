package com.teasgen.keyraces.server;

import java.util.Date;
import java.util.TimerTask;

import static com.teasgen.keyraces.server.GroupServer.GROUP_CREATION_TIME;

public class TimeToStartTask extends TimerTask {
    GroupServer.Group group;
    int times;
    public TimeToStartTask(GroupServer.Group group) {
        this.group = group;
        this.times = GROUP_CREATION_TIME / 1000;
    }
    @Override
    public void run() {
        if (times == 0)
            return;
        group.sendMessageToAllMembers(String.valueOf(times), 5);
        --times;
    }
}