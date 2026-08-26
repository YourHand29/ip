package storage;

import tasks.Deadline;
import tasks.Event;
import tasks.Task;
import tasks.TaskDateTime;
import tasks.TaskList;
import tasks.Todo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves and loads YourHand task data from a file.
 */
public class Storage {
    private static final Path FILE_PATH = Path.of("data", "yourhand.txt");
    private final List<CorruptedTask> corruptedTasks = new ArrayList<>();

    /**
     * Saves every task in the list, replacing the previous saved data.
     *
     * @param taskList Tasks to save.
     * @throws IOException If the data directory or file cannot be written.
     */
    public void save(TaskList taskList) throws IOException {
        Files.createDirectories(FILE_PATH.getParent());
        List<String> taskLines = new ArrayList<>(taskList.getTasks().stream()
                .map(Task::toFileString)
                .toList());
        for (CorruptedTask corruptedTask : corruptedTasks) {
            taskLines.add(corruptedTask.taskLine());
        }
        Files.write(FILE_PATH, taskLines, StandardCharsets.UTF_8);
    }

    /**
     * Loads saved tasks, or returns an empty list when no data file exists.
     *
     * @return Tasks reconstructed from the data file and the number of skipped lines.
     * @throws IOException If the data file cannot be read.
     */
    public LoadResult load() throws IOException {
        TaskList taskList = new TaskList();
        corruptedTasks.clear();
        if (!Files.exists(FILE_PATH)) {
            return new LoadResult(taskList, List.of());
        }

        for (String taskLine : Files.readAllLines(FILE_PATH, StandardCharsets.UTF_8)) {
            if (taskLine.isBlank()) {
                continue;
            }
            try {
                taskList.add(parseTask(taskLine));
            } catch (IllegalArgumentException exception) {
                corruptedTasks.add(new CorruptedTask(taskLine, exception.getMessage()));
            }
        }
        return new LoadResult(taskList, List.copyOf(corruptedTasks));
    }

    /**
     * Returns the saved entries that could not be loaded.
     *
     * @return Corrupt task entries in their displayed order.
     */
    public List<CorruptedTask> getCorruptedTasks() {
        return List.copyOf(corruptedTasks);
    }

    /**
     * Validates a corrected corrupt entry and removes the original entry when valid.
     *
     * @param entryNumber One-based corrupt entry number.
     * @param correctedLine Replacement data-file line.
     * @return The task represented by the corrected line.
     */
    public Task repairCorruptedTask(int entryNumber, String correctedLine) {
        if (corruptedTasks.isEmpty()) {
            throw new IllegalArgumentException("There are no corrupt entries to repair.");
        }
        if (entryNumber < 1 || entryNumber > corruptedTasks.size()) {
            throw new IllegalArgumentException("Pick a corrupt entry from 1 to " + corruptedTasks.size() + ".");
        }
        Task repairedTask = parseTask(correctedLine);
        corruptedTasks.remove(entryNumber - 1);
        return repairedTask;
    }

    /** Reconstructs one task from a well-formed data-file line. */
    private Task parseTask(String taskLine) {
        String[] fields = taskLine.split("\\|", -1);
        for (int index = 0; index < fields.length; index++) {
            fields[index] = fields[index].strip();
        }
        validateFields(fields);
        Task task = switch (fields[0]) {
        case "T" -> new Todo(fields[2]);
        case "D" -> new Deadline(fields[2], parseTaskDateTime(fields[3]));
        case "E" -> parseEvent(fields);
        default -> throw new IllegalArgumentException("Unknown task type in saved data.");
        };
        if (fields[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /** Parses an ISO task date or date-time from the data file. */
    private TaskDateTime parseTaskDateTime(String dateText) {
        try {
            return new TaskDateTime(LocalDate.parse(dateText));
        } catch (DateTimeParseException exception) {
            try {
                return new TaskDateTime(LocalDateTime.parse(dateText));
            } catch (DateTimeParseException dateTimeException) {
                throw new IllegalArgumentException("Saved task date must use ISO date or date-time format.");
            }
        }
    }

    /** Reconstructs an event while ensuring its end date does not precede its start date. */
    private Event parseEvent(String[] fields) {
        TaskDateTime from = parseTaskDateTime(fields[3]);
        TaskDateTime to = parseTaskDateTime(fields[4]);
        if (to.getValue().isBefore(from.getValue())) {
            throw new IllegalArgumentException("Saved event cannot end before it starts.");
        }
        return new Event(fields[2], from, to);
    }

    /** Validates a split data-file line before its fields are accessed. */
    private void validateFields(String[] fields) {
        if (fields.length < 3) {
            throw new IllegalArgumentException("A saved task needs a type, status, and description.");
        }
        if (!fields[1].equals("0") && !fields[1].equals("1")) {
            throw new IllegalArgumentException("Saved task status must be 0 or 1.");
        }
        if (fields[2].isBlank()) {
            throw new IllegalArgumentException("Saved task description cannot be empty.");
        }
        int expectedFieldCount = switch (fields[0]) {
        case "T" -> 3;
        case "D" -> 4;
        case "E" -> 5;
        default -> throw new IllegalArgumentException("Unknown task type in saved data.");
        };
        if (fields.length != expectedFieldCount) {
            throw new IllegalArgumentException("This task type has the wrong number of details.");
        }
        for (int index = 3; index < fields.length; index++) {
            if (fields[index].isBlank()) {
                throw new IllegalArgumentException("A saved task detail cannot be empty.");
            }
        }
    }

    /** Result of loading saved task data. */
    public record LoadResult(TaskList taskList, List<CorruptedTask> corruptedTasks) {
        /** Returns the number of corrupt data-file entries that were skipped. */
        public int skippedTaskCount() {
            return corruptedTasks.size();
        }
    }

    /** A saved task entry that could not be loaded and the reason it failed. */
    public record CorruptedTask(String taskLine, String reason) {
    }
}
