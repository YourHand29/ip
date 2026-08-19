import java.util.Scanner;

/**
 * A command-line chatbot that echoes user commands until asked to exit.
 */
public class YourHand {
    private static final String SEPARATOR = "____________________________________________________________";
    private static final int MAX_TASKS = 100;

    public static void main(String[] args) {
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

        Scanner scanner = new Scanner(System.in);
        Task[] tasks = new Task[MAX_TASKS];
        int numberOfTasks = 0;

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();
            System.out.println(SEPARATOR);

            if (command.equals("bye")) {
                System.out.println(" See you never :)");
                System.out.println(SEPARATOR);
                break;
            }

            if (command.equals("list")) {
                printTaskList(tasks, numberOfTasks);
            } else if (command.startsWith("mark ")) {
                updateTaskStatus(command, "mark ", tasks, numberOfTasks, true);
            } else if (command.startsWith("unmark ")) {
                updateTaskStatus(command, "unmark ", tasks, numberOfTasks, false);
            } else {
                Task task = createTask(command);
                if (task != null) {
                    numberOfTasks = addTask(task, tasks, numberOfTasks);
                }
            }

            System.out.println(SEPARATOR);
        }
    }

    /** Prints every task currently stored in the list. */
    private static void printTaskList(Task[] tasks, int numberOfTasks) {
        if (numberOfTasks == 0) {
            System.out.println(" Your task list is empty.");
            return;
        }

        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < numberOfTasks; i++) {
            System.out.println(" " + (i + 1) + "." + tasks[i]);
        }
    }

    /** Updates the completion status of the numbered task. */
    private static void updateTaskStatus(String command, String prefix, Task[] tasks,
                                         int numberOfTasks, boolean isDone) {
        String taskNumberText = command.substring(prefix.length()).trim();
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > numberOfTasks) {
                System.out.println(" Please provide a task number from 1 to " + numberOfTasks + ".");
                return;
            }

            Task task = tasks[taskNumber - 1];
            if (isDone) {
                task.markAsDone();
                System.out.println(" Nice! I've marked this task as done:");
            } else {
                task.markAsUndone();
                System.out.println(" OK, I've marked this task as not done yet:");
            }
            System.out.println("   " + task);
        } catch (NumberFormatException exception) {
            System.out.println(" Please provide a valid task number.");
        }
    }

    /** Creates a task from a valid task-creation command, or reports invalid input. */
    private static Task createTask(String command) {
        if (command.startsWith("todo ")) {
            return createToDo(command.substring(5).trim());
        }
        if (command.startsWith("deadline ")) {
            return createDeadline(command);
        }
        if (command.startsWith("event ")) {
            return createEvent(command);
        }

        System.out.println(" I don't understand that command.");
        return null;
    }

    /** Creates a to-do when it has a description. */
    private static Task createToDo(String description) {
        if (description.isEmpty()) {
            System.out.println(" A to-do needs a description.");
            return null;
        }
        return new ToDo(description);
    }

    /** Creates a deadline when its description and by-time are present. */
    private static Task createDeadline(String command) {
        int byIndex = command.indexOf(" /by ");
        if (byIndex == -1) {
            System.out.println(" Use: deadline DESCRIPTION /by DATE_OR_TIME");
            return null;
        }

        String description = command.substring(9, byIndex).trim();
        String by = command.substring(byIndex + 5).trim();
        if (description.isEmpty() || by.isEmpty()) {
            System.out.println(" A deadline needs both a description and a by-time.");
            return null;
        }
        return new Deadline(description, by);
    }

    /** Creates an event when its description, start, and end are present. */
    private static Task createEvent(String command) {
        int fromIndex = command.indexOf(" /from ");
        int toIndex = command.indexOf(" /to ");
        if (fromIndex == -1 || toIndex == -1 || toIndex <= fromIndex) {
            System.out.println(" Use: event DESCRIPTION /from START /to END");
            return null;
        }

        String description = command.substring(6, fromIndex).trim();
        String from = command.substring(fromIndex + 7, toIndex).trim();
        String to = command.substring(toIndex + 5).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            System.out.println(" An event needs a description, start time, and end time.");
            return null;
        }
        return new Event(description, from, to);
    }

    /** Adds a task when the fixed-size task list has capacity. */
    private static int addTask(Task task, Task[] tasks, int numberOfTasks) {
        if (numberOfTasks == MAX_TASKS) {
            System.out.println(" Your task list is full.");
            return numberOfTasks;
        }

        tasks[numberOfTasks] = task;
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        numberOfTasks++;
        String taskWord = numberOfTasks == 1 ? "task" : "tasks";
        System.out.println(" Now you have " + numberOfTasks + " " + taskWord + " in the list.");
        return numberOfTasks;
    }
}
