/**
 * Represents a task that starts and ends at specified dates or times.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an incomplete event with the given description, start, and end.
     *
     * @param description Text describing the task.
     * @param from Start date or time text.
     * @param to End date or time text.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /** Returns this event's display text, including its start and end times. */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
