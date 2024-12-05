package main.service;

import java.io.File;

public class Managers {

    public static InMemoryTaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static FileBackedTaskManager getFile(File file) {
        return new FileBackedTaskManager(file);
    }

    public static HistoryManagerImpl getDefaultHistory() {
        return new HistoryManagerImpl();
    }
}
