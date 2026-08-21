/**
 * A task that occurs between stated start and end times.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an incomplete event.
     */
    public Event(String description, String from, String to) {
        super(description, "E");
        this.from = from;
        this.to = to;
    }

    @Override
    protected String getDetails() {
        return " (from: " + from + " to: " + to + ")";
    }
}
