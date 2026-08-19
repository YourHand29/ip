public class Deadline extends Task {
    private final String by;

    /**
     * Creates an incomplete deadline with the given description and due time.
     *
     * @param description text describing the task
     * @param by due date or time text
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + this.by + ")";
    }
}
