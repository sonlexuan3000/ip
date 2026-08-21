/**
 * Base representation of a task with a description and completion status.
 */
public class Task {
    private final String description;
    private final String typeSymbol;
    private boolean isDone;

    /**
     * Creates an incomplete task of the stated type.
     */
    public Task(String description, String typeSymbol) {
        this.description = description;
        this.typeSymbol = typeSymbol;
        this.isDone = false;
    }

    /**
     * Updates whether this task has been completed.
     */
    public void setDone(boolean isDone) {
        this.isDone = isDone;
    }

    /**
     * Returns scheduling details supplied by a specialized task.
     */
    protected String getDetails() {
        return "";
    }

    @Override
    public String toString() {
        return "[" + typeSymbol + "][" + (isDone ? "X" : " ") + "] "
                + description + getDetails();
    }
}
