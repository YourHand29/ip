package commands;

import exceptions.YourHandException;
import storage.Storage;
import tasks.TaskList;
import ui.Ui;

/** Displays every task currently in the list. */
public class ListCommand extends Command {
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) throws YourHandException {
        ui.showTaskList(taskList);
    }
}
