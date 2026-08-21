/**
 * Entry point for the Mira chatbot.
 */
public class Mira {
    private static final String LINE = "____________________________________________________________";

    /**
     * Greets the user and exits.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        printBlock("Hello! I'm Mira\nWhat can I do for you?");
        printBlock("Bye. Hope to see you again soon!");
    }

    private static void printBlock(String message) {
        System.out.println(LINE);
        System.out.println(message);
        System.out.println(LINE);
    }
}
