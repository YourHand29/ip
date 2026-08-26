package tasks;

/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    private final TaskDateTime by;

    /**
     * Creates an incomplete deadline with the given description and due time.
     *
     * @param description Text describing the task.
     * @param by Due date or date and time.
     */
    public Deadline(String description, TaskDateTime by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
    }

    /** Returns this deadline's ISO due date or date-time in data-file format. */
    @Override
    protected String getFileDetails() {
        return " | " + by.toStorageString();
    }

    /** Returns this deadline's display text, including its due date or date and time. */
    @Override
    public String toString() {
        return super.toString() + " (by: " + by.toDisplayString() + ")";
    }
}
