# UI Test Plan

run-command: java -cp _temp/ui-test-classes YourHand
setup-command: javac -d _temp/ui-test-classes src/main/java/*.java
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
 Hmm, I don't speak that yet. Try todo, deadline, event, list, mark, unmark, or bye.
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
 I need a deadline date too. Try: deadline DESCRIPTION /by DATE_OR_TIME
____________________________________________________________
____________________________________________________________
 I have no tasks to work with yet. Add one before using its number.
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
