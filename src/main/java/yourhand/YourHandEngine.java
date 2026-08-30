package yourhand;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import yourhand.commands.Command;
import yourhand.exceptions.YourHandException;
import yourhand.storage.Storage;
import yourhand.tasks.TaskList;
import yourhand.ui.Ui;

/** Executes YourHand commands independently of a particular user interface. */
public class YourHandEngine {
    private final Storage storage;
    private final TaskList taskList;

    /** Creates an engine using the default YourHand data file. */
    public YourHandEngine() {
        storage = new Storage();
        taskList = YourHand.loadTasks(storage, new Ui());
    }

    /** Executes one command and returns the text that should be shown to the user. */
    public String execute(String command) {
        ByteArrayOutputStream response = new ByteArrayOutputStream();
        Ui ui = new Ui(new PrintStream(response));
        try {
            Command parsedCommand = YourHand.parseCommand(command.trim());
            parsedCommand.execute(taskList, ui, storage);
        } catch (YourHandException exception) {
            ui.showError(exception.getMessage());
        }
        return response.toString();
    }
}
