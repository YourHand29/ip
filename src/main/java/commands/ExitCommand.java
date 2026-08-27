package commands;

import exceptions.YourHandException;
import storage.Storage;
import tasks.TaskList;
import ui.Ui;

/** Ends the application after displaying its farewell message. */
public class ExitCommand extends Command {
    /** {@inheritDoc} */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) throws YourHandException {
        ui.showGoodbyeMessage();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isExit() {
        return true;
    }
}
