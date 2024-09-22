package test;

import task.Task;
import taskstatus.TaskStatus;
import taskmanager.Node;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NodeTest {

    @Test
    void testNodeLinking() {
        Task task1 = new Task(1, "Задача 1", " ", TaskStatus.NEW);
        Task task2 = new Task(2, "Задача 2", " ", TaskStatus.NEW);

        Node node1 = new Node(task1);
        Node node2 = new Node(task2);

        node1.setNext(node2);
        node2.setPrev(node1);

        // Проверка связи
        assertEquals(node2, node1.getNext(), "Следующий узел должен быть правильным.");
        assertEquals(node1, node2.getPrev(), "Предыдущий узел должен быть правильным.");
    }

    @Test
    void testDoubleLinking() {
        Task task1 = new Task(1, "Задача 1", " ", TaskStatus.NEW);
        Task task2 = new Task(2, "Задача 2", " ", TaskStatus.NEW);
        Task task3 = new Task(3, "Задача 3", " ", TaskStatus.NEW);

        Node node1 = new Node(task1);
        Node node2 = new Node(task2);
        Node node3 = new Node(task3);

        node1.setNext(node2);
        node2.setPrev(node1);
        node2.setNext(node3);
        node3.setPrev(node2);

        // Проверка цепочки узлов
        assertEquals(node2, node1.getNext(), "Следующий узел должен быть правильным для node1.");
        assertEquals(node3, node2.getNext(), "Следующий узел должен быть правильным для node2.");
        assertEquals(node1, node2.getPrev(), "Предыдущий узел должен быть правильным для node2.");
        assertEquals(node2, node3.getPrev(), "Предыдущий узел должен быть правильным для node3.");
    }

    @Test
    void testNodeProperties() {
        Task task = new Task(1, "Задача 1", " ", TaskStatus.NEW);
        Node node = new Node(task);

        assertEquals(task, node.getTask(), "Задача в узле должна совпадать с заданной задачей.");
        assertNull(node.getNext(), "Следующий узел должен быть null при инициализации.");
        assertNull(node.getPrev(), "Предыдущий узел должен быть null при инициализации.");
    }
}