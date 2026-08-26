package mira;

import java.nio.file.Path;

import mira.command.Command;
import mira.command.CommandType;
import mira.exception.MiraException;
import mira.parser.Parser;
import mira.storage.Storage;
import mira.task.Task;
import mira.task.TaskList;
import mira.ui.Ui;

/**
 * Entry point and coordinator for the Mira task-tracking chatbot.
 */
public class Mira {
    private final Parser parser;
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /**
     * Creates a Mira session connected to standard input and local storage.
     *
     * @throws MiraException if saved tasks cannot be loaded
     */
    public Mira() throws MiraException {
        this.storage = new Storage(Path.of("data", "mira.txt"));
        this.tasks = storage.load();
        this.parser = new Parser();
        this.ui = new Ui();
    }

    /**
     * Processes commands until the user exits or the input stream ends.
     */
    public void run() {
        ui.showWelcome();

        while (ui.hasNextCommand()) {
            try {
                Command command = parser.parse(ui.readCommand());
                if (execute(command)) {
                    return;
                }
            } catch (MiraException exception) {
                ui.showError(exception.getMessage());
            }
        }
    }

    private boolean execute(Command command) throws MiraException {
        switch (command.getType()) {
        case BYE:
            ui.showGoodbye();
            return true;
        case LIST:
            ui.showTasks(tasks.asList());
            break;
        case FIND:
            ui.showMatchingTasks(tasks.find(command.getKeyword()));
            break;
        case TODO:
        case DEADLINE:
        case EVENT:
            addTask(command.getTask());
            break;
        case MARK:
            setTaskDone(command.getTaskNumber(), true);
            break;
        case UNMARK:
            setTaskDone(command.getTaskNumber(), false);
            break;
        case DELETE:
            deleteTask(command.getTaskNumber());
            break;
        case UNKNOWN:
        default:
            throw new MiraException("That command is not supported.");
        }

        return false;
    }

    private void addTask(Task task) throws MiraException {
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }

    private void setTaskDone(int taskNumber, boolean isDone) throws MiraException {
        Task task = tasks.setDone(taskNumber, isDone);
        storage.save(tasks);
        ui.showTaskMarked(task, isDone);
    }

    private void deleteTask(int taskNumber) throws MiraException {
        Task removedTask = tasks.delete(taskNumber);
        storage.save(tasks);
        ui.showTaskDeleted(removedTask, tasks.size());
    }

    /**
     * Launches Mira.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        try {
            new Mira().run();
        } catch (MiraException exception) {
            new Ui().showError(exception.getMessage());
        }
    }
}
