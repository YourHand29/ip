package yourhand.tasks;

import yourhand.exceptions.YourHandException;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores the tasks managed during one YourHand session.
 */
public class TaskList {
    private final List<Task> tasks = new ArrayList<>();

    /**
     * Adds a task to the list.
     *
     * @param task Task to store.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Returns a task using the one-based number shown to the user.
     *
     * @param taskNumber One-based task number.
     * @return The requested task.
     * @throws YourHandException If the task number is outside the current list.
     */
    public Task getTask(int taskNumber) throws YourHandException {
        validateTaskNumber(taskNumber);
        return tasks.get(taskNumber - 1);
    }

    /**
     * Removes and returns a task using the one-based number shown to the user.
     *
     * @param taskNumber One-based task number.
     * @return The removed task.
     * @throws YourHandException If the task number is outside the current list.
     */
    public Task removeTask(int taskNumber) throws YourHandException {
        validateTaskNumber(taskNumber);
        return tasks.remove(taskNumber - 1);
    }

    /** Checks whether a one-based task number refers to a stored task. */
    private void validateTaskNumber(int taskNumber) throws YourHandException {
        if (tasks.isEmpty()) {
            throw new YourHandException("Brother I free how delete stuff. Add one before I can even delete.");
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new YourHandException("Pick a task number from 1 to " + tasks.size() + ".");
        }
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return The current task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether the list has no tasks.
     *
     * @return True if there are no tasks.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns an immutable snapshot of the current tasks.
     *
     * @return Current tasks in their display order.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Returns the number of the first task with the given description.
     *
     * @param description Description to search for.
     * @return The one-based task number, or {@code -1} when no task matches.
     */
    public int findTaskNumberByDescription(String description) {
        for (int index = 0; index < tasks.size(); index++) {
            if (tasks.get(index).getDescription().equalsIgnoreCase(description)) {
                return index + 1;
            }
        }
        return -1;
    }
}
