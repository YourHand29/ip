import exceptions.CorruptFileException;
import exceptions.YourHandException;
import tasks.Deadline;
import tasks.Event;
import tasks.Task;
import tasks.TaskDateTime;
import tasks.TaskList;
import tasks.Todo;
import storage.Storage;
import ui.Ui;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A command-line chatbot that manages to-dos, deadlines, and events.
 */
public class YourHand {
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
        Ui ui = new Ui();
        ui.showWelcomeMessage();
        Storage storage = new Storage();
        TaskList taskList = loadTasks(storage, ui);
        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showSeparator();
            try {
                if (handleCommand(command, taskList, storage, ui)) {
                    ui.showSeparator();
                    break;
                }
            } catch (YourHandException exception) {
                ui.showError(exception.getMessage());
            }
            ui.showSeparator();
        }
    }

    /**
     * Handles one command and returns whether the application should exit.
     *
     * @param command user-entered command
     * @param taskList task storage for this session
     * @return true when the command is {@code bye}
     * @throws YourHandException if the command is invalid
     */
    private static boolean handleCommand(String command, TaskList taskList, Storage storage, Ui ui)
            throws YourHandException {
        if (command.equals("bye")) {
            ui.showGoodbyeMessage();
            return true;
        }
        if (command.equals("list")) {
            ui.showTaskList(taskList);
            return false;
        }
        Matcher statusMatcher = STATUS_PATTERN.matcher(command);
        if (statusMatcher.matches()) {
            updateTaskStatus(statusMatcher, taskList, storage, ui);
            return false;
        }

        Matcher deleteMatcher = DELETE_PATTERN.matcher(command);
        if (deleteMatcher.matches()) {
            deleteTask(deleteMatcher, taskList, storage, ui);
            return false;
        }

        Task task = createTask(command);
        printDuplicateTaskWarning(task, taskList, ui);
        taskList.add(task);
        saveTasks(taskList, storage);
        ui.showTaskAdded(task, taskList.size());
        return false;
    }

    /** Updates a task's completion status from a validated mark or unmark command. */
    private static void updateTaskStatus(Matcher matcher, TaskList taskList, Storage storage, Ui ui)
            throws YourHandException {
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
        boolean isMarkCommand = matcher.group(1).equals("mark");
        if (isMarkCommand) {
            wasUpdated = task.markAsDone();
        } else {
            wasUpdated = task.markAsUndone();
        }
        if (wasUpdated) {
            saveTasks(taskList, storage);
        }
        ui.showTaskStatus(task, isMarkCommand, wasUpdated);
    }

    /** Removes the task specified by a validated delete command. */
    private static void deleteTask(Matcher matcher, TaskList taskList, Storage storage, Ui ui)
            throws YourHandException {
        String taskNumberText = matcher.group(1);
        if (taskNumberText == null || taskNumberText.isBlank()) {
            throw new YourHandException("Don't make me guess — give me a task number, e.g. delete 2.");
        }

        int taskNumber = parseNumber(
                taskNumberText,
                "Task numbers are whole numbers, not creative writing.");

        Task removedTask = taskList.removeTask(taskNumber);
        saveTasks(taskList, storage);
        ui.showTaskDeleted(removedTask, taskList.size());
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
    private static void printDuplicateTaskWarning(Task task, TaskList taskList, Ui ui) {
        int existingTaskNumber = taskList.findTaskNumberByDescription(task.getDescription());
        if (existingTaskNumber != -1) {
            ui.showDuplicateTaskWarning(existingTaskNumber);
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
    private static TaskList loadTasks(Storage storage, Ui ui) {
        try {
            return storage.load();
        } catch (CorruptFileException exception) {
            ui.showCorruptFileWarning();
            return new TaskList();
        } catch (IOException | SecurityException exception) {
            ui.showLoadingError();
            return new TaskList();
        }
    }
}
