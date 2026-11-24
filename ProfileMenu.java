import java.util.Scanner;

public class ProfileMenu{
    public static void show(Scanner sc){
        if(ClubPilotApp.currentUser == null){
            System.out.println("Not logged in.");
            return;
        }
        
        String u=ClubPilotApp.currentUser;

        while(true){
            String name=ClubPilotApp.displayNames.getOrDefault(u, u);
            String email=ClubPilotApp.emails.getOrDefault(u, "");
            String role=ClubPilotApp.getRole(u);

            System.out.println("\n===== MY PROFILE =====");
            System.out.println("Username : " + u);
            System.out.println("Name     : " + name);
            System.out.println("Email    : " + (email.isEmpty() ? "(not set)" : email));
            System.out.println("Role     : " + role);
            System.out.println("----------------------");
            System.out.println("1. Edit display name");
            System.out.println("2. Edit email");
            System.out.println("3. Change password");
            System.out.println("4. My events");
            System.out.println("5. My tasks");
            System.out.println("6. Back");
            System.out.print("Choice: ");

            String choice=sc.nextLine().trim();

            if(choice.equals("1")){
                System.out.print("New name: ");
                String n=sc.nextLine().trim();
                if(n.isEmpty()){
                    System.out.println("Name cannot be blank.");
                }
                else{
                    ClubPilotApp.displayNames.put(u, n);
                    System.out.println("Updated.");
                }
            }
            else if(choice.equals("2")){
                System.out.print("New email: ");
                String e=sc.nextLine().trim().toLowerCase();
                if(e.isEmpty() || !e.contains("@") || !e.contains(".")){
                    System.out.println("Invalid email.");
                }
                else{
                    ClubPilotApp.emails.put(u, e);
                    System.out.println("Updated.");
                }
            }
            else if(choice.equals("3")){
                changePassword(sc, u);
            }
            else if(choice.equals("4")){
                showMyEvents(u);
            }
            else if(choice.equals("5")){
                showMyTasks(u);
            }
            else if(choice.equals("6")){
                return;
            }
            else{
                System.out.println("Invalid choice.");
            }
        }
    }

    private static void changePassword(Scanner sc, String u){
        System.out.print("Current password: ");
        String oldPass=sc.nextLine().trim();
        
        if(!oldPass.equals(ClubPilotApp.users.get(u))){
            System.out.println("Incorrect current password.");
            return;
        }
        
        System.out.print("New password: ");
        String p1=sc.nextLine().trim();
        
        if(p1.isEmpty()){
            System.out.println("Password cannot be blank.");
            return;
        }
        
        System.out.print("Confirm new password: ");
        String p2=sc.nextLine().trim();
        
        if(!p1.equals(p2)){
            System.out.println("Passwords do not match.");
            return;
        }
        
        ClubPilotApp.users.put(u, p1);
        System.out.println("Password changed.");
    }

    private static void showMyEvents(String u){
        System.out.println("\n--- My Events ---");
        CentralBackend.events = DatabaseManager.loadEvents();
        
        boolean isManager=ClubPilotApp.isManager();

        if(isManager){
            System.out.println("Managed Events(All Events):");
            boolean any=false;
            for(Event e : CentralBackend.events){
                System.out.println("  [" + e.getId() + "] " + e.getTitle() + " (" + e.getStatus() + ") on " + e.getDate());
                any=true;
            }
            if(!any){
                System.out.println("  (none)");
            }
        }
        else{
            System.out.println("Joined:");
            boolean any=false;
            for(Event e : CentralBackend.events){
                if(e.joined.contains(u)){
                    System.out.println("  [" + e.getId() + "] " + e.getTitle() + " (" + e.getStatus() + ") on " + e.getDate());
                    any=true;
                }
            }
            if(!any){
                System.out.println("  (none)");
            }
        }

        System.out.println("Attended(approved):");
        boolean anyAtt=false;
        for(Event e : CentralBackend.events){
            if(e.attended.contains(u)){
                System.out.println("  [" + e.getId() + "] " + e.getTitle());
                anyAtt=true;
            }
        }
        if(!anyAtt){
            System.out.println("  (none)");
        }

        System.out.println("Attendance pending:");
        boolean anyPend=false;
        for(Event e : CentralBackend.events){
            if(e.pending.contains(u)){
                System.out.println("  [" + e.getId() + "] " + e.getTitle());
                anyPend=true;
            }
        }
        if(!anyPend){
            System.out.println("  (none)");
        }
    }

    private static void showMyTasks(String u){
        System.out.println("\n--- My Tasks ---");
        boolean any=false;
        
        for(Task t : TaskManagementApp.taskRepo.findAll().values()){
            if(u.equalsIgnoreCase(t.getAssignedTo())){
                System.out.println("  [" + t.getTaskId() + "] " + t.getTitle() + " | " + t.getStatus() + " | priority " + t.getPriority() + " | due " + t.getDeadline());
                any=true;
            }
        }
        
        if(!any){
            System.out.println("  (none)");
        }
    }
}