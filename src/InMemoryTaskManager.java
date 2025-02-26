import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private static int idCounter = 0;
    InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

    @Override
    public int addTask(Task task) {
        task.setId(idCounter++);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public void removeTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public Task getTask(int id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            inMemoryHistoryManager.addToHistory(task);
            return task;
        }
        return null;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            Task oldTask = tasks.get(task.getId());
            if (task.isSubtask()) {
                Subtask newSubtask = (Subtask) task;
                int epicId = newSubtask.getEpicId();
                Epic epic = epics.get(epicId);
                if (!newSubtask.getStatus().equals(oldTask.getStatus())) {
                    updateStatusEpic(epic);
                }
                epics.put(epicId, epic);
            }
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public int addSubtask(Subtask subtask) {
        int epicId = subtask.getId();
        if (epics.containsKey(epicId)) {
            subtask.setId(idCounter++);
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(epicId);
            List<Integer> subtasksIds = epic.getSubtaskIds();
            if (subtasksIds == null) {
                subtasksIds = new ArrayList<>();
            }
            subtasksIds.add(subtask.getId());
            epic.setSubtaskIds(subtasksIds);
            updateStatusEpic(epic);
            epics.put(epicId, epic);
        }
        return subtask.getId();
    }

    @Override
    public void removeSubtask(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            int epicId = subtask.getEpicId();
            Epic epic = epics.get(epicId);
            subtasks.remove(id);
            List<Integer> subtaskIds = epic.getSubtaskIds();
            if (subtaskIds != null) {
                if (subtaskIds.contains(id)) {
                    subtaskIds.remove(Integer.valueOf(id));
                    epic.setSubtaskIds(subtaskIds);
                }
            }
            updateStatusEpic(epic);
            epics.put(epicId, epic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            updateTask(subtask);
        }
    }

    @Override
    public Subtask getSubtask(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            inMemoryHistoryManager.addToHistory(subtask);
            return subtask;
        }
        return null;
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            updateStatusEpic(epic);
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            List<Integer> subtaskIds = epic.getSubtaskIds();
            if (subtaskIds != null) {
                for (Integer subtaskId : subtaskIds) {
                    Subtask subtask = subtasks.get(subtaskId);
                    if (subtask != null) {
                        epicSubtasks.add(subtask);
                    }
                }
            }
        }
        return epicSubtasks;
    }

    @Override
    public int addEpic(Epic epic) {
        epic.setId(idCounter++);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public void removeEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            List<Integer> subtaskIds = epic.getSubtaskIds();
            if (subtaskIds != null) {
                for (Integer subtaskId : subtaskIds) {
                    subtasks.remove(subtaskId);
                }
            }
            epics.remove(id);
        }
    }

@Override
    public Epic getEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            inMemoryHistoryManager.addToHistory(epic);
            return epic;
        }
        return null;
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeAllEpics() {
        for (Epic epic : epics.values()) {
            epics.clear();
        }
    }

    @Override
    public void updateStatusEpic(Epic epic) {
        List<Integer> subtaskIds = epic.getSubtaskIds();
        if (subtaskIds.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        boolean allDone = true;
        boolean hasInProgress = false;
        for (Integer subtaskId : subtaskIds) {
            Subtask subtask = subtasks.get(subtaskId);
            TaskStatus status = subtask.getStatus();
            switch (status) {
                case IN_PROGRESS:
                    hasInProgress = true;
                    allDone = false;
                    break;
                case NEW:
                    allDone = false;
                    break;
                case DONE:
                    break;
            }
        }
        if (hasInProgress) {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        } else if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.NEW);
        }

    }

}
