
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Subtask> subtasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected static int idCounter = 0;
    HistoryManager historyManager = new InMemoryHistoryManager();
    protected final TreeSet<TaskAbstract> prioritizedTasks = new TreeSet<>(Comparator.comparing(TaskAbstract::getStartTime));

    @Override
    public int addTask(Task task) {
        if (task.getStartTime() != null && hasTimeIntersection(task)) {
            throw new ManagerSaveException("Время задачи пересекается с другой задачей");
        }
        task.setId(idCounter++);
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
        return task.getId();
    }

    @Override
    public int addSubtask(Subtask subtask) {
        if (subtask.getStartTime() != null && hasTimeIntersection(subtask)) {
            throw new ManagerSaveException("Время подзадачи пересекается с другой задачей.");
        }
        subtask.setId(idCounter++);
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null && epic.getId() != subtask.getId()) {
            epic.addSubtaskId(subtask.getId());
            updateEpicTimeAndStatus(epic);
        }
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
        return subtask.getId();
    }

    @Override
    public int addEpic(Epic epic) {
        epic.setId(idCounter++);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            throw new NotFoundException("Задача с id="+task.getId()+" не найдена.");
        }
        prioritizedTasks.remove(tasks.get(task.getId()));
        if (task.getStartTime() != null && hasTimeIntersection(task)) {
            prioritizedTasks.add(tasks.get(task.getId()));
            throw new ManagerSaveException("Время задачи пересекается с другой задачей.");
        }
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            throw new NotFoundException("Подзадача с id=" + subtask.getId() + " не найдена.");
        }
        prioritizedTasks.remove(subtasks.get(subtask.getId()));
        if (subtask.getStartTime() != null && hasTimeIntersection(subtask)) {
            prioritizedTasks.add(subtasks.get(subtask.getId()));
            throw new ManagerSaveException("Время подзадачи пересекается с другой задачей");
        }
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            updateEpicTimeAndStatus(epic);
        }
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
    }


    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            throw new NotFoundException("Эпик с id=" + epic.getId() + " не найден.");
        }
            epics.put(epic.getId(), epic);
            updateEpicTimeAndStatus(epic);
        }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new NotFoundException("Задача с id="+id+" не найдена.");
        }
        historyManager.addToHistory(task);
        return task;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null){
            throw new NotFoundException("Подзадача с id=" + id + " не найдена.");
        }
        historyManager.addToHistory(subtask);
        return subtask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic == null){
            throw new NotFoundException("Эпик с id=" + id + " не найден.");
        }
        historyManager.addToHistory(epic);
        return epic;
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new NotFoundException("Эпик с id=" + epicId + " не найден.");
        }
        return epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void removeTask(int id) {
        Task task = tasks.remove(id);
        if (task == null) {
            throw new NotFoundException("Задача с id=" + id + " не найдена.");
        }
            prioritizedTasks.remove(task);
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            throw new NotFoundException("Подзадача с id=" + id + " не найдена.");
        }
            prioritizedTasks.remove(subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.getSubtaskIds().remove((Integer) id);
                updateEpicTimeAndStatus(epic);
            }
        }

    @Override
    public void removeEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic == null) {
            throw new NotFoundException("Эпик с id=" + id + " не найден.");
        }
            for (int subtaskId : epic.getSubtaskIds()) {
                prioritizedTasks.remove(subtasks.get(subtaskId));
                subtasks.remove(subtaskId);

            }
        }

    @Override
    public void removeAllTasks() {
        tasks.values().forEach(prioritizedTasks::remove);
        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.values().forEach(prioritizedTasks::remove);
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.getSubtaskIds().clear();
            updateEpicTimeAndStatus(epic);
        });
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
        prioritizedTasks.clear();
    }

    public TreeSet<TaskAbstract> getPrioritizedTasks() {
        return prioritizedTasks;
    }


    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    protected boolean hasTimeIntersection(TaskAbstract newTask) {
        if (newTask.getStartTime() == null) return false;
        return prioritizedTasks.stream()
                .filter(task -> task.getStartTime() != null && task.getEndTime() != null)
                .anyMatch(existing -> !existing.equals(newTask) && newTask.getStartTime().isBefore(existing.getEndTime())
                        && newTask.getEndTime().isAfter(existing.getStartTime()));
    }

    protected void updateEpicTimeAndStatus(Epic epic) {
        List<Subtask> epicSubtasks = getEpicSubtasks(epic.getId());
        if (epicSubtasks.isEmpty()) {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(null);
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        LocalDateTime start = epicSubtasks.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime end = epicSubtasks.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
        Duration totalDuration = epicSubtasks.stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(java.time.Duration.ZERO, Duration::plus);

        epic.setStartTime(start);
        epic.setEndTime(end);
        epic.setDuration(totalDuration);

        boolean allNew = epicSubtasks.stream()
                .allMatch(s -> s.getStatus() == TaskStatus.NEW);
        boolean allDone = epicSubtasks.stream()
                .allMatch(s -> s.getStatus() == TaskStatus.DONE);
        if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}


