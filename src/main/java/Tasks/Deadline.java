package tasks;

/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    private final String by;

    /**
     * Creates an incomplete deadline with the given description and due time.
     *
     * @param description Text describing the task.
     * @param by Due date or time text.
     */
    public Deadline(String description, String by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
    }

    /** Returns this deadline's display text, including its due date or time. */
    @Override
    public String toString() {
        return super.toString() + " (by: " + by + ")";
    }
}
