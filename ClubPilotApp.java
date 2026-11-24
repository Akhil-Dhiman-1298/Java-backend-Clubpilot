import java.util.*;
public class ClubPilotApp{
    static Scanner sc=new Scanner(System.in);
    static Map<String,String> users=new HashMap<>();
    static Map<String,String> displayNames=new HashMap<>();
    static Map<String,String> emails=new HashMap<>();
    static Map<String,String> roles=new HashMap<>();
    static String currentUser=null;
    public static void main(String[] args){
        DatabaseManager.loadUsers();
        if(users.isEmpty()){
            DatabaseManager.saveUser("admin", "686admin123", "Admin", "admin@clubpilot.com", "admin");
            DatabaseManager.saveUser("lead1", "686leader123", "Team Lead", "lead1@clubpilot.com", "lead");
            DatabaseManager.saveUser("user1", "686user1", "User One", "user1@clubpilot.com", "member");
            DatabaseManager.saveUser("user2", "686user2", "User Two", "user2@clubpilot.com", "member");
            DatabaseManager.saveUser("user3", "686user3", "User Three", "user3@clubpilot.com", "member");
            DatabaseManager.loadUsers();
        }
        System.out.println("Clubpilot-Login");
        System.out.println("(Type 'new' at Username to create an account)");

        if(!login()){
            return;
        }

        while(true){
            System.out.println("Clubpilot Main Menu");
            System.out.println("1) Events Module");
            System.out.println("2) Tasks Module");
            System.out.println("3) My Profile");

            if(isAdmin()){
                System.out.println("4) Members");
                System.out.println("5) Logout");
                System.out.println("6) Exit");
            }
            else{
                System.out.println("4) Logout");
                System.out.println("5) Exit");
            }

            System.out.print("Choice: ");
            String choice=sc.nextLine().trim();

            if(choice.equals("1")){
                CentralBackend.startEvents();
            }
            else if(choice.equals("2")){
                TaskManagementApp.startTasks();
            }
            else if(choice.equals("3")){
                ProfileMenu.show(sc);
            }
            else if(isAdmin() && choice.equals("4")){
                MembersMenu.show(sc);
            }
            else if((isAdmin() && choice.equals("5")) || (!isAdmin() && choice.equals("4"))){
                currentUser=null;
                System.out.println("Logged out.\n");
                if(!login()){
                    return;
                }
            }
            else if((isAdmin() && choice.equals("6")) || (!isAdmin() && choice.equals("5"))){
                System.out.println("Bye!");
                return;
            }
            else{
                System.out.println("Invalid choice.");
            }
        }
    }

    public static boolean isManager(){
        String r=roles.get(currentUser);
        return "admin".equals(r) || "lead".equals(r);
    }

    public static boolean isAdmin(){
        return "admin".equals(roles.get(currentUser));
    }

    public static String getRole(String username){
        return roles.getOrDefault(username, "member");
    }

    static boolean login(){
        while(true){
            System.out.print("\nUsername(or 'exit', or 'new' to register): ");
            String u=sc.nextLine().trim();
            if(u.equalsIgnoreCase("exit")){
                return false;
            }
            if(u.equalsIgnoreCase("new")){
                System.out.print("Choose a username: ");
                String newUser=sc.nextLine().trim();
                if(newUser.isEmpty() || newUser.equalsIgnoreCase("new") || newUser.equalsIgnoreCase("exit")){
                    System.out.println("Invalid username.");
                    continue;
                }
                if(users.containsKey(newUser)){
                    System.out.println("Username already exists! Try another.");
                    continue;
                }
                System.out.print("Choose a password: ");
                String newPass=sc.nextLine().trim();
                if(newPass.length() < 8){
                    System.out.println("Password must be at least 8 characters long.");
                    continue;
                }
                if(!newPass.startsWith("686")){
                    System.out.println("Password must start with '686' (e.g., 686mypass).");
                    continue;
                }
                System.out.print("Confirm password: ");
                String confirmPass=sc.nextLine().trim();

                if(!newPass.equals(confirmPass)){
                    System.out.println("Passwords do not match! Account creation failed.");
                    continue;
                }

                System.out.print("Enter email: ");
                String newEmail=sc.nextLine().trim().toLowerCase();
                
                if(newEmail.isEmpty() || !newEmail.contains("@") || !newEmail.contains(".")){
                    System.out.println("Invalid email. Registration cancelled.");
                    continue;
                }

                users.put(newUser, newPass);
                displayNames.put(newUser, newUser);
                emails.put(newUser, newEmail);
                roles.put(newUser, "member");
                
                DatabaseManager.saveUser(newUser, newPass, newUser, newEmail, "member");
                System.out.println("Account created successfully! You can now login.");
                continue;
            }

            System.out.print("Password: ");
            String p=sc.nextLine().trim();

            if(users.containsKey(u) && users.get(u).equals(p)){
                System.out.println("Login successful! Role: " + getRole(u));
                currentUser=u;
                return true;
            }
            else{
                System.out.println("Invalid credentials, try again.");
            }
        }
    }
}
