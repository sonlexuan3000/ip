import java.util.Scanner;

/**
 * Entry point and command loop for the Mira chatbot.
 */
public class Mira {
    private static final String LINE = "____________________________________________________________";

    /**
     * Echoes commands until the user enters {@code bye}.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        printBlock("Hello! I'm Mira\nWhat can I do for you?");

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            if (input.equals("bye")) {
                printBlock("Bye. Hope to see you again soon!");
                return;
            }
            printBlock(input);
        }
    }

    private static void printBlock(String message) {
        System.out.println(LINE);
        System.out.println(message);
        System.out.println(LINE);
    }
}
