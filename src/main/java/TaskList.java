import java.util.ArrayList;
import java.util.List;

/**
 * Owns Mira's ordered collection of tasks and its list operations.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this(List.of());
    }

    /**
     * Creates a task list containing the supplied tasks in order.
     *
     * @param tasks initial tasks
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Returns a task using the one-based number shown to the user.
     *
     * @param taskNumber one-based display number
     * @return the requested task
     * @throws MiraException if the number is outside the list
     */
    public Task get(int taskNumber) throws MiraException {
        return tasks.get(toZeroBasedIndex(taskNumber));
    }

    /**
     * Updates and returns a task using its one-based display number.
     *
     * @param taskNumber one-based display number
     * @param isDone new completion status
     * @return the updated task
     * @throws MiraException if the number is outside the list
     */
    public Task setDone(int taskNumber, boolean isDone) throws MiraException {
        Task task = get(taskNumber);
        task.setDone(isDone);
        return task;
    }

    /**
     * Deletes and returns a task using its one-based display number.
     *
     * @param taskNumber one-based display number
     * @return the deleted task
     * @throws MiraException if the number is outside the list
     */
    public Task delete(int taskNumber) throws MiraException {
        return tasks.remove(toZeroBasedIndex(taskNumber));
    }

    /**
     * Returns the number of stored tasks.
     *
     * @return task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns an immutable snapshot of the current tasks.
     *
     * @return tasks in display order
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    private int toZeroBasedIndex(int taskNumber) throws MiraException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new MiraException("That task number is not in the list.");
        }
        return taskNumber - 1;
    }
}
