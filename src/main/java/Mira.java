import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Entry point and command loop for the Mira chatbot.
 */
public class Mira {
    private static final String LINE = "____________________________________________________________";

    /**
     * Stores text as tasks and lists it on request.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        List<String> tasks = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        printBlock("Hello! I'm Mira\nWhat can I do for you?");

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            if (input.equals("bye")) {
                printBlock("Bye. Hope to see you again soon!");
                return;
            } else if (input.equals("list")) {
                showTasks(tasks);
            } else {
                tasks.add(input);
                printBlock("Added: " + input);
            }
        }
    }

    private static void showTasks(List<String> tasks) {
        StringBuilder message = new StringBuilder("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            message.append(System.lineSeparator())
                    .append(i + 1)
                    .append(". ")
                    .append(tasks.get(i));
        }
        printBlock(message.toString());
    }

    private static void printBlock(String message) {
        System.out.println(LINE);
        System.out.println(message);
        System.out.println(LINE);
    }
}
