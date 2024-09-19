package Tests;
import taskstatus.TaskStatus;
import task.Task;
import taskmanager.Node;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NodeTest {

    @Test
    void testNodeLinking() {
        Task task1 = new Task(1, "Task 1", "Description 1", TaskStatus.NEW);
        Task task2 = new Task(2, "Task 2", "Description 2", TaskStatus.NEW);

        Node node1 = new Node(task1);
        Node node2 = new Node(task2);

        node1.setNext(node2);
        node2.setPrev(node1);

        assertEquals(node2, node1.getNext(), "Следующий узел должен быть правильным.");
        assertEquals(node1, node2.getPrev(), "Предыдущий узел должен быть правильным.");
    }
}