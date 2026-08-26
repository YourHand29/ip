package tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests for task state changes and common task representations. */
class TaskTest {

    @Test
    void newTodo_isIncompleteAndUsesTodoRepresentations() {
        Task task = new Todo("read book");

        assertEquals(" ", task.getStatusIcon());
        assertEquals("[T][ ] read book", task.toString());
        assertEquals("T | 0 | read book", task.toFileString());
    }

    @Test
    void markAsDone_incompleteTask_marksTaskAndReportsChange() {
        Task task = new Todo("read book");

        assertTrue(task.markAsDone());
        assertEquals("X", task.getStatusIcon());
        assertEquals("T | 1 | read book", task.toFileString());
        assertFalse(task.markAsDone());
    }

    @Test
    void markAsUndone_doneTask_marksTaskUndoneAndReportsChange() {
        Task task = new Todo("read book");
        task.markAsDone();

        assertTrue(task.markAsUndone());
        assertEquals(" ", task.getStatusIcon());
        assertFalse(task.markAsUndone());
    }
}
