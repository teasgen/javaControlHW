package com.teasgen.keyraces.client;

import com.teasgen.keyraces.server.GroupStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

public class ClientHandlerTest {
    private ClientHandler clientHandler;
    @BeforeEach
    public void setup() {
        ClientViewModel clientViewModel = new ClientViewModel();

        clientHandler = new ClientHandler("localhost", 5619, "Vlad", clientViewModel);
    }

    @Test
    public void testFormTable() {
        GroupStats groupStats = new GroupStats(null, 5, null);
        groupStats.names = new ArrayList<>(Arrays.asList("Player1", "Player2"));
        groupStats.totalCount = new ArrayList<>(Arrays.asList(10, 8));
        groupStats.errorCount = new ArrayList<>(Arrays.asList(2, 3));
        groupStats.disconnected = new ArrayList<>(Arrays.asList(false, false));
        groupStats.itsMe = new ArrayList<>(Arrays.asList(false, false));
        groupStats.textLength = 20;

        String table = clientHandler.formTable(groupStats);

        assertEquals("""
                Player1 50.00%, 2 mistakes, 3 sym/min
                Player2 40.00%, 3 mistakes, 2 sym/min
                """, table);
    }

}
