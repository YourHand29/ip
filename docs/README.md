# YourHand User Guide

YourHand is a friendly command-line task manager. It saves your to-dos, deadlines, and events between program sessions.

## Quick start

1. Start the application as described in the project [README](../README.md).
2. Enter a command and press Enter.
3. Type `bye` when you are finished.

Task numbers shown by `list` start at 1. Dates and times are stored exactly as the text you provide.

## Commands

### Add a to-do

Use `todo DESCRIPTION` for a task without a date or time.

```text
todo borrow book
```

### Add a deadline

Use `deadline DESCRIPTION /by DATE_OR_TIME` for a task due by a date or time.

```text
deadline submit report /by Sunday 5pm
```

### Add an event

Use `event DESCRIPTION /from START /to END` for a task with a start and end time.

```text
event project meeting /from Monday 2pm /to 4pm
```

### List tasks

Use `list` to display every task. Type icons identify the task kind: `[T]` for a to-do, `[D]` for a deadline, and `[E]` for an event. `[X]` means completed; `[ ]` means not completed.

```text
list
```

### Mark a task done

Use `mark TASK_NUMBER` to complete a task.

```text
mark 2
```

### Mark a task not done

Use `unmark TASK_NUMBER` to reverse a completed task.

```text
unmark 2
```

### Delete a task

Use `delete TASK_NUMBER` to remove a task permanently from the current session.

```text
delete 3
```

After deletion, remaining tasks are renumbered by `list`.

### Inspect corrupted saved entries

Use `corrupt` to see saved entries that YourHand could not load and the reason each entry is invalid.

```text
corrupt
```

### Repair a corrupted saved entry

Use `editcorrupt ENTRY_NUMBER CORRECTED_FILE_LINE` to replace one displayed corrupt entry. The corrected line must use the data-file format.

```text
editcorrupt 1 D | 0 | return book | Sunday
```

The repaired task is added back to your task list and saved immediately.

### Exit

Use `bye` to close YourHand.

```text
bye
```

## Errors

YourHand keeps running after an invalid command. It reports a message explaining what is missing or invalid, and the existing task list is unchanged. For example, `todo` needs a description, and `mark one` needs a whole-number task number.

The `|` character is reserved for the saved-data format and cannot be used in task text. If the saved file contains malformed task entries, YourHand skips those entries and reports how many were ignored while still loading valid tasks.

When adding a task with the same description as an existing task, YourHand shows a reminder but still adds it.

## Limitations

- Tasks are saved automatically in `data/yourhand.txt` and restored when YourHand starts.
- Dates and times are treated as text; YourHand does not validate or sort them.
