package yourhand;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import yourhand.commands.Command;
import yourhand.exceptions.YourHandException;
import yourhand.storage.Storage;
import yourhand.tasks.TaskList;
import yourhand.ui.Ui;

/** Executes YourHand commands independently of a particular user interface. */
public class YourHandEngine {
    private static final Logger LOGGER = Logger.getLogger(YourHandEngine.class.getName());
    private final Storage storage;
    private final TaskList taskList;

    /** Creates an engine using the default YourHand data file. */
    public YourHandEngine() {
        storage = new Storage();
        taskList = YourHand.loadTasks(storage, new Ui());
    }

    /** Creates an engine with collaborators supplied by the caller. */
    public YourHandEngine(Storage storage, TaskList taskList) {
        this.storage = Objects.requireNonNull(storage, "engine storage must be provided");
        this.taskList = Objects.requireNonNull(taskList, "engine task list must be provided");
    }

    /** Executes one command and returns the text that should be shown to the user. */
    public String execute(String command) {
        ByteArrayOutputStream response = new ByteArrayOutputStream();
        Ui ui = new Ui(new PrintStream(response));
        try {
            Command parsedCommand = YourHand.parseCommand(command);
            parsedCommand.execute(taskList, ui, storage);
        } catch (YourHandException exception) {
            ui.showError(exception.getMessage());
        } catch (RuntimeException exception) {
            LOGGER.log(Level.WARNING, "Unexpected error while executing a command", exception);
            ui.showError("I couldn't complete that command. Please try again or type help.");
        }
        return response.toString();
    }

    /** Returns the current task list for graphical views. */
    TaskList getTaskList() {
        return taskList;
    }
}
