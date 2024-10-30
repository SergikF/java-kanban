package main.service;

import main.classes.Task;

import java.util.List;

public interface HistoryManagerService {
    void add(Task task);
    void remove(int id);

    List<Task> getHistory();
}
