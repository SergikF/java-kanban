package main.service;

import main.classes.Task;

import java.util.ArrayList;
import java.util.List;

public class HistoryManagerImpl implements HistoryManagerService {
    private final ArrayList<Task> history = new ArrayList<>();

    @Override
    public void addHistory(Task task) {
        if (history.size() == 10) {
            history.removeFirst();
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
