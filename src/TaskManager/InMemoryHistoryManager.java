package taskmanager;

import task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> history = new LinkedList<>();
    private static final int MAX_HISTORY_SIZE = 10;
    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void clear() {
        nodeMap.clear();
        head = null;
        tail = null;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node current = head;
        while (current != null) {
            history.add(current.getTask()); // Используем геттер для получения задачи
            current = current.next;
        }
        return history;
    }

    @Override
    public void add(Task task) {

        int taskId = task.getId();

        // Если задача уже в истории, удаляем старый узел
        if (nodeMap.containsKey(taskId)) {

            removeNode(nodeMap.get(taskId));
        }

        // Добавляем новый узел в конец списка
        linkLast(task);

        // Если история превышает допустимый размер, удаляем старый узел
        if (nodeMap.size() > MAX_HISTORY_SIZE) {

            removeOldest();
        }
    }

    @Override
    public void remove(int id) {

        if (nodeMap.containsKey(id)) {
            removeNode(nodeMap.get(id));
        }
    }

    // Добавление узла в конец списка
    private void linkLast(Task task) {

        Node newNode = new Node(task);
        nodeMap.put(task.getId(), newNode);

        if (tail == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
    }

    // Удаление узла
    private void removeNode(Node node) {

        if (node == null) return;

        nodeMap.remove(node.getTask().getId()); // Используем геттер для получения ID задачи

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next; // Если удаляемый узел был головой
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev; // Если удаляемый узел был хвостом
        }
    }

    // Удаление самого старого узла (глава списка)
    private void removeOldest() {

        if (head != null) {
            removeNode(head);
        }
    }


}