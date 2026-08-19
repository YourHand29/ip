/**
 * Represents a task and whether it has been completed.
 */
public class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description text describing the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the symbol used to show this task's completion status.
     *
     * @return {@code X} when done, otherwise a space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsUndone() {
        isDone = false;
    }

    /** Returns the task's display text, including its completion status. */
    @Override
    public String toString() {
        return "[" + this.getStatusIcon() + "] " + this.description;
    }
}
