package com.teasgen.keyraces.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupStats implements Serializable {
    public List<String> names = new ArrayList<>();
    public List<Integer> totalCount = new ArrayList<>();
    public List<Integer> errorCount = new ArrayList<>();
    public List<Boolean> itsMe = new ArrayList<>();
    public List<Boolean> disconnected = new ArrayList<>();
    public int remainTime, textLength;
    public GroupStats(GroupServer.Group group, int remainTime, GroupServer.ClientThread client) {
        if (group == null)
            return;
        boolean everyoneHasWritten = true;
        for (GroupServer.ClientThread clientThread : group.getClients()) {
            names.add(clientThread.getPlayerName());
            totalCount.add(clientThread.getTotalCnt());
            everyoneHasWritten &= (clientThread.getTotalCnt() == group.getTextLength());
            errorCount.add(clientThread.getErrorCnt());
            itsMe.add(client.equals(clientThread));
            disconnected.add(!client.isRunning());
        }
        group.setGenius(everyoneHasWritten);
        this.remainTime = remainTime;
        this.textLength = group.getTextLength();
    }
}
