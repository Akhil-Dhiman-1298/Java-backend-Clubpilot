package service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import model.Task;
import model.Priority;
import model.TaskStatus;

import repository.TaskRepository;
import factory.TaskFactory;

public class TaskService {

    private TaskRepository taskRepo;

    public TaskService(TaskRepository taskRepo) {
        this.taskRepo = taskRepo;
    }

    public void displayAllTasks() {

        if (taskRepo.findAll().isEmpty()) {
            System.out.println("No tasks available.");
            return;
        }

        for (Task task : taskRepo.findAll().values()) {
            task.displayTask();
        }
    }

    public void createTask(String title, String description,
                           Priority priority, String deadline,
                           String assignedTo, String assignedBy) {

        Task task = TaskFactory.create(
            taskRepo.nextId(), title, description,
            priority, deadline, assignedTo, assignedBy
        );

        taskRepo.add(task);
    }

    public void assignTask(int taskId, String username) {

        Task task = taskRepo.findById(taskId);

        if (task == null) {
            System.out.println("Task not found!");
            return;
        }

        task.setAssignedTo(username);
        System.out.println("Task assigned successfully.");
    }

    public void showMyTasks(String username) {

        boolean found = false;

        for (Task task : taskRepo.findAll().values()) {

            if (task.getAssignedTo().equalsIgnoreCase(username)) {
                task.displayTask();
                found = true;
            }
        }

        if (!found) {
            System.out.println("No tasks assigned to you.");
        }
    }

    public void updateTaskStatus(int taskId, TaskStatus status,
                                 String username, boolean isManager) {

        Task task = taskRepo.findById(taskId);

        if (task == null) {
            System.out.println("Task not found!");
            return;
        }

        if (!isManager &&
            !task.getAssignedTo().equalsIgnoreCase(username)) {
            System.out.println("You cannot update this task.");
            return;
        }

        task.setStatus(status);
        System.out.println("Task status updated.");
    }

    public void updateTask(int taskId, String title,
                           String description, Priority priority,
                           String deadline) {

        Task task = taskRepo.findById(taskId);

        if (task == null) {
            System.out.println("Task not found!");
            return;
        }

        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(priority);
        task.setDeadline(deadline);

        System.out.println("Task updated successfully.");
    }

    public void deleteTask(int taskId) {

        if (!taskRepo.exists(taskId)) {
            System.out.println("Task not found!");
            return;
        }

        taskRepo.delete(taskId);
        System.out.println("Task deleted successfully.");
    }

    public void searchTask(int taskId) {

        Task task = taskRepo.findById(taskId);

        if (task == null) {
            System.out.println("Task not found!");
        } else {
            task.displayTask();
        }
    }

    public void searchByTitle(String title) {

        boolean found = false;

        for (Task task : taskRepo.findAll().values()) {

            if (task.getTitle().toLowerCase()
                    .contains(title.toLowerCase())) {
                task.displayTask();
                found = true;
            }
        }

        if (!found) {
            System.out.println("No matching tasks found.");
        }
    }

    public void showTasksByStatus(TaskStatus status) {

        for (Task task : taskRepo.findAll().values()) {
            if (task.getStatus() == status) {
                task.displayTask();
            }
        }
    }

    public void showTasksByPriority(Priority priority) {

        for (Task task : taskRepo.findAll().values()) {
            if (task.getPriority() == priority) {
                task.displayTask();
            }
        }
    }

    public void sortByPriority() {

        List<Task> taskList =
            new ArrayList<>(taskRepo.findAll().values());

        taskList.sort(
            Comparator.comparingInt(
                task -> task.getPriority() == Priority.HIGH ? 1 :
                        task.getPriority() == Priority.MEDIUM ? 2 : 3
            )
        );

        for (Task task : taskList) {
            task.displayTask();
        }
    }

    public void sortByDeadline() {

        List<Task> taskList =
            new ArrayList<>(taskRepo.findAll().values());

        taskList.sort(
            Comparator.comparing(Task::getDeadline)
        );

        for (Task task : taskList) {
            task.displayTask();
        }
    }

    public void showTaskSummary() {

        int total = 0;
        int pending = 0;
        int inProgress = 0;
        int completed = 0;

        for (Task task : taskRepo.findAll().values()) {

            total++;

            switch (task.getStatus()) {
                case PENDING: pending++; break;
                case IN_PROGRESS: inProgress++; break;
                case COMPLETED: completed++; break;
            }
        }

        System.out.println("\n------ TASK SUMMARY ------");
        System.out.println("Total Tasks     : " + total);
        System.out.println("Pending         : " + pending);
        System.out.println("In Progress     : " + inProgress);
        System.out.println("Completed       : " + completed);
    }
}