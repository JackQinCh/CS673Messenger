package edu.njit.fall15.team1.cs673messenger.models;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jack on 15/11/26.
 */
public class Tasks {
    private List<String> taskList = new LinkedList<>();
    private final String chatId;

    public Tasks(String chatId) {
        this.chatId = chatId;
    }

    public void addTask(String task){
        taskList.add(task);
    }

    public void removeTask(int index){
        if (taskList.size() != 0){
            String task = taskList.get(index);
            taskList.remove(index);
            taskList.add(task+"(Finished)");
        }
    }

    public void removeTask(String task){
        if (taskList.size() != 0 && taskList.contains(task)){
            taskList.remove(task);
            taskList.add(task+"(Finished)");
        }
    }

    public List<String> getTaskList() {
        List<String> list = new ArrayList<>();
        list.addAll(taskList);
        return list;
    }

    public String getChatId() {
        return chatId;
    }
}
