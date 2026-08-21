import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Entry point and command loop for the Mira task-tracking chatbot.
 */
public class Mira {
    private static final String LINE = "____________________________________________________________";
    private static final Pattern DEADLINE_PATTERN = Pattern.compile(
            "^(.+?)\\s+/by\\s+(.+)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern EVENT_PATTERN = Pattern.compile(
            "^(.+?)\\s+/from\\s+(.+?)\\s+/to\\s+(.+)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern BY_DELIMITER_PATTERN = Pattern.compile(
            "\\s+/by\\s+", Pattern.CASE_INSENSITIVE);
    private static final Pattern FROM_DELIMITER_PATTERN = Pattern.compile(
            "\\s+/from\\s+", Pattern.CASE_INSENSITIVE);
    private static final Pattern TO_DELIMITER_PATTERN = Pattern.compile(
            "\\s+/to\\s+", Pattern.CASE_INSENSITIVE);

    private final List<Task> tasks = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Starts the chatbot and handles invalid input without ending the session.
     */
    public void run() {
        printBlock("Hello! I'm Mira\nWhat can I do for you?");

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            try {
                if (execute(input)) {
                    return;
                }
            } catch (MiraException exception) {
                printBlock("OOPS!!! " + exception.getMessage());
            }
        }
    }

    /**
     * Executes one user command.
     *
     * @return {@code true} when the application should exit
     */
    private boolean execute(String input) throws MiraException {
        if (input.isBlank()) {
            throw new MiraException("Please enter a command.");
        }

        String[] commandParts = input.split("\\s+", 2);
        String command = commandParts[0].toLowerCase(Locale.ROOT);
        String arguments = commandParts.length == 2 ? commandParts[1].trim() : "";

        switch (command) {
        case "bye":
            ensureNoArguments(arguments, "bye");
            printBlock("Bye. Hope to see you again soon!");
            return true;
        case "list":
            ensureNoArguments(arguments, "list");
            showTasks();
            break;
        case "todo":
            addTodo(arguments);
            break;
        case "deadline":
            addDeadline(arguments);
            break;
        case "event":
            addEvent(arguments);
            break;
        case "mark":
            setTaskDone(arguments, true);
            break;
        case "unmark":
            setTaskDone(arguments, false);
            break;
        case "delete":
            deleteTask(arguments);
            break;
        default:
            throw new MiraException("I'm sorry, but I don't know what that means :-(");
        }
        return false;
    }

    private void addTodo(String arguments) throws MiraException {
        if (arguments.isBlank()) {
            throw new MiraException("The description of a todo cannot be empty.");
        }
        addTask(new Todo(arguments));
    }

    private void addDeadline(String arguments) throws MiraException {
        Matcher matcher = DEADLINE_PATTERN.matcher(arguments);
        if (!hasExactlyOneMatch(BY_DELIMITER_PATTERN, arguments) || !matcher.matches()) {
            throw new MiraException(
                    "A deadline must follow: deadline DESCRIPTION /by DATE_OR_TIME.");
        }
        addTask(new Deadline(matcher.group(1).trim(), matcher.group(2).trim()));
    }

    private void addEvent(String arguments) throws MiraException {
        Matcher matcher = EVENT_PATTERN.matcher(arguments);
        if (!hasExactlyOneMatch(FROM_DELIMITER_PATTERN, arguments)
                || !hasExactlyOneMatch(TO_DELIMITER_PATTERN, arguments)
                || !matcher.matches()) {
            throw new MiraException(
                    "An event must follow: event DESCRIPTION /from START /to END.");
        }
        addTask(new Event(
                matcher.group(1).trim(),
                matcher.group(2).trim(),
                matcher.group(3).trim()));
    }

    private void addTask(Task task) {
        tasks.add(task);
        printBlock("Got it. I've added this task:\n  " + task
                + "\n" + getTaskCountMessage());
    }

    private void showTasks() {
        if (tasks.isEmpty()) {
            printBlock("Your task list is empty.");
            return;
        }

        StringBuilder message = new StringBuilder("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            message.append(System.lineSeparator())
                    .append(i + 1)
                    .append('.')
                    .append(tasks.get(i));
        }
        printBlock(message.toString());
    }

    private void setTaskDone(String arguments, boolean isDone) throws MiraException {
        Task task = tasks.get(parseTaskIndex(arguments));
        task.setDone(isDone);
        String message = isDone
                ? "Nice! I've marked this task as done:"
                : "OK, I've marked this task as not done yet:";
        printBlock(message + "\n  " + task);
    }

    private void deleteTask(String arguments) throws MiraException {
        Task removedTask = tasks.remove(parseTaskIndex(arguments));
        printBlock("Noted. I've removed this task:\n  " + removedTask
                + "\n" + getTaskCountMessage());
    }

    private int parseTaskIndex(String arguments) throws MiraException {
        if (!arguments.matches("\\d+")) {
            throw new MiraException("Please provide a valid task number.");
        }

        int userIndex;
        try {
            userIndex = Integer.parseInt(arguments);
        } catch (NumberFormatException exception) {
            throw new MiraException("That task number is too large.");
        }
        if (userIndex < 1 || userIndex > tasks.size()) {
            throw new MiraException("That task number is not in the list.");
        }
        return userIndex - 1;
    }

    private void ensureNoArguments(String arguments, String command) throws MiraException {
        if (!arguments.isBlank()) {
            throw new MiraException("The " + command + " command does not take extra text.");
        }
    }

    private String getTaskCountMessage() {
        String noun = tasks.size() == 1 ? "task" : "tasks";
        return "Now you have " + tasks.size() + " " + noun + " in the list.";
    }

    private static boolean hasExactlyOneMatch(Pattern pattern, String input) {
        Matcher matcher = pattern.matcher(input);
        return matcher.find() && !matcher.find();
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
