package yourhand.tasks;

/**
 * Represents a task that starts and ends at specified dates or times.
 */
public class Event extends Task {
    private final TaskDateTime from;
    private final TaskDateTime to;

    /**
     * Creates an incomplete event with the given description, start, and end.
     *
     * @param description Text describing the task.
     * @param from Start date or date and time.
     * @param to End date or date and time.
     */
    public Event(String description, TaskDateTime from, TaskDateTime to) {
        super(description, TaskType.EVENT);
        this.from = from;
        this.to = to;
    }

    /** Returns this event's ISO start and end dates or date-times in data-file format. */
    @Override
    protected String getFileDetails() {
        return " | " + from.toStorageString() + " | " + to.toStorageString();
    }

    /** Returns this event's display text, including its start and end dates or date-times. */
    @Override
    public String toString() {
        return super.toString() + " (from: " + from.toDisplayString()
                + " to: " + to.toDisplayString() + ")";
    }
}
