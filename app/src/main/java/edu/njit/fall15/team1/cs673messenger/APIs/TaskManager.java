package edu.njit.fall15.team1.cs673messenger.APIs;

import java.util.ArrayList;
import java.util.List;

import edu.njit.fall15.team1.cs673messenger.models.Tasks;

/**
 * Created by jack on 15/11/26.
 */
public enum  TaskManager {
    INSTANCE;

    List<Tasks> allTasks = new ArrayList<>();

    public Tasks findTasks(String chatId){
        if (allTasks.size() != 0){
            for (Tasks tasks: allTasks){
                if (tasks.getChatId().equals(chatId)){
                    return tasks;
                }
            }
        }
        return null;
    }

    public void addTask(String chatId, String task){
        Tasks tasks = findTasks(chatId);
        if (tasks != null){
            tasks.addTask(task);
        }else{
            tasks = new Tasks(chatId);
            tasks.addTask(task);
            allTasks.add(tasks);
        }
    }

    public void removeTask(String chatId, int index){
        Tasks tasks = findTasks(chatId);
        if (tasks != null){
            if (tasks.getTaskList().size() != 0){
                tasks.removeTask(index);
            }
        }
    }
    public void removeTask(String chatId, String task){
        Tasks tasks = findTasks(chatId);
        if (tasks != null){
            if (tasks.getTaskList().size() != 0){
                tasks.removeTask(task);
            }
        }
    }
}
