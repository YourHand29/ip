package yourhand.ui;

import yourhand.exceptions.YourHandException;
import yourhand.tasks.Task;
import yourhand.tasks.TaskList;

import java.util.Scanner;
import java.io.PrintStream;

/**
 * Handles console input and all messages shown by YourHand.
 */
public class Ui {
    private static final String SEPARATOR = "____________________________________________________________";
    private final Scanner scanner = new Scanner(System.in);
    private final PrintStream output;

    /** Creates a UI that writes responses to standard output. */
    public Ui() {
        this(System.out);
    }

    /** Creates a UI that writes responses to the given output stream. */
    public Ui(PrintStream output) {
        this.output = output;
    }

    /** Displays the application banner and greeting. */
    public void showWelcomeMessage() {
        String banner = "__   __                 _   _                 _\n"
                + "\\ \\ / /__  _   _ _ __  | | | | __ _ _ __   __| |\n"
                + " \\ V / _ \\| | | | '__| | |_| |/ _` | '_ \\ / _` |\n"
                + "  | | (_) | |_| | |    |  _  | (_| | | | | (_| |\n"
                + "  |_|\\___/ \\__,_|_|    |_| |_|\\__,_|_| |_|\\__,_|";
        output.println(banner);
        showSeparator();
        output.println(" Selamat Datang 早上好! YourHand 为你服务");
        output.println(" 你来这干嘛 What are you here for?");
        showSeparator();
    }

    /** Returns whether another command can be read from the console. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Reads and trims the next user command. */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Displays the separator used around command responses. */
    public void showSeparator() {
        output.println(SEPARATOR);
    }

    /** Displays the farewell message. */
    public void showGoodbyeMessage() {
        output.println(" See you never :)");
    }

    /** Displays an error message caused by a user command. */
    public void showError(String message) {
        output.println(" " + message);
    }

    /** Displays every task in the current list, or an empty-list message. */
    public void showTaskList(TaskList taskList) {
        if (taskList.isEmpty()) {
            output.println(" Your task list is empty.");
            return;
        }

        output.println(" Here's your list of responsibilities:");
        int taskNumber = 1;
        for (Task task : taskList.getTasks()) {
            output.println(" " + taskNumber + "." + task);
            taskNumber++;
        }
    }

    /** Displays tasks whose descriptions contain the given keyword. */
    public void showSearchResults(TaskList taskList, String keyword) throws YourHandException {
        var matchingTaskNumbers = taskList.findTaskNumbersByDescriptionKeyword(keyword);
        if (matchingTaskNumbers.isEmpty()) {
            output.println(" No tasks found containing \"" + keyword + "\".");
            return;
        }
        output.println(" Here are the matching tasks in your list:");
        for (int taskNumber : matchingTaskNumbers) {
            output.println(" " + taskNumber + "." + taskList.getTask(taskNumber));
        }
    }

    /** Warns that a task with the same description already exists. */
    public void showDuplicateTaskWarning(int existingTaskNumber) {
        output.println(" Heads up: task " + existingTaskNumber + " already has that description."
                + " I'll add this one too.");
    }

    /** Displays confirmation that a task was added. */
    public void showTaskAdded(Task task, int taskCount) {
        output.println(" Fine, I've written this down:");
        output.println("   " + task);
        String taskWord = taskCount == 1 ? "task" : "tasks";
        output.println(" That's " + taskCount + " " + taskWord + " on your plate.");
    }

    /** Displays the result of marking or unmarking a task. */
    public void showTaskStatus(Task task, boolean isMarkCommand, boolean wasUpdated) {
        if (isMarkCommand) {
            output.println(wasUpdated
                    ? " Good job for surviving. I'll mark this as done:"
                    : " That task was already done. Double-checking never hurts:");
        } else {
            output.println(wasUpdated
                    ? " Unmarked. Check pls:"
                    : " That task was already waiting for you. No change:");
        }
        output.println("   " + task);
    }

    /** Displays confirmation that a task was removed. */
    public void showTaskDeleted(Task task, int taskCount) {
        output.println(" Poof. I've removed this task:");
        output.println("   " + task);
        String taskWord = taskCount == 1 ? "task" : "tasks";
        output.println(" That's " + taskCount + " " + taskWord + " left on your plate.");
    }

    /** Informs the user that no tasks were loaded from malformed saved data. */
    public void showCorruptFileWarning() {
        output.println(" Your saved data file looks corrupted, so I didn't load it.");
    }

    /** Informs the user that saved data could not be read. */
    public void showLoadingError() {
        output.println(" I couldn't load your saved tasks. Starting with a clean slate.");
    }
}
