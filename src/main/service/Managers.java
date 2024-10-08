package main.service;

import main.classes.Epic;
import main.classes.Status;
import main.classes.SubTask;
import main.classes.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Managers {

    public static TaskManagerImpl getDefault() {
        return new TaskManagerImpl();
    }

    public static HistoryManagerImpl getDefaultHistory() {
        return new HistoryManagerImpl();
    }
}
