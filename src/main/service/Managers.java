package main.service;

public class Managers {

    public static TaskManagerImpl getDefault() {
        return new TaskManagerImpl();
    }

    public static HistoryManagerImpl getDefaultHistory() {
        return new HistoryManagerImpl();
    }
}
