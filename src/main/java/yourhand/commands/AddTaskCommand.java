package yourhand.commands;

import yourhand.exceptions.YourHandException;
import yourhand.storage.Storage;
import yourhand.tasks.Task;
import yourhand.tasks.TaskList;
import yourhand.ui.Ui;

/** Adds one parsed task to the task list. */
public class AddTaskCommand extends Command {
    private final Task task;

    /** Creates a command that adds the given task. */
    public AddTaskCommand(Task task) {
        this.task = task;
    }

    /** {@inheritDoc} */
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
