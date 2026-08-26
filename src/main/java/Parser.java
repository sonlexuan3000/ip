import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts raw user input into validated commands and task objects.
 */
public class Parser {
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

    /**
     * Parses and validates one complete command line.
     *
     * @param input complete user input
     * @return the parsed command
     * @throws MiraException if the input does not form a valid command
     */
    public Command parse(String input) throws MiraException {
        if (input.isBlank()) {
            throw new MiraException("Please enter a command.");
        }

        String[] commandParts = input.split("\\s+", 2);
        CommandType type = CommandType.fromWord(commandParts[0]);
        String arguments = commandParts.length == 2 ? commandParts[1].trim() : "";

        switch (type) {
        case BYE:
            ensureNoArguments(arguments, "bye");
            return Command.withoutArguments(type);
        case LIST:
            ensureNoArguments(arguments, "list");
            return Command.withoutArguments(type);
        case TODO:
            return Command.withTask(type, parseTodo(arguments));
        case DEADLINE:
            return Command.withTask(type, parseDeadline(arguments));
        case EVENT:
            return Command.withTask(type, parseEvent(arguments));
        case MARK:
        case UNMARK:
        case DELETE:
            return Command.withTaskNumber(type, parseTaskNumber(arguments));
        case UNKNOWN:
            throw new MiraException("I'm sorry, but I don't know what that means :-(");
        default:
            throw new MiraException("That command is not supported.");
        }
    }

    private Task parseTodo(String arguments) throws MiraException {
        if (arguments.isBlank()) {
            throw new MiraException("The description of a todo cannot be empty.");
        }
        return new Todo(arguments);
    }

    private Task parseDeadline(String arguments) throws MiraException {
        Matcher matcher = DEADLINE_PATTERN.matcher(arguments);
        if (!hasExactlyOneMatch(BY_DELIMITER_PATTERN, arguments) || !matcher.matches()) {
            throw new MiraException(
                    "A deadline must follow: deadline DESCRIPTION /by YYYY-MM-DD.");
        }

        try {
            LocalDate by = LocalDate.parse(matcher.group(2).trim());
            return new Deadline(matcher.group(1).trim(), by);
        } catch (DateTimeParseException exception) {
            throw new MiraException("A deadline date must use YYYY-MM-DD.");
        }
    }

    private Task parseEvent(String arguments) throws MiraException {
        Matcher matcher = EVENT_PATTERN.matcher(arguments);
        if (!hasExactlyOneMatch(FROM_DELIMITER_PATTERN, arguments)
                || !hasExactlyOneMatch(TO_DELIMITER_PATTERN, arguments)
                || !matcher.matches()) {
            throw new MiraException(
                    "An event must follow: event DESCRIPTION /from START /to END.");
        }
        return new Event(
                matcher.group(1).trim(),
                matcher.group(2).trim(),
                matcher.group(3).trim());
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
}
