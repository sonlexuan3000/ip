package mira.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import mira.task.Task;

/**
 * Handles all text input and output for Mira.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";

    private final PrintStream output;
    private final Scanner scanner;

    /**
     * Creates a UI connected to standard input and output.
     */
    public Ui() {
        this(System.in, System.out);
    }

    /**
     * Creates a UI connected to the supplied streams.
     *
     * @param input stream from which commands are read
     * @param output stream to which responses are written
     */
    public Ui(InputStream input, PrintStream output) {
        this.scanner = new Scanner(input);
        this.output = output;
    }

    /**
     * Returns whether another command is available.
     *
     * @return {@code true} if another input line can be read
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next command line.
     *
     * @return the next user command
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays Mira's greeting.
     */
    public void showWelcome() {
        showBlock("Hello! I'm Mira\nWhat can I do for you?");
    }

    /**
     * Displays Mira's farewell.
     */
    public void showGoodbye() {
        showBlock("Bye. Hope to see you again soon!");
    }

    /**
     * Displays a recoverable command or storage error.
     *
     * @param message user-facing explanation of the problem
     */
    public void showError(String message) {
        showBlock("OOPS!!! " + message);
    }

    /**
     * Displays confirmation after a task is added.
     *
     * @param task task that was added
     * @param taskCount resulting number of tasks
     */
    public void showTaskAdded(Task task, int taskCount) {
        showBlock("Got it. I've added this task:\n  " + task
                + "\n" + getTaskCountMessage(taskCount));
    }

    /**
     * Displays every task in its current order.
     *
     * @param tasks tasks to display
     */
    public void showTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            showBlock("Your task list is empty.");
            return;
        }

        showNumberedTasks("Here are the tasks in your list:", tasks);
    }

    /**
     * Displays tasks whose descriptions matched a find command.
     *
     * @param tasks matching tasks to display
     */
    public void showMatchingTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            showBlock("No matching tasks found.");
            return;
        }

        showNumberedTasks("Here are the matching tasks in your list:", tasks);
    }

    private void showNumberedTasks(String header, List<Task> tasks) {
        StringBuilder message = new StringBuilder(header);
        for (int i = 0; i < tasks.size(); i++) {
            message.append(System.lineSeparator())
                    .append(i + 1)
                    .append('.')
                    .append(tasks.get(i));
        }
        showBlock(message.toString());
    }

    /**
     * Displays confirmation after a task's completion status changes.
     *
     * @param task task whose status changed
     * @param isDone new completion status
     */
    public void showTaskMarked(Task task, boolean isDone) {
        String message = isDone
                ? "Nice! I've marked this task as done:"
                : "OK, I've marked this task as not done yet:";
        showBlock(message + "\n  " + task);
    }

    /**
     * Displays confirmation after a task is deleted.
     *
     * @param task task that was deleted
     * @param taskCount resulting number of tasks
     */
    public void showTaskDeleted(Task task, int taskCount) {
        showBlock("Noted. I've removed this task:\n  " + task
                + "\n" + getTaskCountMessage(taskCount));
    }

    private String getTaskCountMessage(int taskCount) {
        String noun = taskCount == 1 ? "task" : "tasks";
        return "Now you have " + taskCount + " " + noun + " in the list.";
    }

    private void showBlock(String message) {
        output.println(LINE);
        output.println(message);
        output.println(LINE);
    }
}
