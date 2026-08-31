package mira;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import mira.exception.MiraException;

class MiraTest {
    @TempDir
    private Path tempDirectory;

    @Test
    void getResponse_validCommands_updatesStateAndBuildsResponses() throws MiraException {
        Mira mira = new Mira(tempDirectory.resolve("mira.txt"));

        assertTrue(mira.getResponse("todo read a book").contains("I've added this task"));
        assertTrue(mira.getResponse("list").contains("[T][ ] read a book"));
        assertTrue(mira.getResponse("mark 1").contains("[T][X] read a book"));
        assertTrue(mira.getResponse("find BOOK").contains("[T][X] read a book"));
        assertTrue(mira.getResponse("delete 1").contains("I've removed this task"));
        assertEquals("Your task list is empty.", mira.getResponse("list"));
    }

    @Test
    void getResponse_invalidCommand_returnsRecoverableError() throws MiraException {
        Mira mira = new Mira(tempDirectory.resolve("mira.txt"));

        assertEquals("OOPS!!! Please enter a command.", mira.getResponse(""));
        assertTrue(mira.getResponse("something else").startsWith("OOPS!!!"));
        assertEquals("Bye. Hope to see you again soon!", mira.getResponse("bye"));
    }

    @Test
    void constructor_existingDataFile_restoresTasksForNewSession() throws MiraException {
        Path dataFile = tempDirectory.resolve("mira.txt");
        Mira originalSession = new Mira(dataFile);
        originalSession.getResponse("todo persisted task");

        Mira restoredSession = new Mira(dataFile);

        assertTrue(restoredSession.getResponse("list").contains("persisted task"));
    }
}
