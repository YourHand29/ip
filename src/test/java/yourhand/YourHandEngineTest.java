package yourhand;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import yourhand.storage.Storage;
import yourhand.tasks.TaskList;

/** Tests command execution independently of the graphical user interface. */
class YourHandEngineTest {
    @Test
    public void execute_addTask_returnsConfirmation() {
        YourHandEngine engine = createEngine();

        String response = engine.execute("todo borrow book");

        assertTrue(response.contains("I've written this down"));
        assertTrue(response.contains("borrow book"));
    }

    @Test
    public void execute_findTask_returnsMatchingTask() {
        YourHandEngine engine = createEngine();
        engine.execute("todo borrow book");
        engine.execute("todo buy milk");

        String response = engine.execute("find BOOK");

        assertTrue(response.contains("borrow book"));
        assertFalse(response.contains("buy milk"));
    }

    @Test
    public void execute_invalidCommand_returnsErrorMessage() {
        YourHandEngine engine = createEngine();

        String response = engine.execute("hello");

        assertTrue(response.contains("I don't speak that yet"));
    }

    private YourHandEngine createEngine() {
        Path testFile = Path.of("build", "test-data", "engine-test.txt");
        return new YourHandEngine(new Storage(testFile), new TaskList());
    }
}
