package com.teasgen.keyraces.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InitialViewModelTest {
    private InitialViewModel initialViewModel;

    @BeforeEach
    public void setUp() {
        initialViewModel = new InitialViewModel();
    }

    @Test
    public void testInitialValues() {
        assertNull(initialViewModel.addressProperty().get());
        assertNull(initialViewModel.portProperty().get());
        assertNull(initialViewModel.nameProperty().get());
    }

    @Test
    public void testGetAddress() {
        initialViewModel.addressProperty().set("127.0.0.1");
        assertEquals("127.0.0.1", initialViewModel.getAddress());
    }

    @Test
    public void testGetName() {
        initialViewModel.nameProperty().set("John");
        assertEquals("John", initialViewModel.getName());
    }

    @Test
    public void testGetPort() {
        initialViewModel.portProperty().set("1234");
        assertEquals("1234", initialViewModel.getPort());
    }

    @Test
    public void testFillBlankOrIncorrectValues() {
        initialViewModel.fillBlankOrIncorrectValues();

        assertEquals("localhost", initialViewModel.addressProperty().get());
        assertEquals(String.valueOf(InitialViewModel.PORT), initialViewModel.portProperty().get());
        assertEquals("Vlad", initialViewModel.nameProperty().get());
    }

    @Test
    public void testFillBlankOrIncorrectValues_WithCorrectValues() {
        initialViewModel.addressProperty().set("192.168.0.1");
        initialViewModel.portProperty().set("5678");
        initialViewModel.nameProperty().set("Alice");

        initialViewModel.fillBlankOrIncorrectValues();

        assertEquals("192.168.0.1", initialViewModel.addressProperty().get());
        assertEquals("5678", initialViewModel.portProperty().get());
        assertEquals("Alice", initialViewModel.nameProperty().get());
    }

    @Test
    public void testFillBlankOrIncorrectValues_WithIncorrectPort() {
        initialViewModel.addressProperty().set("10.0.0.1");
        initialViewModel.portProperty().set("abcd");
        initialViewModel.nameProperty().set("Bob");

        initialViewModel.fillBlankOrIncorrectValues();

        assertEquals("10.0.0.1", initialViewModel.addressProperty().get());
        assertEquals(String.valueOf(InitialViewModel.PORT), initialViewModel.portProperty().get());
        assertEquals("Bob", initialViewModel.nameProperty().get());
    }
}
