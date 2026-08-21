import java.util.Locale;

/**
 * Commands understood by Mira.
 */
public enum CommandType {
    /** Ends the chatbot session. */
    BYE,
    /** Displays all tasks. */
    LIST,
    /** Adds a task without scheduling information. */
    TODO,
    /** Adds a task with a due date or time. */
    DEADLINE,
    /** Adds a task with start and end times. */
    EVENT,
    /** Marks a task as completed. */
    MARK,
    /** Marks a task as not completed. */
    UNMARK,
    /** Removes a task. */
    DELETE,
    /** Represents an unrecognized command word. */
    UNKNOWN;

    /**
     * Maps the first word of user input to a command type.
     *
     * @param word first word of a user command
     * @return the corresponding command type, or {@link #UNKNOWN}
     */
    public static CommandType fromWord(String word) {
        try {
            return valueOf(word.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return UNKNOWN;
        }
    }
}
