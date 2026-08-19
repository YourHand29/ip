public class ToDo extends Task {
    /**
     * Creates an incomplete task with the given description.
     *
     * @param description text describing the task
     */
    public ToDo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
