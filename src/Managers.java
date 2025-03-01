
public class Managers {

    private Managers () {
    }

    public static TaskManager getDefault () {
        return new InMemoryTaskManager();
    }

    public static HistoryManager historyManager () {
        return new InMemoryHistoryManager();
    }
}
