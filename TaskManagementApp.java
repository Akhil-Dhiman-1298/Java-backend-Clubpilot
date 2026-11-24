import java.util.Scanner;
public class TaskManagementApp {
    static Scanner sc = new Scanner(System.in);
    static TaskRepository taskRepo = new TaskRepository();
    static TaskService taskService = new TaskService(taskRepo);
    public static void startTasks() {
        seedTasks();
        System.out.println("================================");
        System.out.println("   CLUBPILOT TASK MANAGEMENT");
        System.out.println("================================");
        boolean manager = ClubPilotApp.isManager();
        while (true) {
            if (manager) {
                System.out.println("\n1) Show all tasks");
                System.out.println("2) Create task");
                System.out.println("3) Assign task");
                System.out.println("4) Update task status");
                System.out.println("5) Update task details");
                System.out.println("6) Delete task");
                System.out.println("7) Search task by ID");
                System.out.println("8) Search by title");
                System.out.println("9) Filter by status");
                System.out.println("10) Filter by priority");
                System.out.println("11) Sort by priority");
                System.out.println("12) Sort by deadline");
                System.out.println("13) Task summary");
                System.out.println("0) Back to Main Menu");

            } else {
                System.out.println("\n1) View my tasks");
                System.out.println("2) Update task status");
                System.out.println("0) Back to Main Menu");
            }

            System.out.print("Choice: ");
            String choice = sc.nextLine().trim();
            if (manager) {
                switch (choice) {
                    case "1":
                        taskService.displayAllTasks();
                        break;
                    case "2":
                        createTaskFlow();
                        break;
                    case "3":
                        taskService.assignTask(
                            promptInt("Task ID"),
                            promptStr("Member username")
                        );
                        break;
                    case "4":
                        taskService.updateTaskStatus(
                            promptInt("Task ID"),
                            TaskStatus.valueOf(promptStr("Status (PENDING/IN_PROGRESS/COMPLETED)").toUpperCase()),
                            ClubPilotApp.currentUser,
                            true
                        );
                        break;
                    case "5":
                        taskService.updateTask(
                            promptInt("Task ID"),
                            promptStr("New title"),
                            promptStr("New description"),
                            Priority.valueOf(
                                promptStr("Priority (LOW/MEDIUM/HIGH)")
                                    .toUpperCase()
                            ),
                            promptStr("New deadline")
                        );
                        break;
                    case "6":
                        taskService.deleteTask(promptInt("Task ID"));
                        break;
                    case "7":
                        taskService.searchTask(promptInt("Task ID"));
                        break;
                    case "8":
                        taskService.searchByTitle(promptStr("Title"));
                        break;
                    case "9":
                        taskService.showTasksByStatus(
                            TaskStatus.valueOf(
                                promptStr("Status").toUpperCase()
                            )
                        );
                        break;
                    case "10":
                        taskService.showTasksByPriority(
                            Priority.valueOf(
                                promptStr("Priority").toUpperCase()
                            )
                        );
                        break;
                    case "11":
                        taskService.sortByPriority();
                        break;
                    case "12":
                        taskService.sortByDeadline();
                        break;
                    case "13":
                        taskService.showTaskSummary();
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Invalid choice!");
                }

            } else {
                switch (choice) {
                    case "1":
                        taskService.showMyTasks(
                            ClubPilotApp.currentUser
                        );
                        break;
                    case "2":
                        taskService.updateTaskStatus(
                            promptInt("Task ID"),
                            TaskStatus.valueOf(
                                promptStr("Status (PENDING/IN_PROGRESS/COMPLETED)")
                                    .toUpperCase()
                            ),
                            ClubPilotApp.currentUser,
                            false
                        );
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Invalid choice!");
                }
            }
        }
    }

    static void seedTasks() {
        if (taskRepo.findAll().isEmpty()) {
            taskService.createTask(
                "Build Event Landing Page",
                "Create the React event landing page",
                Priority.HIGH,
                "2026-12-01",
                "user1",
                "lead1"
            );

            taskService.createTask(
                "Design Dashboard UI",
                "Create dashboard layout and styling",
                Priority.MEDIUM,
                "2026-12-05",
                "user1",
                "lead1"
            );
        }
    }

    static void createTaskFlow() {
        String title = promptStr("Title");
        String description = promptStr("Description");

        Priority priority = Priority.valueOf(
            promptStr("Priority (LOW/MEDIUM/HIGH)")
                .toUpperCase()
        );

        String deadline = promptStr("Deadline (YYYY-MM-DD)");
        String assignedTo = promptStr("Assigned to");
        String assignedBy = ClubPilotApp.currentUser;

        taskService.createTask(
            title,
            description,
            priority,
            deadline,
            assignedTo,
            assignedBy
        );
    }
    static String promptStr(String label) {
        System.out.print(label + ": ");
        return sc.nextLine().trim();
    }
    static int promptInt(String label) {
        while (true) {
            System.out.print(label + ": ");
            try {

                return Integer.parseInt(
                    sc.nextLine().trim()
                );

            } catch (NumberFormatException ex) {
                System.out.println(
                    "Enter a valid number."
                );
            }
        }
    }
}
