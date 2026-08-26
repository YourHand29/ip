import exceptions.CorruptFileException;
import exceptions.YourHandException;
import tasks.Deadline;
import tasks.Event;
import tasks.Task;
import tasks.TaskDateTime;
import tasks.TaskList;
import tasks.Todo;
import storage.Storage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
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
    private static final DateTimeFormatter ISO_DATE_FORMAT = DateTimeFormatter.ofPattern("uuuu-M-d")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter ISO_COMPACT_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-M-d HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter ISO_COLON_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-M-d HH:mm")
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter SLASH_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);

    /**
     * Starts the YourHand command-line application.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        printWelcomeMessage();

        Scanner scanner = new Scanner(System.in);
        Storage storage = new Storage();
        TaskList taskList = loadTasks(storage);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();
            System.out.println(SEPARATOR);
            try {
                if (handleCommand(command, taskList, storage)) {
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
    private static boolean handleCommand(String command, TaskList taskList, Storage storage) throws YourHandException {
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
            updateTaskStatus(statusMatcher, taskList, storage);
            return false;
        }

        Matcher deleteMatcher = DELETE_PATTERN.matcher(command);
        if (deleteMatcher.matches()) {
            deleteTask(deleteMatcher, taskList, storage);
            return false;
        }

        Task task = createTask(command);
        printDuplicateTaskWarning(task, taskList);
        taskList.add(task);
        saveTasks(taskList, storage);
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
    private static void updateTaskStatus(Matcher matcher, TaskList taskList, Storage storage) throws YourHandException {
        String taskNumberText = matcher.group(2);
        if (taskNumberText == null || taskNumberText.isBlank()) {
            throw new YourHandException("Don't make me guess — give me a task number, e.g. "
                    + matcher.group(1) + " 2.");
        }

        int taskNumber = parseNumber(
                taskNumberText,
                "Task numbers are whole numbers, not creative writing.");

        Task task = taskList.getTask(taskNumber);
        boolean wasUpdated;
        if (matcher.group(1).equals("mark")) {
            wasUpdated = task.markAsDone();
            if (wasUpdated) {
                System.out.println(" Good job for surviving. I'll mark this as done:");
            } else {
                System.out.println(" That task was already done. Double-checking never hurts:");
            }
        } else {
            wasUpdated = task.markAsUndone();
            if (wasUpdated) {
                System.out.println(" Unmarked. Check pls:");
            } else {
                System.out.println(" That task was already waiting for you. No change:");
            }
        }
        if (wasUpdated) {
            saveTasks(taskList, storage);
        }
        System.out.println("   " + task);
    }

    /** Removes the task specified by a validated delete command. */
    private static void deleteTask(Matcher matcher, TaskList taskList, Storage storage) throws YourHandException {
        String taskNumberText = matcher.group(1);
        if (taskNumberText == null || taskNumberText.isBlank()) {
            throw new YourHandException("Don't make me guess — give me a task number, e.g. delete 2.");
        }

        int taskNumber = parseNumber(
                taskNumberText,
                "Task numbers are whole numbers, not creative writing.");

        Task removedTask = taskList.removeTask(taskNumber);
        saveTasks(taskList, storage);
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
            return new Todo(
                    validateTaskText(
                            description,
                            "You handed me an empty to-do. Try: todo borrow book"));
        }

        Matcher deadlineMatcher = DEADLINE_PATTERN.matcher(command);
        if (deadlineMatcher.matches()) {
            return new Deadline(
                    validateTaskText(deadlineMatcher.group(1), "Your deadline needs a description."),
                    parseTaskDateTime(validateTaskText(deadlineMatcher.group(2),
                            "Your deadline needs a due date.")));
        }
        if (isCommand(command, "deadline")) {
            throw new YourHandException("Bro due when please. Try: deadline DESCRIPTION /by DATE_OR_TIME");
        }

        Matcher eventMatcher = EVENT_PATTERN.matcher(command);
        if (eventMatcher.matches()) {
            String description = validateTaskText(
                    eventMatcher.group(1), "Your event needs a description.");
            TaskDateTime from = parseTaskDateTime(
                    validateTaskText(
                            eventMatcher.group(2),
                            "Your event needs a start date."));
            TaskDateTime to = parseTaskDateTime(
                    validateTaskText(
                            eventMatcher.group(3),
                            "Your event needs an end date."));
            if (to.getValue().isBefore(from.getValue())) {
                throw new YourHandException("Your event cannot end before it starts.");
            }
            return new Event(description, from, to);
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

    /** Converts user-entered text to a whole number or reports a command-specific error. */
    private static int parseNumber(String numberText, String errorMessage) throws YourHandException {
        try {
            return Integer.parseInt(numberText.trim());
        } catch (NumberFormatException exception) {
            throw new YourHandException(errorMessage);
        }
    }

    /** Warns when a new task duplicates an existing task description. */
    private static void printDuplicateTaskWarning(Task task, TaskList taskList) {
        int existingTaskNumber = taskList.findTaskNumberByDescription(task.getDescription());
        if (existingTaskNumber != -1) {
            System.out.println(" Heads up: task " + existingTaskNumber + " already has that description."
                    + " I'll add this one too.");
        }
    }

    /** Validates and trims text that will be stored in the pipe-delimited data file. */
    private static String validateTaskText(String text, String emptyMessage) throws YourHandException {
        String trimmedText = text.trim();
        if (trimmedText.isEmpty()) {
            throw new YourHandException(emptyMessage);
        }
        if (trimmedText.contains("|")) {
            throw new YourHandException("Please don't use | in a task. I need it to save your data safely.");
        }
        return trimmedText;
    }

    /** Parses a supported task date or date-time and presents parse errors as chatbot errors. */
    private static TaskDateTime parseTaskDateTime(String dateText) throws YourHandException {
        try {
            return new TaskDateTime(LocalDate.parse(dateText, ISO_DATE_FORMAT));
        } catch (DateTimeParseException exception) {
            return parseTaskDateTimeWithTime(dateText);
        }
    }

    /** Parses one of the supported date-time formats. */
    private static TaskDateTime parseTaskDateTimeWithTime(String dateText) throws YourHandException {
        for (DateTimeFormatter formatter : List.of(
                ISO_COMPACT_DATE_TIME_FORMAT,
                ISO_COLON_DATE_TIME_FORMAT,
                SLASH_DATE_TIME_FORMAT)) {
            try {
                return new TaskDateTime(LocalDateTime.parse(dateText, formatter));
            } catch (DateTimeParseException exception) {
                // Try the next documented format.
            }
        }
        throw new YourHandException("Use yyyy-M-d, yyyy-M-d HHmm, yyyy-M-d HH:mm, or d/M/yyyy HHmm.");
    }

    /** Saves the current list and reports any file-writing problem to the user. */
    private static void saveTasks(TaskList taskList, Storage storage) throws YourHandException {
        try {
            storage.save(taskList);
        } catch (IOException | SecurityException exception) {
            throw new YourHandException("I couldn't save your tasks. Please check the data folder.");
        }
    }

    /** Loads saved tasks and starts with an empty list if the data file cannot be read. */
    private static TaskList loadTasks(Storage storage) {
        try {
            return storage.load();
        } catch (CorruptFileException exception) {
            System.out.println(" Your saved data file looks corrupted, so I didn't load it.");
            return new TaskList();
        } catch (IOException | SecurityException exception) {
            System.out.println(" I couldn't load your saved tasks. Starting with a clean slate.");
            return new TaskList();
        }
    }
}
