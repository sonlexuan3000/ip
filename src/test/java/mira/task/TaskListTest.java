package mira.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import mira.exception.MiraException;

class TaskListTest {
    @Test
    void setDone_validAndBoundaryNumbers_updatesSelectedTask() throws MiraException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("last"));

        Task first = tasks.setDone(1, true);
        Task last = tasks.setDone(2, true);
        assertTrue(first.isDone());
        assertTrue(last.isDone());

        tasks.setDone(1, false);
        assertFalse(first.isDone());
        assertTrue(last.isDone());
    }

    @Test
    void delete_validNumber_removesAndRenumbersTasks() throws MiraException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("middle"));
        tasks.add(new Todo("last"));

        Task deleted = tasks.delete(2);

        assertEquals("middle", deleted.getDescription());
        assertEquals(2, tasks.size());
        assertEquals("last", tasks.get(2).getDescription());
    }

    @Test
    void get_outOfRangeNumber_throwsWithoutChangingList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("only"));

        assertThrows(MiraException.class, () -> tasks.get(0));
        assertThrows(MiraException.class, () -> tasks.get(2));
        assertEquals(1, tasks.size());
        assertEquals("only", tasks.asList().get(0).getDescription());
    }
}
