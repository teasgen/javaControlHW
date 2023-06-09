package com.teasgen.keyraces.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GroupFormationTaskTest {

    private GroupServer.Group group;
    private Timer timer;
    private InputStream inputStream;
    private List<String> textNames;

    @BeforeEach
    public void setUp() {
        GroupServer groupServer = new GroupServer();
        group = groupServer.new Group();
        timer = new Timer();
        textNames = new ArrayList<>(Arrays.asList("text1", "text2"));
    }

    @Test
    public void testRun() {
        String testText = "Test Text";
        inputStream = new ByteArrayInputStream(testText.getBytes(StandardCharsets.UTF_8));
        GroupFormationTask groupFormationTask = new GroupFormationTask(group, timer);
        groupFormationTask.inputStream = inputStream;
        groupFormationTask.waitingTime = 0;

        groupFormationTask.run();

        assertFalse(group.isOpened());
        assertEquals(testText.length(), group.getTextLength());

        groupFormationTask.cancel();
    }

    @Test
    public void testReadTextFromInputStream() {
        String testText = "Test Text";
        inputStream = new ByteArrayInputStream(testText.getBytes(StandardCharsets.UTF_8));
        GroupFormationTask groupFormationTask = new GroupFormationTask(group, timer);
        groupFormationTask.inputStream = inputStream;

        String result = groupFormationTask.readTextFromInputStream();

        assertEquals(testText, result);
    }
}
