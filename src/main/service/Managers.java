package main.service;

import java.nio.file.Path;

public class Managers {

    public static InMemoryTaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static FileBackedTaskManager getFile(Path path) {
        return new FileBackedTaskManager(path);
    }

    public static HistoryManagerImpl getDefaultHistory() {
        return new HistoryManagerImpl();
    }
}
