import java.util.Scanner;

/**
 * A command-line chatbot that echoes user commands until asked to exit.
 */
public class YourHand {
    private static final String SEPARATOR = "____________________________________________________________";

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
        String[] tasks = new String[100];
        int numberOfTasks = 0;

        while (true) {
            String command = scanner.nextLine();
            System.out.println(SEPARATOR);

            if (command.equals("bye")) {
                System.out.println(" See you never :)");
                System.out.println(SEPARATOR);
                break;
            }

            if (command.equals("list")) {
                for (int i = 0; i < numberOfTasks; i++) {
                    System.out.println(" " + (i + 1) + ". " + tasks[i]);
                }
            } else if (numberOfTasks < tasks.length) {
                tasks[numberOfTasks] = command;
                numberOfTasks++;
                System.out.println(" added: " + command);
            }

            System.out.println(SEPARATOR);
        }
    }
}
