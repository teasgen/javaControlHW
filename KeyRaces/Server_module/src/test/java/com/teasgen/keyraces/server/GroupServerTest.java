package com.teasgen.keyraces.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class GroupServerTest {
    private GroupServer groupServer;
    private GroupServer.Group group;
    private GroupServer.Group group2;
    private GroupServer.ClientThread client1;
    private GroupServer.ClientThread client2;

    @BeforeEach
    public void setUp() {
        groupServer = new GroupServer();
        group = groupServer.new Group();
        group2 = groupServer.new Group();
        client1 = groupServer.new ClientThread(null);
        client2 = groupServer.new ClientThread(null);

        List<GroupServer.Group> groups = new ArrayList<>();
        groups.add(group);
        groups.add(group2);
        groupServer.groups = groups;
    }
    @Test
    public void isOpened_NewGroup_IsOpened() {
        assertTrue(group.isOpened());
    }

    @Test
    public void close_ClosesGroup_IsNotOpened() {
        group.close();
        assertFalse(group.isOpened());
    }

    @Test
    public void addClient_NotFullGroup_ClientAdded() {
        group.addClient(client1);
        assertTrue(group.getClients().contains(client1));
    }

    @Test
    public void addClient_FullGroup_ThrowsException() {
        group.addClient(client1);
        group.addClient(client2);
        group.addClient(groupServer.new ClientThread(null));
    }

    @Test
    public void removeClient_ClientInGroup_ClientRemoved() {
        group.addClient(client1);
        group.addClient(client2);

        group.removeClient(client1);

        assertFalse(group.getClients().contains(client1));
        assertTrue(group.getClients().contains(client2));
    }

    @Test
    public void removeClient_ClientNotInGroup_NoChanges() {
        group.addClient(client1);
        group.addClient(client2);

        GroupServer.ClientThread client3 = groupServer.new ClientThread(null);
        group.removeClient(client3);

        assertTrue(group.getClients().contains(client1));
        assertTrue(group.getClients().contains(client2));
    }

    @Test
    public void isEmpty_EmptyGroup_ReturnsTrue() {
        assertTrue(group.isEmpty());
    }

    @Test
    public void isEmpty_NonEmptyGroup_ReturnsFalse() {
        group.addClient(client1);
        assertFalse(group.isEmpty());
    }

    @Test
    public void isFull_NotFullGroup_ReturnsFalse() {
        group.addClient(client1);
        assertFalse(group.isFull());
    }

    @Test
    public void getAvailableGroup_OpenedGroupExists_ReturnsOpenGroup() {
        group.close();
        group2.close();

        GroupServer.Group availableGroup = groupServer.getAvailableGroup();

        assertNull(availableGroup);
    }

    @Test
    public void getAvailableGroup_NoOpenedGroupExists_ReturnsNull() {
        group.close();
        group2.close();

        GroupServer.Group availableGroup = groupServer.getAvailableGroup();

        assertNull(availableGroup);
    }

    @Test
    public void getClientGroup_ClientInGroup_ReturnsGroup() {
        group.addClient(client1);

        GroupServer.Group clientGroup = groupServer.getClientGroup(client1);

        assertEquals(group, clientGroup);
    }

    @Test
    public void getClientGroup_ClientNotInGroup_ReturnsNull() {
        GroupServer.Group clientGroup = groupServer.getClientGroup(client1);

        assertNull(clientGroup);
    }
}