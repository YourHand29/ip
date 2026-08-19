public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an incomplete event with the given description, start, and end.
     *
     * @param description text describing the task
     * @param from start date or time text
     * @param to end date or time text
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + this.from + " to: " + this.to + ")";
    }
}
