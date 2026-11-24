package model;

public class Task {

    private int taskId;
    private String title;
    private String description;
    private Priority priority;
    private TaskStatus status;
    private String deadline;
    private String assignedTo;
    private String assignedBy;

    public Task(int taskId, String title, String description,
                Priority priority, TaskStatus status,
                String deadline, String assignedTo,
                String assignedBy) {

        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.deadline = deadline;
        this.assignedTo = assignedTo;
        this.assignedBy = assignedBy;
    }

    public int getTaskId() { return taskId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Priority getPriority() { return priority; }
    public TaskStatus getStatus() { return status; }
    public String getDeadline() { return deadline; }
    public String getAssignedTo() { return assignedTo; }
    public String getAssignedBy() { return assignedBy; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setPriority(Priority priority) {
        this.priority = priority;
    }
    public void setStatus(TaskStatus status) {
        this.status = status;
    }
    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public void displayTask() {
        System.out.println("\n------------------------------");
        System.out.println("Task ID     : " + taskId);
        System.out.println("Title       : " + title);
        System.out.println("Description : " + description);
        System.out.println("Priority    : " + priority);
        System.out.println("Status      : " + status);
        System.out.println("Deadline    : " + deadline);
        System.out.println("Assigned To : " + assignedTo);
        System.out.println("Assigned By : " + assignedBy);
        System.out.println("------------------------------");
    }
}