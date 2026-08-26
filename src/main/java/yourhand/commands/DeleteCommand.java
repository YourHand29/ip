package yourhand.commands;

import yourhand.exceptions.YourHandException;
import yourhand.storage.Storage;
import yourhand.tasks.Task;
import yourhand.tasks.TaskList;
import yourhand.ui.Ui;

/** Removes a task by its one-based task number. */
public class DeleteCommand extends Command {
    private final int taskNumber;

    /** Creates a command that removes the task with the given number. */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) throws YourHandException {
        Task removedTask = taskList.removeTask(taskNumber);
        saveTasks(taskList, storage);
        ui.showTaskDeleted(removedTask, taskList.size());
    }
}
