package com.teasgen.keyraces.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The GroupStats class represents the statistics of a group in the game.
 * It holds information about the players' names, total counts, error counts,
 * their own status, and disconnection status. It also includes the remaining
 * time and the length of the text for the group
 */
public class GroupStats implements Serializable {
    public List<String> names = new ArrayList<>();
    public List<Integer> totalCount = new ArrayList<>();
    public List<Integer> errorCount = new ArrayList<>();
    public List<Boolean> itsMe = new ArrayList<>();
    public List<Boolean> disconnected = new ArrayList<>();
    public int remainTime, textLength;
    /**
     * Constructs a GroupStats object with the specified group, remaining time,
     * and client. It initializes the statistics based on the clients in the group.
     *
     * @param group       the Group object representing the group
     * @param remainTime  the remaining time for the group
     * @param client      the ClientThread object representing the client
     */
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
