package com.teasgen.keyraces.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClientViewModelTest {
    private ClientViewModel clientViewModel;

    @BeforeEach
    public void setUp() {
        clientViewModel = new ClientViewModel();
    }

    @Test
    public void testInitialValues() {
        assertEquals("Soon start", clientViewModel.timeProperty().get());
        assertNull(clientViewModel.tableProperty().get());
        assertNull(clientViewModel.textProperty().get());
        assertTrue(clientViewModel.disabledProperty().get());
        assertFalse(clientViewModel.wantTryAgainProperty().get());
        assertFalse(clientViewModel.showAllProperty().get());
        assertEquals(0, clientViewModel.getTotalNumber());
        assertEquals(0, clientViewModel.getErrorsNumber());
    }

    @Test
    public void testSetDisabled() {
        clientViewModel.setDisabled(true);
        assertTrue(clientViewModel.disabledProperty().get());

        clientViewModel.setDisabled(false);
        assertFalse(clientViewModel.disabledProperty().get());
    }

    @Test
    public void testSetWantTryAgain() {
        clientViewModel.setWantTryAgain(true);
        assertTrue(clientViewModel.wantTryAgainProperty().get());

        clientViewModel.setWantTryAgain(false);
        assertFalse(clientViewModel.wantTryAgainProperty().get());
    }

    @Test
    public void testSetTime() {
        clientViewModel.setTime("New time");
        assertEquals("New time", clientViewModel.timeProperty().get());
    }

    @Test
    public void testSetTable() {
        clientViewModel.setTable("New table");
        assertEquals("New table", clientViewModel.tableProperty().get());
    }

    @Test
    public void testSetText() {
        clientViewModel.setText("New text");
        assertEquals("New text", clientViewModel.textProperty().get());
    }

    @Test
    public void testIncreaseTotalNumber() {
        clientViewModel.increaseTotalNumber();
        assertEquals(1, clientViewModel.getTotalNumber());

        clientViewModel.increaseTotalNumber();
        assertEquals(2, clientViewModel.getTotalNumber());
    }

    @Test
    public void testIncreaseErrorsNumber() {
        clientViewModel.increaseErrorsNumber();
        assertEquals(1, clientViewModel.getErrorsNumber());

        clientViewModel.increaseErrorsNumber();
        assertEquals(2, clientViewModel.getErrorsNumber());
    }

    @Test
    public void testSetEndgame() {
        clientViewModel.setEndgame("New endgame");
        assertEquals("New endgame", clientViewModel.endgameProperty().get());
    }

    @Test
    public void testSetShowAll() {
        clientViewModel.setShowAll(true);
        assertTrue(clientViewModel.showAllProperty().get());

        clientViewModel.setShowAll(false);
        assertFalse(clientViewModel.showAllProperty().get());
    }

    @Test
    public void testReset() {
        clientViewModel.setDisabled(true);
        clientViewModel.setWantTryAgain(true);
        clientViewModel.setTime("New time");
        clientViewModel.setTable("New table");
        clientViewModel.setText("New text");
        clientViewModel.increaseTotalNumber();
        clientViewModel.increaseErrorsNumber();
        clientViewModel.setEndgame("New endgame");
        clientViewModel.setShowAll(true);

        clientViewModel.reset();

        assertEquals("Soon start", clientViewModel.timeProperty().get());
        assertEquals("", clientViewModel.tableProperty().get());
        assertEquals("", clientViewModel.textProperty().get());
        assertTrue(clientViewModel.disabledProperty().get());
        assertFalse(clientViewModel.wantTryAgainProperty().get());
        assertFalse(clientViewModel.showAllProperty().get());
        assertEquals(0, clientViewModel.getTotalNumber());
        assertEquals(0, clientViewModel.getErrorsNumber());
        assertEquals("Text", clientViewModel.endgameProperty().get());
    }
}
