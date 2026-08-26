/**
 * A task that should be completed by a stated date or time.
 */
public class Deadline extends Task {
    private final String by;

    /**
     * Creates an incomplete deadline.
     *
     * @param description human-readable task description
     * @param by due date or time as entered by the user
     */
    public Deadline(String description, String by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
    }

    /**
     * Returns the deadline text exactly as entered by the user.
     *
     * @return the due date or time text
     */
    public String getBy() {
        return by;
    }

    @Override
    protected String getDetails() {
        return " (by: " + by + ")";
    }
}
