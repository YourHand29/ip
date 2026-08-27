package yourhand.ui;

import yourhand.exceptions.YourHandException;
import yourhand.tasks.Task;
import yourhand.tasks.TaskList;

import java.util.Scanner;

/**
 * Handles console input and all messages shown by YourHand.
 */
public class Ui {
    private static final String SEPARATOR = "____________________________________________________________";
    private final Scanner scanner = new Scanner(System.in);

    /** Displays the application banner and greeting. */
    public void showWelcomeMessage() {
        String banner = "__   __                 _   _                 _\n"
                + "\\ \\ / /__  _   _ _ __  | | | | __ _ _ __   __| |\n"
                + " \\ V / _ \\| | | | '__| | |_| |/ _` | '_ \\ / _` |\n"
                + "  | | (_) | |_| | |    |  _  | (_| | | | | (_| |\n"
                + "  |_|\\___/ \\__,_|_|    |_| |_|\\__,_|_| |_|\\__,_|";
        System.out.println(banner);
        showSeparator();
        System.out.println(" Selamat Datang 早上好! YourHand 为你服务");
        System.out.println(" 你来这干嘛 What are you here for?");
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
        System.out.println(SEPARATOR);
    }

    /** Displays the farewell message. */
    public void showGoodbyeMessage() {
        System.out.println(" See you never :)");
    }

    /** Displays an error message caused by a user command. */
    public void showError(String message) {
        System.out.println(" " + message);
    }

    /** Displays every task in the current list, or an empty-list message. */
    public void showTaskList(TaskList taskList) {
        if (taskList.isEmpty()) {
            System.out.println(" Your task list is empty.");
            return;
        }

        System.out.println(" Here's your list of responsibilities:");
        int taskNumber = 1;
        for (Task task : taskList.getTasks()) {
            System.out.println(" " + taskNumber + "." + task);
            taskNumber++;
        }
    }

    /** Displays tasks whose descriptions contain the given keyword. */
    public void showSearchResults(TaskList taskList, String keyword) throws YourHandException {
        var matchingTaskNumbers = taskList.findTaskNumbersByDescriptionKeyword(keyword);
        if (matchingTaskNumbers.isEmpty()) {
            System.out.println(" No tasks found containing \"" + keyword + "\".");
            return;
        }
        System.out.println(" Here are the matching tasks in your list:");
        for (int taskNumber : matchingTaskNumbers) {
            System.out.println(" " + taskNumber + "." + taskList.getTask(taskNumber));
        }
    }

    /** Warns that a task with the same description already exists. */
    public void showDuplicateTaskWarning(int existingTaskNumber) {
        System.out.println(" Heads up: task " + existingTaskNumber + " already has that description."
                + " I'll add this one too.");
    }

    /** Displays confirmation that a task was added. */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println(" Fine, I've written this down:");
        System.out.println("   " + task);
        String taskWord = taskCount == 1 ? "task" : "tasks";
        System.out.println(" That's " + taskCount + " " + taskWord + " on your plate.");
    }

    /** Displays the result of marking or unmarking a task. */
    public void showTaskStatus(Task task, boolean isMarkCommand, boolean wasUpdated) {
        if (isMarkCommand) {
            System.out.println(wasUpdated
                    ? " Good job for surviving. I'll mark this as done:"
                    : " That task was already done. Double-checking never hurts:");
        } else {
            System.out.println(wasUpdated
                    ? " Unmarked. Check pls:"
                    : " That task was already waiting for you. No change:");
        }
        System.out.println("   " + task);
    }

    /** Displays confirmation that a task was removed. */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println(" Poof. I've removed this task:");
        System.out.println("   " + task);
        String taskWord = taskCount == 1 ? "task" : "tasks";
        System.out.println(" That's " + taskCount + " " + taskWord + " left on your plate.");
    }

    /** Informs the user that no tasks were loaded from malformed saved data. */
    public void showCorruptFileWarning() {
        System.out.println(" Your saved data file looks corrupted, so I didn't load it.");
    }

    /** Informs the user that saved data could not be read. */
    public void showLoadingError() {
        System.out.println(" I couldn't load your saved tasks. Starting with a clean slate.");
    }
}
