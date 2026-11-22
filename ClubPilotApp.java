import java.util.*;

public class ClubPilotApp {
    static Scanner sc = new Scanner(System.in);
    static Map<String, String> users = new HashMap<>();
    static String currentUser = null;
    public static void main(String[] args) {
        users.put("admin", "686admin123");
        users.put("lead1", "686leader123");
        users.put("user1", "686user1");
        users.put("user2", "686user2");
        users.put("user3", "686user3");
        System.out.println("Clubpilot-Login");
        System.out.println("(Type 'new' at Username to create an account)");
        if (!login()) return;

        while (true) {
            System.out.println("\nClubpilot Main Menu");
            System.out.println("1) Events Module");
            System.out.println("2) Tasks Module");
            System.out.println("3) Logout");
            System.out.println("4) Exit");
            System.out.print("Choice: ");

            String choice = sc.nextLine().trim();

            if (choice.equals("1")) {
                CentralBackend.startEvents();
            } else if (choice.equals("2")) {
                TaskManagementApp.startTasks();
            } else if (choice.equals("3")) {
                currentUser = null;
                System.out.println("Logged out.\n");
                if (!login()) return;
            } else if (choice.equals("4")) {
                System.out.println("Bye!");
                return;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    static boolean login() {
        while (true) {
            System.out.print("\nUsername (or 'exit', or 'new' to register): ");
            String u = sc.nextLine().trim();
            if (u.equalsIgnoreCase("exit")) return false;
            if (u.equalsIgnoreCase("new")) {
                System.out.print("Choose a username: ");
                String newUser = sc.nextLine().trim();
                
                if (newUser.isEmpty() || newUser.equalsIgnoreCase("new") || newUser.equalsIgnoreCase("exit")) {
                    System.out.println("Invalid username.");
                    continue;
                }
                if (users.containsKey(newUser)) {
                    System.out.println("Username already exists! Try another.");
                    continue;
                }

                System.out.print("Choose a password: ");
                String newPass = sc.nextLine().trim();
                users.put(newUser, newPass);
                System.out.println("Account created successfully! You can now login.");
                continue; 
            }


            System.out.print("Password: ");
            String p = sc.nextLine().trim();
            
            if (users.containsKey(u) && users.get(u).equals(p)) {
                System.out.println("Login successful!");
                currentUser = u;
                return true;
            } else {
                System.out.println("Invalid credentials, try again.");
            }
        }
    }
}