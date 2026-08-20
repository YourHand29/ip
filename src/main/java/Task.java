/**
 * Represents a task and whether it has been completed.
 */
public abstract class Task {
    private final String description;
    private final TaskType taskType;
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description Text describing the task.
     * @param taskType Category of the task.
     */
    protected Task(String description, TaskType taskType) {
        this.description = description;
        this.taskType = taskType;
        this.isDone = false;
    }

    /**
     * Returns the symbol used to show this task's completion status.
     *
     * @return {@code X} when done, otherwise a space.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Marks this task as done.
     *
     * @return True if the task changed from not done to done.
     */
    public boolean markAsDone() {
        if (isDone) {
            return false;
        }
        isDone = true;
        return true;
    }

    /**
     * Marks this task as not done.
     *
     * @return True if the task changed from done to not done.
     */
    public boolean markAsUndone() {
        if (!isDone) {
            return false;
        }
        isDone = false;
        return true;
    }

    /** Returns the task's display text, including its completion status. */
    @Override
    public String toString() {
        return "[" + taskType.getIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
