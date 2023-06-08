package com.teasgen.keyraces.server;

import java.util.TimerTask;

public class CheckConnectionTask extends TimerTask {
    GroupServer.Group group;
    public CheckConnectionTask(GroupServer.Group group) {
        this.group = group;
    }
    @Override
    public void run() {
//        group.sendMessageToAllMembers("", 1);
    }
}