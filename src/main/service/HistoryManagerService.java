package main.service;

import main.classes.Task;

import java.util.List;

public interface HistoryManagerService {
    void addHistory(Task task);
    public List<Task> getHistory();
}
