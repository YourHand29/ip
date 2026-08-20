import exceptions.YourHandException;
import tasks.Deadline;
import tasks.Event;
import tasks.Task;
import tasks.TaskList;
import tasks.Todo;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A command-line chatbot that manages to-dos, deadlines, and events.
 */
public class YourHand {
    private static final String SEPARATOR = "____________________________________________________________";
    private static final Pattern TODO_PATTERN = Pattern.compile("^todo(?:\\s+(.*))?$");
    private static final Pattern DEADLINE_PATTERN = Pattern.compile("^deadline\\s+(.+?)\\s+/by\\s+(.+)$");
    private static final Pattern EVENT_PATTERN = Pattern.compile(
            "^event\\s+(.+?)\\s+/from\\s+(.+?)\\s+/to\\s+(.+)$");
    private static final Pattern STATUS_PATTERN = Pattern.compile("^(mark|unmark)(?:\\s+(.+))?$");
    private static final Pattern DELETE_PATTERN = Pattern.compile("^delete(?:\\s+(.+))?$");

    /**
     * Starts the YourHand command-line application.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        printWelcomeMessage();

        Scanner scanner = new Scanner(System.in);
        TaskList taskList = new TaskList();
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();
            System.out.println(SEPARATOR);
            try {
                if (handleCommand(command, taskList)) {
                    System.out.println(SEPARATOR);
                    break;
                }
            } catch (YourHandException exception) {
                System.out.println(" " + exception.getMessage());
            }
            System.out.println(SEPARATOR);
        }
    }

    /** Prints the chatbot banner and initial greeting. */
    private static void printWelcomeMessage() {
        String banner = "__   __                 _   _                 _\n"
                + "\\ \\ / /__  _   _ _ __  | | | | __ _ _ __   __| |\n"
                + " \\ V / _ \\| | | | '__| | |_| |/ _` | '_ \\ / _` |\n"
                + "  | | (_) | |_| | |    |  _  | (_| | | | | (_| |\n"
                + "  |_|\\___/ \\__,_|_|    |_| |_|\\__,_|_| |_|\\__,_|";
        System.out.println(banner);
        System.out.println(SEPARATOR);
        System.out.println(" Selamat Datang 早上好! YourHand 为你服务");
        System.out.println(" 你来这干嘛 What are you here for?");
        System.out.println(SEPARATOR);
    }

    /**
     * Handles one command and returns whether the application should exit.
     *
     * @param command user-entered command
     * @param taskList task storage for this session
     * @return true when the command is {@code bye}
     * @throws YourHandException if the command is invalid
     */
    private static boolean handleCommand(String command, TaskList taskList) throws YourHandException {
        if (command.equals("bye")) {
            System.out.println(" See you never :)");
            return true;
        }
        if (command.equals("list")) {
            printTaskList(taskList);
            return false;
        }

        Matcher statusMatcher = STATUS_PATTERN.matcher(command);
        if (statusMatcher.matches()) {
            updateTaskStatus(statusMatcher, taskList);
            return false;
        }

        Matcher deleteMatcher = DELETE_PATTERN.matcher(command);
        if (deleteMatcher.matches()) {
            deleteTask(deleteMatcher, taskList);
            return false;
        }

        Task task = createTask(command);
        taskList.add(task);
        System.out.println(" Fine, I've written this down:");
        System.out.println("   " + task);
        String taskWord = taskList.size() == 1 ? "task" : "tasks";
        System.out.println(" That's " + taskList.size() + " " + taskWord + " on your plate.");
        return false;
    }

    /** Prints all stored tasks, or an empty-list message. */
    private static void printTaskList(TaskList taskList) throws YourHandException {
        if (taskList.isEmpty()) {
            System.out.println(" Your task list is empty.");
            return;
        }

        System.out.println(" Here's your list of responsibilities:");
        for (int taskNumber = 1; taskNumber <= taskList.size(); taskNumber++) {
            System.out.println(" " + taskNumber + "." + taskList.getTask(taskNumber));
        }
    }

    /** Updates a task's completion status from a validated mark or unmark command. */
    private static void updateTaskStatus(Matcher matcher, TaskList taskList) throws YourHandException {
        String taskNumberText = matcher.group(2);
        if (taskNumberText == null || taskNumberText.isBlank()) {
            throw new YourHandException("Don't make me guess — give me a task number, e.g. "
                    + matcher.group(1) + " 2.");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(taskNumberText.trim());
        } catch (NumberFormatException exception) {
            throw new YourHandException("Task numbers are whole numbers, not creative writing.");
        }

        Task task = taskList.getTask(taskNumber);
        if (matcher.group(1).equals("mark")) {
            if (task.markAsDone()) {
                System.out.println(" Good job for surviving. I'll mark this as done:");
            } else {
                System.out.println(" That task was already done. Double-checking never hurts:");
            }
        } else {
            if (task.markAsUndone()) {
                System.out.println(" Unmarked. Check pls:");
            } else {
                System.out.println(" That task was already waiting for you. No change:");
            }
        }
        System.out.println("   " + task);
    }

    /** Removes the task specified by a validated delete command. */
    private static void deleteTask(Matcher matcher, TaskList taskList) throws YourHandException {
        String taskNumberText = matcher.group(1);
        if (taskNumberText == null || taskNumberText.isBlank()) {
            throw new YourHandException("Don't make me guess — give me a task number, e.g. delete 2.");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(taskNumberText.trim());
        } catch (NumberFormatException exception) {
            throw new YourHandException("Task numbers are whole numbers, not creative writing.");
        }

        Task removedTask = taskList.removeTask(taskNumber);
        System.out.println(" Poof. I've removed this task:");
        System.out.println("   " + removedTask);
        String taskWord = taskList.size() == 1 ? "task" : "tasks";
        System.out.println(" That's " + taskList.size() + " " + taskWord + " left on your plate.");
    }

    /** Creates a task from a task-creation command. */
    private static Task createTask(String command) throws YourHandException {
        Matcher todoMatcher = TODO_PATTERN.matcher(command);
        if (todoMatcher.matches()) {
            String description = todoMatcher.group(1);
            if (description == null || description.isBlank()) {
                throw new YourHandException("You handed me an empty to-do. Try: todo borrow book");
            }
            return new Todo(description.trim());
        }

        Matcher deadlineMatcher = DEADLINE_PATTERN.matcher(command);
        if (deadlineMatcher.matches()) {
            return new Deadline(deadlineMatcher.group(1).trim(), deadlineMatcher.group(2).trim());
        }
        if (isCommand(command, "deadline")) {
            throw new YourHandException("Bro due when please. Try: deadline DESCRIPTION /by DATE_OR_TIME");
        }

        Matcher eventMatcher = EVENT_PATTERN.matcher(command);
        if (eventMatcher.matches()) {
            return new Event(eventMatcher.group(1).trim(), eventMatcher.group(2).trim(), eventMatcher.group(3).trim());
        }
        if (isCommand(command, "event")) {
            throw new YourHandException("Walao when the even happening. Try: event DESCRIPTION /from START /to END");
        }

        throw new YourHandException("Hmm, I don't speak that yet. Try todo, deadline, event, list, mark, "
                + "unmark, delete, or bye.");
    }

    /** Returns whether a command begins with a command word. */
    private static boolean isCommand(String command, String commandWord) {
        return command.equals(commandWord) || command.startsWith(commandWord + " ");
    }
}
