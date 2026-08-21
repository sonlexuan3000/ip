/**
 * A task without an attached date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo.
     */
    public Todo(String description) {
        super(description, "T");
    }
}
