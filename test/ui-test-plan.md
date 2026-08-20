# UI Test Plan

run-command: java -cp _temp/ui-test-classes YourHand
setup-command: javac -d _temp/ui-test-classes src/main/java/YourHand.java src/main/java/tasks/*.java src/main/java/exceptions/*.java
working-directory: ..
timeout-seconds: 10

## Test case: Reject an empty to-do and unknown command

### Aim

Confirm that invalid input is handled through a clear user-visible error message and that the chatbot continues running.

### Input

```text
todo
blah
bye
```

### Expected output

```text
__   __                 _   _                 _
\ \ / /__  _   _ _ __  | | | | __ _ _ __   __| |
 \ V / _ \| | | | '__| | |_| |/ _` | '_ \ / _` |
  | | (_) | |_| | |    |  _  | (_| | | | | (_| |
  |_|\___/ \__,_|_|    |_| |_|\__,_|_| |_|\__,_|
____________________________________________________________
 Selamat Datang 早上好! YourHand 为你服务
 你来这干嘛 What are you here for?
____________________________________________________________
____________________________________________________________
 You handed me an empty to-do. Try: todo borrow book
____________________________________________________________
____________________________________________________________
 Hmm, I don't speak that yet. Try todo, deadline, event, list, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 See you never :)
____________________________________________________________
```

## Test case: Reject malformed deadlines and task references

### Aim

Confirm that malformed task commands and references to a missing task produce specific errors.

### Input

```text
deadline report
mark 1
bye
```

### Expected output

```text
__   __                 _   _                 _
\ \ / /__  _   _ _ __  | | | | __ _ _ __   __| |
 \ V / _ \| | | | '__| | |_| |/ _` | '_ \ / _` |
  | | (_) | |_| | |    |  _  | (_| | | | | (_| |
  |_|\___/ \__,_|_|    |_| |_|\__,_|_| |_|\__,_|
____________________________________________________________
 Selamat Datang 早上好! YourHand 为你服务
 你来这干嘛 What are you here for?
____________________________________________________________
____________________________________________________________
 Bro due when please. Try: deadline DESCRIPTION /by DATE_OR_TIME
____________________________________________________________
____________________________________________________________
 Brother I free how delete stuff. Add one before I can even delete.
____________________________________________________________
____________________________________________________________
 See you never :)
____________________________________________________________
```

## Test case: Mark and unmark a to-do

### Aim

Confirm that a valid task can be added, marked done, marked not done, and displayed with its final status.

### Input

```text
todo read book
mark 1
unmark 1
list
bye
```

### Expected output

```text
__   __                 _   _                 _
\ \ / /__  _   _ _ __  | | | | __ _ _ __   __| |
 \ V / _ \| | | | '__| | |_| |/ _` | '_ \ / _` |
  | | (_) | |_| | |    |  _  | (_| | | | | (_| |
  |_|\___/ \__,_|_|    |_| |_|\__,_|_| |_|\__,_|
____________________________________________________________
 Selamat Datang 早上好! YourHand 为你服务
 你来这干嘛 What are you here for?
____________________________________________________________
____________________________________________________________
 Fine, I've written this down:
   [T][ ] read book
 That's 1 task on your plate.
____________________________________________________________
____________________________________________________________
 Good job for surviving. I'll mark this as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Unmarked. Check pls:
   [T][ ] read book
____________________________________________________________
____________________________________________________________
 Here's your list of responsibilities:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
 See you never :)
____________________________________________________________
```

## Test case: Preserve a valid to-do after rejecting an empty one

### Aim

Confirm that a valid task remains in the list when a later invalid to-do command is rejected.

### Input

```text
todo buy milk
todo
list
bye
```

### Expected output

```text
__   __                 _   _                 _
\ \ / /__  _   _ _ __  | | | | __ _ _ __   __| |
 \ V / _ \| | | | '__| | |_| |/ _` | '_ \ / _` |
  | | (_) | |_| | |    |  _  | (_| | | | | (_| |
  |_|\___/ \__,_|_|    |_| |_|\__,_|_| |_|\__,_|
____________________________________________________________
 Selamat Datang 早上好! YourHand 为你服务
 你来这干嘛 What are you here for?
____________________________________________________________
____________________________________________________________
 Fine, I've written this down:
   [T][ ] buy milk
 That's 1 task on your plate.
____________________________________________________________
____________________________________________________________
 You handed me an empty to-do. Try: todo borrow book
____________________________________________________________
____________________________________________________________
 Here's your list of responsibilities:
 1.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
 See you never :)
____________________________________________________________
```

## Test case: Preserve a deadline after rejecting an incomplete event

### Aim

Confirm that a malformed event does not alter an already stored deadline.

### Input

```text
deadline submit report /by Sunday
event team meeting /from Monday
list
bye
```

### Expected output

```text
__   __                 _   _                 _
\ \ / /__  _   _ _ __  | | | | __ _ _ __   __| |
 \ V / _ \| | | | '__| | |_| |/ _` | '_ \ / _` |
  | | (_) | |_| | |    |  _  | (_| | | | | (_| |
  |_|\___/ \__,_|_|    |_| |_|\__,_|_| |_|\__,_|
____________________________________________________________
 Selamat Datang 早上好! YourHand 为你服务
 你来这干嘛 What are you here for?
____________________________________________________________
____________________________________________________________
 Fine, I've written this down:
   [D][ ] submit report (by: Sunday)
 That's 1 task on your plate.
____________________________________________________________
____________________________________________________________
 Walao when the even happening. Try: event DESCRIPTION /from START /to END
____________________________________________________________
____________________________________________________________
 Here's your list of responsibilities:
 1.[D][ ] submit report (by: Sunday)
____________________________________________________________
____________________________________________________________
 See you never :)
____________________________________________________________
```

## Test case: Preserve completion status after invalid task numbers

### Aim

Confirm that non-numeric and out-of-range task numbers do not change a task's completion status.

### Input

```text
todo read book
mark one
list
mark 2
list
bye
```

### Expected output

```text
__   __                 _   _                 _
\ \ / /__  _   _ _ __  | | | | __ _ _ __   __| |
 \ V / _ \| | | | '__| | |_| |/ _` | '_ \ / _` |
  | | (_) | |_| | |    |  _  | (_| | | | | (_| |
  |_|\___/ \__,_|_|    |_| |_|\__,_|_| |_|\__,_|
____________________________________________________________
 Selamat Datang 早上好! YourHand 为你服务
 你来这干嘛 What are you here for?
____________________________________________________________
____________________________________________________________
 Fine, I've written this down:
   [T][ ] read book
 That's 1 task on your plate.
____________________________________________________________
____________________________________________________________
 Task numbers are whole numbers, not creative writing.
____________________________________________________________
____________________________________________________________
 Here's your list of responsibilities:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
 Pick a task number from 1 to 1.
____________________________________________________________
____________________________________________________________
 Here's your list of responsibilities:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
 See you never :)
____________________________________________________________
```

## Test case: Delete an event and keep the remaining task order

### Aim

Confirm that deleting a task removes only that task, preserves the remaining tasks and their completion status, and closes the numbering gap.

### Input

```text
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
mark 1
mark 2
delete 3
list
bye
```

### Expected output

```text
__   __                 _   _                 _
\ \ / /__  _   _ _ __  | | | | __ _ _ __   __| |
 \ V / _ \| | | | '__| | |_| |/ _` | '_ \ / _` |
  | | (_) | |_| | |    |  _  | (_| | | | | (_| |
  |_|\___/ \__,_|_|    |_| |_|\__,_|_| |_|\__,_|
____________________________________________________________
 Selamat Datang 早上好! YourHand 为你服务
 你来这干嘛 What are you here for?
____________________________________________________________
____________________________________________________________
 Fine, I've written this down:
   [T][ ] read book
 That's 1 task on your plate.
____________________________________________________________
____________________________________________________________
 Fine, I've written this down:
   [D][ ] return book (by: June 6th)
 That's 2 tasks on your plate.
____________________________________________________________
____________________________________________________________
 Fine, I've written this down:
   [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
 That's 3 tasks on your plate.
____________________________________________________________
____________________________________________________________
 Good job for surviving. I'll mark this as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Good job for surviving. I'll mark this as done:
   [D][X] return book (by: June 6th)
____________________________________________________________
____________________________________________________________
 Poof. I've removed this task:
   [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
 That's 2 tasks left on your plate.
____________________________________________________________
____________________________________________________________
 Here's your list of responsibilities:
 1.[T][X] read book
 2.[D][X] return book (by: June 6th)
____________________________________________________________
____________________________________________________________
 See you never :)
____________________________________________________________
```

## Test case: Preserve tasks after invalid delete commands

### Aim

Confirm that out-of-range and non-numeric delete commands leave the task list unchanged, and that deleting the valid task then leaves an empty list.

### Input

```text
todo buy bread
delete 2
list
delete one
list
delete 1
list
bye
```

### Expected output

```text
__   __                 _   _                 _
\ \ / /__  _   _ _ __  | | | | __ _ _ __   __| |
 \ V / _ \| | | | '__| | |_| |/ _` | '_ \ / _` |
  | | (_) | |_| | |    |  _  | (_| | | | | (_| |
  |_|\___/ \__,_|_|    |_| |_|\__,_|_| |_|\__,_|
____________________________________________________________
 Selamat Datang 早上好! YourHand 为你服务
 你来这干嘛 What are you here for?
____________________________________________________________
____________________________________________________________
 Fine, I've written this down:
   [T][ ] buy bread
 That's 1 task on your plate.
____________________________________________________________
____________________________________________________________
 Pick a task number from 1 to 1.
____________________________________________________________
____________________________________________________________
 Here's your list of responsibilities:
 1.[T][ ] buy bread
____________________________________________________________
____________________________________________________________
 Task numbers are whole numbers, not creative writing.
____________________________________________________________
____________________________________________________________
 Here's your list of responsibilities:
 1.[T][ ] buy bread
____________________________________________________________
____________________________________________________________
 Poof. I've removed this task:
   [T][ ] buy bread
 That's 0 tasks left on your plate.
____________________________________________________________
____________________________________________________________
 Your task list is empty.
____________________________________________________________
____________________________________________________________
 See you never :)
____________________________________________________________
```

## Test case: Keep task status correct after repeated marking and unmarking

### Aim

Confirm that repeated mark and unmark commands do not corrupt task status, and that the final list shows the status from the last valid change.

### Input

```text
todo write essay
mark 1
mark 1
unmark 1
unmark 1
mark 1
list
bye
```

### Expected output

```text
__   __                 _   _                 _
\ \ / /__  _   _ _ __  | | | | __ _ _ __   __| |
 \ V / _ \| | | | '__| | |_| |/ _` | '_ \ / _` |
  | | (_) | |_| | |    |  _  | (_| | | | | (_| |
  |_|\___/ \__,_|_|    |_| |_|\__,_|_| |_|\__,_|
____________________________________________________________
 Selamat Datang 早上好! YourHand 为你服务
 你来这干嘛 What are you here for?
____________________________________________________________
____________________________________________________________
 Fine, I've written this down:
   [T][ ] write essay
 That's 1 task on your plate.
____________________________________________________________
____________________________________________________________
 Good job for surviving. I'll mark this as done:
   [T][X] write essay
____________________________________________________________
____________________________________________________________
 That task was already done. Double-checking never hurts:
   [T][X] write essay
____________________________________________________________
____________________________________________________________
 Unmarked. Check pls:
   [T][ ] write essay
____________________________________________________________
____________________________________________________________
 That task was already waiting for you. No change:
   [T][ ] write essay
____________________________________________________________
____________________________________________________________
 Good job for surviving. I'll mark this as done:
   [T][X] write essay
____________________________________________________________
____________________________________________________________
 Here's your list of responsibilities:
 1.[T][X] write essay
____________________________________________________________
____________________________________________________________
 See you never :)
____________________________________________________________
```
