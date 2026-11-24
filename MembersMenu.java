import java.util.*;

public class MembersMenu{
    public static void show(Scanner sc){
        while(true){
            System.out.println("\n===== MEMBERS =====");
            List<String> names=new ArrayList<>(ClubPilotApp.users.keySet());
            Collections.sort(names);

            System.out.printf("%-12s %-16s %-28s %s%n", "Username", "Name", "Email", "Role");
            for(String u : names){
                String n=ClubPilotApp.displayNames.getOrDefault(u, u);
                String e=ClubPilotApp.emails.getOrDefault(u, "");
                System.out.printf("%-12s %-16s %-28s %s%n", u, n, e, ClubPilotApp.getRole(u));
            }
            System.out.println("-------------------");

            boolean manager=ClubPilotApp.isManager();
            boolean admin=ClubPilotApp.isAdmin();

            System.out.println("1. Search member by username");
            System.out.println("2. View member details");
            System.out.println("3. Filter by role");
            if(manager) System.out.println("4. View member assignments");
            if(admin) System.out.println("5. Change member role");
            if(manager) System.out.println("6. Remove member");
            System.out.println("7. Back");
            System.out.print("Choice: ");

            String choice=sc.nextLine().trim();

            if(choice.equals("1")){
                System.out.print("Username to search: ");
                String q=sc.nextLine().trim();
                if(!ClubPilotApp.users.containsKey(q)){
                    System.out.println("No member found: " + q);
                }
                else{
                    System.out.println("Found: " + q + " | " + ClubPilotApp.displayNames.getOrDefault(q, q) + " | " + ClubPilotApp.emails.getOrDefault(q, "") + " | " + ClubPilotApp.getRole(q));
                }
            }
            else if(choice.equals("2")){
                System.out.print("Username to view: ");
                String q=sc.nextLine().trim();
                if(!ClubPilotApp.users.containsKey(q)){
                    System.out.println("No member found: " + q);
                }
                else{
                    showMemberDetails(q);
                }
            }
            else if(choice.equals("3")){
                filterByRole(sc);
            }
            else if(choice.equals("4") && manager){
                System.out.print("Username to inspect: ");
                String q=sc.nextLine().trim();
                if(!ClubPilotApp.users.containsKey(q)){
                    System.out.println("No member found: " + q);
                }
                else{
                    showAssignments(q);
                }
            }
            else if(choice.equals("5") && admin){
                System.out.print("Username to change role: ");
                String q=sc.nextLine().trim();
                
                if(!ClubPilotApp.users.containsKey(q)){
                    System.out.println("No member found: " + q);
                    continue;
                }
                
                if(q.equals(ClubPilotApp.currentUser)){
                    System.out.println("You cannot change your own role.");
                    continue;
                }
                
                System.out.print("New role(admin/lead/member): ");
                String r=sc.nextLine().trim().toLowerCase();
                
                if(!r.equals("admin") && !r.equals("lead") && !r.equals("member")){
                    System.out.println("Invalid role.");
                }
                else{
                    ClubPilotApp.roles.put(q, r);
                    System.out.println("Role updated: " + q + " -> " + r);
                }
            }
            else if(choice.equals("6") && manager){
                System.out.print("Username to remove: ");
                String q=sc.nextLine().trim();
                
                if(q.equals(ClubPilotApp.currentUser)){
                    System.out.println("You cannot remove yourself.");
                }
                else if(!ClubPilotApp.users.containsKey(q)){
                    System.out.println("No member found: " + q);
                }
                else if("admin".equals(ClubPilotApp.getRole(q)) && !ClubPilotApp.isAdmin()){
                    System.out.println("Only an admin can remove another admin.");
                }
                else{
                    ClubPilotApp.users.remove(q);
                    ClubPilotApp.displayNames.remove(q);
                    ClubPilotApp.emails.remove(q);
                    ClubPilotApp.roles.remove(q);
                    System.out.println("Removed.");
                }
            }
            else if(choice.equals("7")){
                return;
            }
            else{
                System.out.println("Invalid choice.");
            }
        }
    }

    private static void showMemberDetails(String q){
        System.out.println("\n--- Member Details ---");
        System.out.println("Username : " + q);
        System.out.println("Name     : " + ClubPilotApp.displayNames.getOrDefault(q, q));
        System.out.println("Email    : " + ClubPilotApp.emails.getOrDefault(q, "(not set)"));
        System.out.println("Role     : " + ClubPilotApp.getRole(q));

        showAssignments(q);
    }

    private static void filterByRole(Scanner sc){
        System.out.print("Role to filter(admin/lead/member): ");
        String r=sc.nextLine().trim().toLowerCase();
        
        if(!r.equals("admin") && !r.equals("lead") && !r.equals("member")){
            System.out.println("Invalid role.");
            return;
        }
        
        System.out.println("\n--- Members with role: " + r + " ---");
        List<String> names=new ArrayList<>(ClubPilotApp.users.keySet());
        Collections.sort(names);
        boolean any=false;
        
        for(String u : names){
            if(ClubPilotApp.getRole(u).equals(r)){
                System.out.println("  " + u + " | " + ClubPilotApp.displayNames.getOrDefault(u, u) + " | " + ClubPilotApp.emails.getOrDefault(u, ""));
                any=true;
            }
        }
        
        if(!any) System.out.println("  (none)");
    }

    private static void showAssignments(String username){
        System.out.println("\n--- Assignments for " + username + " ---");

        System.out.println("Events joined:");
        boolean anyEvent=false;
        
        for(Event e : CentralBackend.events){
            if(e.joined.contains(username)){
                System.out.println("  [" + e.getId() + "] " + e.getTitle() + " (" + e.getStatus() + ") on " + e.getDate());
                anyEvent=true;
            }
        }
        
        if(!anyEvent) System.out.println("  (none)");

        System.out.println("Events attended(approved):");
        boolean anyAtt=false;
        
        for(Event e : CentralBackend.events){
            if(e.attended.contains(username)){
                System.out.println("  [" + e.getId() + "] " + e.getTitle());
                anyAtt=true;
            }
        }
        
        if(!anyAtt) System.out.println("  (none)");

        System.out.println("Attendance pending:");
        boolean anyPend=false;
        
        for(Event e : CentralBackend.events){
            if(e.pending.contains(username)){
                System.out.println("  [" + e.getId() + "] " + e.getTitle());
                anyPend=true;
            }
        }
        
        if(!anyPend) System.out.println("  (none)");

        System.out.println("Tasks assigned:");
        boolean anyTask=false;
        
        for(Task t : TaskManagementApp.taskRepo.findAll().values()){
            if(username.equalsIgnoreCase(t.getAssignedTo())){
                System.out.println("  [" + t.getTaskId() + "] " + t.getTitle() + " | " + t.getStatus() + " | priority " + t.getPriority() + " | due " + t.getDeadline());
                anyTask=true;
            }
        }
        
        if(!anyTask) System.out.println("  (none)");
    }
}