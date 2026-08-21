/**
 * A task that should be completed by a stated date or time.
 */
public class Deadline extends Task {
    private final String by;

    /**
     * Creates an incomplete deadline.
     */
    public Deadline(String description, String by) {
        super(description, "D");
        this.by = by;
    }

    @Override
    protected String getDetails() {
        return " (by: " + by + ")";
    }
}
