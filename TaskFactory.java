package factory;

import model.Task;
import model.TaskStatus;
import model.Priority;

public class TaskFactory {

    public static Task create(
            int taskId,
            String title,
            String description,
            Priority priority,
            String deadline,
            String assignedTo,
            String assignedBy) {

        return new Task(
            taskId,
            title,
            description,
            priority,
            TaskStatus.PENDING,
            deadline,
            assignedTo,
            assignedBy
        );
    }
}