package mira.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import mira.exception.MiraException;
import mira.task.Deadline;
import mira.task.Event;
import mira.task.Task;
import mira.task.TaskList;
import mira.task.Todo;

/**
 * Loads and saves Mira tasks in a local text file.
 */
public class Storage {
    private static final String FIELD_SEPARATOR = " | ";

    private final Path filePath;

    /**
     * Creates storage backed by the specified file.
     *
     * @param filePath relative or absolute path to the data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all tasks from disk, creating an empty data file if needed.
     *
     * @return tasks in their saved order.
     * @throws MiraException if the data cannot be read or decoded.
     */
    public TaskList load() throws MiraException {
        createDataFileIfMissing();

        try {
            List<Task> tasks = new ArrayList<>();
            for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                if (!line.isBlank()) {
                    tasks.add(decodeTask(line));
                }
            }
            return new TaskList(tasks);
        } catch (IOException exception) {
            throw new MiraException("I couldn't read the data file.");
        }
    }

    /**
     * Replaces the data file with the current task list.
     *
     * @param tasks task list to persist in display order.
     * @throws MiraException if the data cannot be written.
     */
    public void save(TaskList tasks) throws MiraException {
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            List<String> lines = new ArrayList<>();
            for (Task task : tasks.asList()) {
                lines.add(encodeTask(task));
            }
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new MiraException("I couldn't save your tasks.");
        }
    }

    private void createDataFileIfMissing() throws MiraException {
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException exception) {
            throw new MiraException("I couldn't create the data file.");
        }
    }

    private static String encodeTask(Task task) {
        List<String> fields = new ArrayList<>();
        fields.add(task.getType().getSymbol());
        fields.add(task.isDone() ? "1" : "0");
        fields.add(encodeText(task.getDescription()));

        switch (task.getType()) {
            case DEADLINE:
                fields.add(encodeText(((Deadline) task).getBy().toString()));
                break;
            case EVENT:
                Event event = (Event) task;
                fields.add(encodeText(event.getFrom()));
                fields.add(encodeText(event.getTo()));
                break;
            case TODO:
                break;
            default:
                throw new IllegalArgumentException("Unsupported task type: " + task.getType());
        }

        return String.join(FIELD_SEPARATOR, fields);
    }

    private static Task decodeTask(String line) throws MiraException {
        String[] fields = line.split(" \\| ", -1);
        if (fields.length < 3) {
            throw invalidData();
        }

        Task task;
        String description = decodeText(fields[2]);
        switch (fields[0]) {
            case "T":
                requireFieldCount(fields, 3);
                task = new Todo(description);
                break;
            case "D":
                requireFieldCount(fields, 4);
                task = new Deadline(description, decodeDate(fields[3]));
                break;
            case "E":
                requireFieldCount(fields, 5);
                task = new Event(description, decodeText(fields[3]), decodeText(fields[4]));
                break;
            default:
                throw invalidData();
        }

        if ("1".equals(fields[1])) {
            task.setDone(true);
        } else if (!"0".equals(fields[1])) {
            throw invalidData();
        }
        return task;
    }

    private static String encodeText(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    private static String decodeText(String encodedText) throws MiraException {
        try {
            byte[] bytes = Base64.getDecoder().decode(encodedText);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException exception) {
            throw invalidData();
        }
    }

    private static LocalDate decodeDate(String encodedDate) throws MiraException {
        try {
            return LocalDate.parse(decodeText(encodedDate));
        } catch (DateTimeParseException exception) {
            throw invalidData();
        }
    }

    private static void requireFieldCount(String[] fields, int expectedCount)
            throws MiraException {
        if (fields.length != expectedCount) {
            throw invalidData();
        }
    }

    private static MiraException invalidData() {
        return new MiraException("The data file contains an invalid task.");
    }
}
