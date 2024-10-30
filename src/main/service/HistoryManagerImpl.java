package main.service;

import main.classes.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryManagerImpl implements HistoryManagerService {
    private final Map<Integer, Node> history = new HashMap<>();
    private Node<Task> first;
    private Node<Task> last;

    class Node<Task> {
        private Task data;
        private Node<Task> next;
        private Node<Task> prev;

        public Node(Node<Task> prev, Task data, Node<Task> next) {
            this.prev = prev;
            this.data = data;
            this.next = next;
        }
    }

    @Override
    public void add(Task task) {
        if (history.containsKey(task.getId())) { // если такая задача уже есть в истории - удаляем
            remove(task.getId());
        }

        // добавляем в историю
        final Node<Task> oldLast = last;
        final Node<Task> newNode = new Node<>(oldLast, task, null);
        last = newNode;
        if (oldLast == null) {
            first = newNode;
        } else {
            oldLast.next = newNode;
        }
        history.put(task.getId(), newNode);
    }

    @Override
    public void remove(int id) {                // удаляем запись из истории
        if (history.containsKey(id)) {
            Node node = history.get(id);
            if (node == first && node == last) {
                first = null;
                last = null;
                node.data = null;
            } else if (node == first) {
                node.next.prev = null;
                first = node.next;
            } else if (node == last) {
                node.prev.next = null;
                last = node.prev;
            } else {
                node.prev.next = node.next;
                node.next.prev = node.prev;
            }
            history.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> result = new ArrayList<>();
        Node node = first;
        while (node != null) {
            result.add((Task) node.data);
            node = node.next;
        }
        return result;
    }

}