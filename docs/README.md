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

Use `deadline DESCRIPTION /by DATE_OR_TIME` for a task due on a date or at a time.

Accepted formats are `yyyy-M-d`, `yyyy-M-d HHmm`, `yyyy-M-d HH:mm`, and `d/M/yyyy HHmm`. Zero-padding the month or day is optional.

```text
deadline submit report /by 2/12/2019 1800
```

### Add an event

Use `event DESCRIPTION /from DATE_OR_TIME /to DATE_OR_TIME` for a task with a start and end date or time.

```text
event project meeting /from 2026-08-26 18:00 /to 2026-08-26 2000
```

### List tasks

Use `list` to display every task. Type icons identify the task kind: `[T]` for a to-do, `[D]` for a deadline, and `[E]` for an event. `[X]` means completed; `[ ]` means not completed.

```text
list
```

### Find tasks

Use `find KEYWORD` to display tasks whose descriptions contain the keyword. Matching is case-insensitive, and the keyword is treated as ordinary text.

```text
find book
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

### Exit

Use `bye` to close YourHand.

```text
bye
```

## Errors

YourHand keeps running after an invalid command. It reports a message explaining what is missing or invalid, and the existing task list is unchanged. For example, `todo` needs a description, and `mark one` needs a whole-number task number.

The `|` character is reserved for the saved-data format and cannot be used in task text. Dates and times must be real values in one of the documented formats. If the saved file contains any malformed task entry, YourHand does not load that file and starts with an empty list instead.

When adding a task with the same description as an existing task, YourHand shows a reminder but still adds it.

## Limitations

- Tasks are saved automatically in `data/yourhand.txt` and restored when YourHand starts.
- Deadline and event dates and times are validated and displayed in a friendly format.
