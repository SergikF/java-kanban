package main.service;

import main.classes.Task;

import java.util.*;

public class HistoryManagerImpl implements HistoryManagerService {
    private final Map<Integer, Node<Task>> history = new HashMap<>();
    private Node<Task> first;
    private Node<Task> last;

    static class Node<T> {
        private T data;
        private Node<T> next;
        private Node<T> prev;

        public Node(Node<T> prev, T data, Node<T> next) {
            this.prev = prev;
            this.data = data;
            this.next = next;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Node<?> node)) return false;
            return Objects.equals(data, node.data) && Objects.equals(next, node.next) && Objects.equals(prev, node.prev);
        }

        @Override
        public int hashCode() {
            return Objects.hash(data, next, prev);
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
            Node<Task> node = history.get(id);
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
        Node<Task> node = first;
        while (node != null) {
            result.add(node.data);
            node = node.next;
        }
        return result;
    }

}
