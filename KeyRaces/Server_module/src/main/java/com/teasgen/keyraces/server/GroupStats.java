package com.teasgen.keyraces.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupStats implements Serializable {
    public final List<String> names = new ArrayList<>();
    public final List<Integer> totalCount = new ArrayList<>();
    public final List<Integer> errorCount = new ArrayList<>();
    public final List<Boolean> itsMe = new ArrayList<>();
    public final List<Boolean> disconnected = new ArrayList<>();
    public final long remainTime;
    public GroupStats(GroupServer.Group group, long remainTime, GroupServer.ClientThread client) {
        for (GroupServer.ClientThread clientThread : group.getClients()) {
            if (!clientThread.isRunning())
                break;
            names.add(clientThread.getName());
            totalCount.add(clientThread.getTotalCnt());
            errorCount.add(clientThread.getErrorCnt());
            itsMe.add(client.equals(clientThread));
            disconnected.add(clientThread.isConnected());
        }
        this.remainTime = remainTime;
    }
}
