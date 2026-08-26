import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
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

    private final List<Task> tasks;
    private final Scanner scanner;
    private final Storage storage;

    /**
     * Creates a Mira session that reads commands from standard input.
     *
     * @throws MiraException if saved tasks cannot be loaded
     */
    public Mira() throws MiraException {
        this.storage = new Storage(Path.of("data", "mira.txt"));
        this.tasks = new ArrayList<>(storage.load());
        this.scanner = new Scanner(System.in);
    }

    /**
     * Starts the chatbot and processes commands until the user enters {@code bye}
     * or the input stream ends.
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
     * @param input complete command entered by the user
     * @return {@code true} when the application should exit
     * @throws MiraException if the command is invalid
     */
    private boolean execute(String input) throws MiraException {
        if (input.isBlank()) {
            throw new MiraException("Please enter a command.");
        }

        String[] commandParts = input.split("\\s+", 2);
        CommandType commandType = CommandType.fromWord(commandParts[0]);
        String arguments = commandParts.length == 2 ? commandParts[1].trim() : "";

        switch (commandType) {
        case BYE:
            ensureNoArguments(arguments, "bye");
            printBlock("Bye. Hope to see you again soon!");
            return true;
        case LIST:
            ensureNoArguments(arguments, "list");
            showTasks();
            break;
        case TODO:
            addTodo(arguments);
            break;
        case DEADLINE:
            addDeadline(arguments);
            break;
        case EVENT:
            addEvent(arguments);
            break;
        case MARK:
            setTaskDone(arguments, true);
            break;
        case UNMARK:
            setTaskDone(arguments, false);
            break;
        case DELETE:
            deleteTask(arguments);
            break;
        case UNKNOWN:
            throw new MiraException("I'm sorry, but I don't know what that means :-(");
        default:
            throw new MiraException("That command is not supported.");
        }

        return false;
    }

    /**
     * Adds a todo task after validating that its description is present.
     */
    private void addTodo(String arguments) throws MiraException {
        if (arguments.isBlank()) {
            throw new MiraException("The description of a todo cannot be empty.");
        }
        addTask(new Todo(arguments));
    }

    /**
     * Parses and adds a deadline in the form {@code description /by yyyy-MM-dd}.
     */
    private void addDeadline(String arguments) throws MiraException {
        Matcher matcher = DEADLINE_PATTERN.matcher(arguments);
        if (!hasExactlyOneMatch(BY_DELIMITER_PATTERN, arguments) || !matcher.matches()) {
            throw new MiraException(
                    "A deadline must follow: deadline DESCRIPTION /by YYYY-MM-DD.");
        }

        LocalDate by;
        try {
            by = LocalDate.parse(matcher.group(2).trim());
        } catch (DateTimeParseException exception) {
            throw new MiraException("A deadline date must use YYYY-MM-DD.");
        }
        addTask(new Deadline(matcher.group(1).trim(), by));
    }

    /**
     * Parses and adds an event in the form
     * {@code description /from start /to end}.
     */
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

    /**
     * Adds a task and confirms the updated task count.
     */
    private void addTask(Task task) throws MiraException {
        tasks.add(task);
        storage.save(tasks);
        printBlock("Got it. I've added this task:\n  " + task
                + "\n" + getTaskCountMessage());
    }

    /**
     * Displays all current tasks in their user-facing order.
     */
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

    /**
     * Marks or unmarks the task identified by a one-based user index.
     */
    private void setTaskDone(String arguments, boolean isDone) throws MiraException {
        Task task = getTask(arguments);
        task.setDone(isDone);
        storage.save(tasks);

        String message = isDone
                ? "Nice! I've marked this task as done:"
                : "OK, I've marked this task as not done yet:";
        printBlock(message + "\n  " + task);
    }

    /**
     * Deletes the task identified by a one-based user index.
     */
    private void deleteTask(String arguments) throws MiraException {
        int index = parseTaskIndex(arguments);
        Task removedTask = tasks.remove(index);
        storage.save(tasks);
        printBlock("Noted. I've removed this task:\n  " + removedTask
                + "\n" + getTaskCountMessage());
    }

    /**
     * Returns the task identified by a one-based user index.
     */
    private Task getTask(String arguments) throws MiraException {
        return tasks.get(parseTaskIndex(arguments));
    }

    /**
     * Validates and converts a one-based user index to a zero-based list index.
     */
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

    /**
     * Rejects unexpected text after a command that accepts no arguments.
     */
    private void ensureNoArguments(String arguments, String command) throws MiraException {
        if (!arguments.isBlank()) {
            throw new MiraException("The " + command + " command does not take extra text.");
        }
    }

    /**
     * Describes the current task count using the correct singular or plural noun.
     */
    private String getTaskCountMessage() {
        String noun = tasks.size() == 1 ? "task" : "tasks";
        return "Now you have " + tasks.size() + " " + noun + " in the list.";
    }

    /**
     * Checks that a command contains one unambiguous occurrence of a delimiter.
     */
    private static boolean hasExactlyOneMatch(Pattern pattern, String input) {
        Matcher matcher = pattern.matcher(input);
        return matcher.find() && !matcher.find();
    }

    /**
     * Prints a response inside the chatbot's text boundary.
     */
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
        try {
            new Mira().run();
        } catch (MiraException exception) {
            printBlock("OOPS!!! " + exception.getMessage());
        }
    }
}
