import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Entry point and command loop for the Mira task-tracking chatbot.
 */
public class Mira {
    private static final String LINE = "____________________________________________________________";
    private final List<Task> tasks = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Starts the chatbot and processes commands until {@code bye}.
     */
    public void run() {
        printBlock("Hello! I'm Mira\nWhat can I do for you?");

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            if (input.equals("bye")) {
                printBlock("Bye. Hope to see you again soon!");
                return;
            } else if (input.equals("list")) {
                showTasks();
            } else if (input.startsWith("mark ")) {
                setTaskDone(input.substring(5), true);
            } else if (input.startsWith("unmark ")) {
                setTaskDone(input.substring(7), false);
            } else if (input.startsWith("todo ")) {
                addTask(new Todo(input.substring(5)));
            } else if (input.startsWith("deadline ")) {
                addDeadline(input.substring(9));
            } else if (input.startsWith("event ")) {
                addEvent(input.substring(6));
            }
        }
    }

    private void addDeadline(String arguments) {
        int byIndex = arguments.indexOf(" /by ");
        addTask(new Deadline(
                arguments.substring(0, byIndex),
                arguments.substring(byIndex + 5)));
    }

    private void addEvent(String arguments) {
        int fromIndex = arguments.indexOf(" /from ");
        int toIndex = arguments.indexOf(" /to ", fromIndex + 7);
        addTask(new Event(
                arguments.substring(0, fromIndex),
                arguments.substring(fromIndex + 7, toIndex),
                arguments.substring(toIndex + 5)));
    }

    private void addTask(Task task) {
        tasks.add(task);
        String noun = tasks.size() == 1 ? "task" : "tasks";
        printBlock("Got it. I've added this task:\n  " + task
                + "\nNow you have " + tasks.size() + " " + noun + " in the list.");
    }

    private void showTasks() {
        StringBuilder message = new StringBuilder("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            message.append(System.lineSeparator())
                    .append(i + 1)
                    .append('.')
                    .append(tasks.get(i));
        }
        printBlock(message.toString());
    }

    private void setTaskDone(String indexText, boolean isDone) {
        Task task = tasks.get(Integer.parseInt(indexText) - 1);
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

    /**
     * Launches Mira.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        new Mira().run();
    }
}
