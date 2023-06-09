package com.teasgen.keyraces.server;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GroupStatsTest {
    @Test
    public void testGroupStatsConstructorWithNullGroup() {
        GroupStats groupStats = new GroupStats(null, 5, null);

        assertTrue(groupStats.names.isEmpty());
        assertTrue(groupStats.totalCount.isEmpty());
        assertTrue(groupStats.errorCount.isEmpty());
        assertTrue(groupStats.itsMe.isEmpty());
        assertTrue(groupStats.disconnected.isEmpty());
        assertEquals(0, groupStats.remainTime);
        assertEquals(0, groupStats.textLength);
    }
    @Test
    public void testGroupStatsInitialization() {
        GroupServer groupServer = new GroupServer();
        GroupServer.Group group = groupServer.new Group();
        group.addClient(groupServer.new ClientThread(null));
        group.addClient(groupServer.new ClientThread(null));
        group.addClient(groupServer.new ClientThread(null));
        int textLength = 200;
        group.setTextLength(textLength);
        int remainTime = 100;

        GroupStats groupStats = new GroupStats(group, remainTime, groupServer.new ClientThread(null));

        assertEquals(3, groupStats.names.size());
        assertEquals(3, groupStats.totalCount.size());
        assertEquals(3, groupStats.errorCount.size());
        assertEquals(3, groupStats.itsMe.size());
        assertEquals(3, groupStats.disconnected.size());
        assertEquals(remainTime, groupStats.remainTime);
        assertEquals(textLength, groupStats.textLength);
    }
}
