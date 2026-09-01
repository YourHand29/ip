package yourhand.storage;

import yourhand.exceptions.CorruptFileException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import yourhand.tasks.Deadline;
import yourhand.tasks.Event;
import yourhand.tasks.TaskList;
import yourhand.tasks.TaskDateTime;
import yourhand.tasks.Todo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests for task persistence and validation of saved data. */
class StorageTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    public void load_missingDataFile_returnsEmptyTaskList() throws IOException, CorruptFileException {
        Storage storage = new Storage(temporaryDirectory.resolve("yourhand.txt"));

        TaskList loadedTasks = storage.load();

        assertTrue(loadedTasks.isEmpty());
    }

    @Test
    void saveAndLoad_validTasks_preservesTypesDetailsAndCompletionStatus()
            throws IOException, CorruptFileException {
        Path dataFile = temporaryDirectory.resolve("yourhand.txt");
        Storage storage = new Storage(dataFile);
        TaskList originalTasks = new TaskList();
        Todo todo = new Todo("read book");
        todo.markAsDone();
        originalTasks.add(todo);
        originalTasks.add(new Deadline("submit report", new TaskDateTime(LocalDate.of(2026, 8, 26))));
        originalTasks.add(new Event(
                "team meeting",
                new TaskDateTime(LocalDateTime.of(2026, 8, 26, 18, 0)),
                new TaskDateTime(LocalDateTime.of(2026, 8, 26, 20, 0))));

        storage.save(originalTasks);
        TaskList loadedTasks = storage.load();

        assertEquals(List.of(
                "[T][X] read book",
                "[D][ ] submit report (by: Aug 26 2026)",
                "[E][ ] team meeting (from: Aug 26 2026 6:00pm to: Aug 26 2026 8:00pm)"),
                loadedTasks.getTasks().stream().map(Object::toString).toList());
    }

    @Test
    public void load_malformedSavedEntry_throwsCorruptFileException() throws IOException {
        Path dataFile = temporaryDirectory.resolve("yourhand.txt");
        Files.writeString(dataFile, "D | 0 | submit report | 2026-02-30", StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        assertThrows(CorruptFileException.class, storage::load);
    }

    @Test
    public void load_eventEndingBeforeStart_throwsCorruptFileException() throws IOException {
        Path dataFile = temporaryDirectory.resolve("yourhand.txt");
        Files.writeString(dataFile, "E | 0 | meeting | 2026-08-27 | 2026-08-26", StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        assertThrows(CorruptFileException.class, storage::load);
    }
}
