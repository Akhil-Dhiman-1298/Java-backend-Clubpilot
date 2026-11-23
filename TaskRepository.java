package repository;

import java.util.HashMap;
import java.util.Map;

import model.Task;

public class TaskRepository {

    private Map<Integer, Task> tasks = new HashMap<>();
    private int nextTaskId = 1;

    public int nextId() {
        return nextTaskId++;
    }

    public void add(Task task) {

        if (tasks.containsKey(task.getTaskId())) {
            System.out.println("Task ID already exists!");
            return;
        }

        tasks.put(task.getTaskId(), task);
        System.out.println("Task added successfully.");
    }

    public Task findById(int taskId) {
        return tasks.get(taskId);
    }

    public Map<Integer, Task> findAll() {
        return tasks;
    }

    public void delete(int taskId) {
        tasks.remove(taskId);
    }

    public boolean exists(int taskId) {
        return tasks.containsKey(taskId);
    }
}