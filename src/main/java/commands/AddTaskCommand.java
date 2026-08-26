package commands;

import exceptions.YourHandException;
import storage.Storage;
import tasks.Task;
import tasks.TaskList;
import ui.Ui;

/** Adds one parsed task to the task list. */
public class AddTaskCommand extends Command {
    private final Task task;

    /** Creates a command that adds the given task. */
    public AddTaskCommand(Task task) {
        this.task = task;
    }

    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) throws YourHandException {
        int existingTaskNumber = taskList.findTaskNumberByDescription(task.getDescription());
        if (existingTaskNumber != -1) {
            ui.showDuplicateTaskWarning(existingTaskNumber);
        }
        taskList.add(task);
        saveTasks(taskList, storage);
        ui.showTaskAdded(task, taskList.size());
    }
}
