import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private static final String HEADER = "id,type,name,status,description,duration,startTime,epic";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    public static String toString(LocalDateTime dateTime) {
        return dateTime.format(formatter);
    }

    public void save() {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {

            writer.write(HEADER + "\n");

            for (Task task : getTasks()) writer.write(task.toCSV() + "\n");

            for (Subtask subtask : getSubtasks()) writer.write(subtask.toCSV() + "\n");

            for (Epic epic : getAllEpics()) writer.write(epic.toCSV() + "\n");

        } catch (IOException e) {
            System.out.println("Ошибка сохранения данных в файл: " + e.getMessage());
        }

    }

    @Override
    public int addTask(Task task) {
        int id = super.addTask(task);
        save();
        return id;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        int id = super.addSubtask(subtask);
        save();
        return id;
    }

    @Override
    public int addEpic(Epic epic) {
        int id = super.addEpic(epic);
        save();
        return id;
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    public TaskAbstract fromString(String value) throws ManagerSaveException {
        String[] data = value.split(",", -1); // -1 сохраняет пустые ячейки

        if (data.length < 5) {
            throw new ManagerSaveException("Неверный формат строки задачи: " + value);
        }

        int id = Integer.parseInt(data[0]);
        TaskType type = TaskType.valueOf(data[1]);
        String name = data[2];
        TaskStatus status = TaskStatus.valueOf(data[3]);
        String description = data[4];

        Duration duration = null;
        if (!data[5].isEmpty()) {
            long minutes = Long.parseLong(data[5]);
            duration = Duration.ofMinutes(minutes);
        }

        LocalDateTime startTime = null;
        if (!data[6].isEmpty()) {
            startTime = LocalDateTime.parse(data[6], formatter); // Парсинг с использованием formatter
        }

        TaskAbstract taskAbstract;

        switch (type) {
            case TASK -> {
                Task task = new Task(TaskType.TASK, name, description, startTime, duration);
                task.setStatus(status);
                task.setId(id);
                taskAbstract = task;
            }
            case SUBTASK -> {
                if (data.length < 8 || data[7].isEmpty()) {
                    throw new ManagerSaveException("Неверный формат строки подзадачи: отсутствует epicId: " + value);
                }
                int epicId = Integer.parseInt(data[7]);
                Subtask subtask = new Subtask(TaskType.SUBTASK, name, description, startTime, duration);
                subtask.setStatus(status);
                subtask.setId(id);
                subtask.setEpicId(epicId);
                taskAbstract = subtask;
            }
            case EPIC -> {
                Epic epic = new Epic(TaskType.EPIC, name, description);
                epic.setStatus(status);
                epic.setId(id);
                epic.setStartTime(startTime); // может быть null — норм
                epic.setDuration(duration);
                taskAbstract = epic;
            }
            default -> throw new ManagerSaveException("Неизвестный тип задачи: " + type);
        }

        return taskAbstract;
    }

    public static FileBackedTaskManager loadFromFile(FileBackedTaskManager fileBackedTaskManager, File file) throws ManagerSaveException {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            if (lines.isEmpty() || !lines.get(0).equals(HEADER)) return manager;

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (!line.isBlank()) {
                    TaskAbstract task = manager.fromString(line);
                    switch (task.getType()) {
                        case TASK -> manager.tasks.put(task.getId(), (Task) task);
                        case SUBTASK -> {
                            Subtask subtask = (Subtask) task;
                            manager.subtasks.put(subtask.getId(), subtask);
                            Epic epic = manager.epics.get(subtask.getEpicId());
                            if (epic != null) {
                                epic.addSubtaskId(subtask.getId());
                            }
                        }
                        case EPIC -> manager.epics.put(task.getId(), (Epic) task);
                    }
                }
            }

            manager.epics.values().forEach(manager::updateEpicTimeAndStatus);

        } catch (IOException e) {
            System.out.println("Ошибка загрузки файла " + e.getMessage());
        }

        return manager;
    }
}
