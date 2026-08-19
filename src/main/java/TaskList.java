/**
 * Stores the tasks managed during one YourHand session.
 */
public class TaskList {
    private static final int MAX_TASKS = 100;

    private final Task[] tasks = new Task[MAX_TASKS];
    private int size;

    /**
     * Adds a task to the list.
     *
     * @param task task to store
     * @throws YourHandException if the list already contains 100 tasks
     */
    public void add(Task task) throws YourHandException {
        if (size == MAX_TASKS) {
            throw new YourHandException("My brain is full. Please remove a task before adding another.");
        }
        tasks[size] = task;
        size++;
    }

    /**
     * Returns a task using the one-based number shown to the user.
     *
     * @param taskNumber one-based task number
     * @return the requested task
     * @throws YourHandException if the task number is outside the current list
     */
    public Task getTask(int taskNumber) throws YourHandException {
        if (size == 0) {
            throw new YourHandException("I have no tasks to work with yet. Add one before using its number.");
        }
        if (taskNumber < 1 || taskNumber > size) {
            throw new YourHandException("Pick a task number from 1 to " + size + ".");
        }
        return tasks[taskNumber - 1];
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the current task count
     */
    public int size() {
        return size;
    }

    /**
     * Returns whether the list has no tasks.
     *
     * @return true if there are no tasks
     */
    public boolean isEmpty() {
        return size == 0;
    }
}
