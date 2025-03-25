import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    static class Node {
        Task task;
        Node next;
        Node prev;

        public Node(Task task) {
            this.task = task;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "task=" + task +
                    '}';
        }
    }

    //реализуем двухсвязный список
    private Node head;
    private Node tail;
    private int size;

    //далее хеш-таблицу
    private final Map<Integer, Node> myTaskMap = new HashMap<>();

    private void linkLast(Task task) {
        Node newNode = new Node(task);
        if (head == null) {
            head = newNode;//то новая нода становится головой
        } else {
            tail.next = newNode;//если список не пуст, устанавливаем двухсторонние связи
            newNode.prev = tail;
        }
        tail = newNode;//теперь новая нода стала хвостом
        myTaskMap.put(task.getId(), newNode);
        size++;
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }
        if (node.prev != null) {
            node.prev.next = node.next;//если да, предыидущий узел указывает на следующий за удаляемым
        } else {
            head = node.next;//если нет то этот узел становится head
        }
        if (node.next != null) {
            node.next.prev = node.prev;//если да, то последующий узел указывает на предыдущий перед удаляемым
        } else {
            tail = node.prev;//если нет, то следующий узел становится хвостом
        }
        node.prev = null;
        node.next = null;
        myTaskMap.remove(node.task.getId());
        size--;
    }

    @Override
    public void addToHistory(TaskAbstract task) {
        if (task == null) {
            return;
        }
        Task taskCopy = new Task(task.getName(), task.getDescription(), task.getStatus());//создаем копию
        taskCopy.setId(task.getId());//устанавливаем в ID копии ID задачи
        int taskId = task.getId();//получаем ID задачи
        if (myTaskMap.containsKey(taskId)) {
            removeNode(myTaskMap.get(taskId));
        }
        linkLast(taskCopy);//добавляем в конец списка
    }

    @Override
    public void remove(int id) {
        if (myTaskMap.containsKey(id)) {
            removeNode(myTaskMap.get(id));
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node current = head;
        while (current != null) {
            history.add(current.task);
            current = current.next;
        }
        return history;
    }
}


