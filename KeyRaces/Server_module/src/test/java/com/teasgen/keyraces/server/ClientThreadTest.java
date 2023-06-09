package com.teasgen.keyraces.server;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClientThreadTest {

    private GroupServer.ClientThread client;
    private GroupServer groupServer;
    @BeforeEach
    public void setUP() {
        groupServer = new GroupServer();
        client = groupServer.new ClientThread(null);
    }

    @BeforeEach
    public void setUp() {
        client = groupServer.new ClientThread(null);
    }

    @Test
    public void getTotalCnt_InitialValueIsZero_ReturnsZero() {
        assertEquals(0, client.getTotalCnt());
    }

    @Test
    public void getErrorCnt_InitialValueIsZero_ReturnsZero() {
        assertEquals(0, client.getErrorCnt());
    }

    @Test
    public void isRunning_InitialValueIsTrue_ReturnsFalse() {
        assertFalse(client.isRunning());
    }

    @Test
    public void getPlayerName_InitialValueIsNull_ReturnsNull() {
        assertNull(client.getPlayerName());
    }
}
