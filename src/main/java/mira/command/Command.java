package mira.command;

import mira.task.Task;

/**
 * Represents one validated instruction for Mira to execute.
 */
public class Command {
    private final CommandType type;
    private final Task task;
    private final int taskNumber;

    private Command(CommandType type, Task task, int taskNumber) {
        this.type = type;
        this.task = task;
        this.taskNumber = taskNumber;
    }

    /**
     * Creates a command that needs no additional value.
     *
     * @param type command type
     * @return a command without a task or index
     */
    public static Command withoutArguments(CommandType type) {
        return new Command(type, null, 0);
    }

    /**
     * Creates a command that carries a newly parsed task.
     *
     * @param type command type
     * @param task task to add
     * @return a command containing the task
     */
    public static Command withTask(CommandType type, Task task) {
        return new Command(type, task, 0);
    }

    /**
     * Creates a command that targets a displayed task number.
     *
     * @param type command type
     * @param taskNumber one-based task number
     * @return a command containing the task number
     */
    public static Command withTaskNumber(CommandType type, int taskNumber) {
        return new Command(type, null, taskNumber);
    }

    /**
     * Returns the command type.
     *
     * @return the command type
     */
    public CommandType getType() {
        return type;
    }

    /**
     * Returns the task carried by an add command.
     *
     * @return the parsed task, or {@code null} for other commands
     */
    public Task getTask() {
        return task;
    }

    /**
     * Returns the one-based number carried by a task-targeting command.
     *
     * @return the task number, or zero for other commands
     */
    public int getTaskNumber() {
        return taskNumber;
    }
}
