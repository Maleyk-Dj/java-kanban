import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private Map<Integer, Task> tasks = new HashMap<>();
    private java.util.Map<Integer, Subtask> subtasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
        loadFromFile(file);
    }

    public void save() {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {

            writer.write("id,type,name,status,description,epic\n");

            for (Task task : getTasks()) {
                writer.write(task.toCSV() + "\n");
            }
            for (Subtask subtask : getSubtasks()) {
                writer.write(subtask.toCSV() + "\n");
            }
            for (Epic epic : getAllEpics()) {
                writer.write(epic.toCSV() + "\n");
            }
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
        String[] data = value.split(",");
        if (data.length < 5) {
            throw new ManagerSaveException("Неверный формат строки задачи: " + value);
        }
        int id = Integer.parseInt(data[0]);
        TaskType type = TaskType.valueOf(data[1]);
        String name = data[2];
        TaskStatus status = TaskStatus.valueOf(data[3]);
        String description = data[4];
        TaskAbstract taskAbstract;

        switch (type) {
            case TASK:
                taskAbstract = new Task(name, description, status);
                break;
            case SUBTASK:
                if (data.length < 6) {
                    throw new ManagerSaveException("Неверный формат сроки подзадачи: отсуствует epicId: " + value);
                }
                int epicId = Integer.parseInt(data[5]);
                taskAbstract = new Subtask(name, description, status);
                break;
            default:
                throw new ManagerSaveException("Неизвестный тип задачи: " + type);
        }
        taskAbstract.setId(id);
        return taskAbstract;
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {

        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            if (lines.isEmpty()) {
                return fileBackedTaskManager;
            }
            if (!lines.get(0).equals("id,type,name,status,description,epic")) {
                throw new ManagerSaveException("Неверный формат файла");
            }
            for (int i = 1; i < 6; i++) {
                String line = lines.get(i);
                TaskAbstract task = fileBackedTaskManager.fromString(line);
                if (task instanceof Task) {
                    fileBackedTaskManager.tasks.put(task.getId(), (Task) task);
                } else if (task instanceof Subtask) {
                    fileBackedTaskManager.subtasks.put(task.getId(), (Subtask) task);
                } else if (task instanceof Epic) {
                    fileBackedTaskManager.epics.put(task.getId(), (Epic) task);
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при загрузке задачи из строки " + e.getMessage());
        }
        return fileBackedTaskManager;
    }
}
