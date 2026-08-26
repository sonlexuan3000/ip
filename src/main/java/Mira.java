import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Entry point and command loop for the Mira task-tracking chatbot.
 */
public class Mira {
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

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /**
     * Creates a Mira session connected to standard input and local storage.
     *
     * @throws MiraException if saved tasks cannot be loaded
     */
    public Mira() throws MiraException {
        this.storage = new Storage(Path.of("data", "mira.txt"));
        this.tasks = storage.load();
        this.ui = new Ui();
    }

    /**
     * Starts the chatbot and processes commands until the user enters {@code bye}
     * or the input stream ends.
     */
    public void run() {
        ui.showWelcome();

        while (ui.hasNextCommand()) {
            String input = ui.readCommand();

            try {
                if (execute(input)) {
                    return;
                }
            } catch (MiraException exception) {
                ui.showError(exception.getMessage());
            }
        }
    }

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
            ui.showGoodbye();
            return true;
        case LIST:
            ensureNoArguments(arguments, "list");
            ui.showTasks(tasks.asList());
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

    private void addTask(Task task) throws MiraException {
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }

    private void setTaskDone(String arguments, boolean isDone) throws MiraException {
        int taskNumber = parseTaskNumber(arguments);
        Task task = tasks.setDone(taskNumber, isDone);
        storage.save(tasks);
        ui.showTaskMarked(task, isDone);
    }

    private void deleteTask(String arguments) throws MiraException {
        int taskNumber = parseTaskNumber(arguments);
        Task removedTask = tasks.delete(taskNumber);
        storage.save(tasks);
        ui.showTaskDeleted(removedTask, tasks.size());
    }

    private int parseTaskNumber(String arguments) throws MiraException {
        if (!arguments.matches("\\d+")) {
            throw new MiraException("Please provide a valid task number.");
        }

        try {
            return Integer.parseInt(arguments);
        } catch (NumberFormatException exception) {
            throw new MiraException("That task number is too large.");
        }
    }

    private void ensureNoArguments(String arguments, String command) throws MiraException {
        if (!arguments.isBlank()) {
            throw new MiraException("The " + command + " command does not take extra text.");
        }
    }

    private static boolean hasExactlyOneMatch(Pattern pattern, String input) {
        Matcher matcher = pattern.matcher(input);
        return matcher.find() && !matcher.find();
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
            new Ui().showError(exception.getMessage());
        }
    }
}
