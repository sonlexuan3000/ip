import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Entry point and command loop for the Mira chatbot.
 */
public class Mira {
    private static final String LINE = "____________________________________________________________";

    /**
     * Stores tasks and supports listing and completion updates.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        List<Task> tasks = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        printBlock("Hello! I'm Mira\nWhat can I do for you?");

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            if (input.equals("bye")) {
                printBlock("Bye. Hope to see you again soon!");
                return;
            } else if (input.equals("list")) {
                showTasks(tasks);
            } else if (input.startsWith("mark ")) {
                setTaskDone(tasks, input.substring(5), true);
            } else if (input.startsWith("unmark ")) {
                setTaskDone(tasks, input.substring(7), false);
            } else {
                Task task = new Task(input);
                tasks.add(task);
                printBlock("Added: " + task);
            }
        }
    }

    private static void showTasks(List<Task> tasks) {
        StringBuilder message = new StringBuilder("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            message.append(System.lineSeparator())
                    .append(i + 1)
                    .append('.')
                    .append(tasks.get(i));
        }
        printBlock(message.toString());
    }

    private static void setTaskDone(List<Task> tasks, String indexText, boolean isDone) {
        int index = Integer.parseInt(indexText) - 1;
        Task task = tasks.get(index);
        task.setDone(isDone);
        String message = isDone
                ? "Nice! I've marked this task as done:"
                : "OK, I've marked this task as not done yet:";
        printBlock(message + "\n  " + task);
    }

    private static void printBlock(String message) {
        System.out.println(LINE);
        System.out.println(message);
        System.out.println(LINE);
    }
}
