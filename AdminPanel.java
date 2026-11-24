
import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdminPanel {

    private static ArrayList<String> activityLog = new ArrayList<>();

    public static void show(Scanner sc) {

        // Only admins can access this panel
        if (!ClubPilotApp.isAdmin()) {
            System.out.println("Access denied. Admin privileges required.");
            return;
        }

        addActivity("Admin opened the Admin Panel");

        int choice;

        do {
            System.out.println("\n========== ADMIN PANEL ==========");
            System.out.println("1. Manage Members");
            System.out.println("2. View My Profile");
            System.out.println("3. View Activity Log");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter your choice: ");

            while (!sc.hasNextInt()) {
                System.out.print("Please enter a valid number: ");
                sc.next();
            }

            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    addActivity("Admin opened Member Management");
                    MembersMenu.show(sc);
                    break;

                case 2:
                    addActivity("Admin opened Profile");
                    ProfileMenu.show(sc);
                    break;

                case 3:
                    viewActivityLog();
                    break;

                case 4:
                    addActivity("Admin exited the Admin Panel");
                    System.out.println("Returning to Main Menu...");
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }

        } while (choice != 4);
    }

    private static void viewActivityLog() {

        System.out.println("\n========== ADMIN ACTIVITY LOG ==========");

        if (activityLog.isEmpty()) {
            System.out.println("No activities recorded yet.");
            return;
        }

        for (int i = activityLog.size() - 1; i >= 0; i--) {
            System.out.println(activityLog.get(i));
        }

        System.out.println("========================================");
    }

    private static void addActivity(String action) {

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        String timestamp = LocalDateTime.now().format(formatter);

        activityLog.add("[" + timestamp + "] " + action);
    }
}
