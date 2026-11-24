import java.util.*;
public class CentralBackend{
    static class InvalidInputException extends Exception{
        public InvalidInputException(String msg){ 
            super(msg); 
        }
    }

    static class CapacityRule implements EligibilityRule{
        public boolean isEligible(Event event,String user){
            return event.getStatus() != EventStatus.CANCELLED
                && event.getStatus() != EventStatus.COMPLETED
                && event.getStatus() != EventStatus.ONGOING
                && event.getParticipants()<event.getCapacity()
                && !event.joined.contains(user);
        }
    }

    static List<Event> events=new ArrayList<>();
    static Map<Integer,Event> eventsByTheirId=new HashMap<>();
    static int nextId=1;
    static ParticipationQueue participationQueue=new ParticipationQueue();
    static Scanner sc=new Scanner(System.in);
    static EligibilityRule capacityRule=new CapacityRule();

    static boolean isManager(){
        return "admin".equals(ClubPilotApp.currentUser) || "lead1".equals(ClubPilotApp.currentUser);
    }

    public static void startEvents(){
        events=DatabaseManager.loadEvents();
        eventsByTheirId.clear();
        nextId=1;
        for(Event e:events){
            eventsByTheirId.put(e.getId(), e);
            if(e.getId()>=nextId){
                nextId=e.getId() + 1;
            }
        }

        if(events.isEmpty()){
            addEvent("Tech Meetup 2026", "2026-08-25", "10:00", "12:00", "upcoming", "tech", 50, 20);
            addEvent("Annual Cultural Fest", "2026-09-05", "14:00", "18:00", "upcoming", "cultural", 50, 33);
            addEvent("Football Tournament", "2026-08-20", "09:00", "11:00", "ongoing", "sports", 50, 0);
            addEvent("UI/UX Design Workshop", "2026-08-15", "10:00", "13:00", "completed", "workshop", 50, 37);
            addEvent("Club Orientation Day", "2026-07-28", "11:00", "12:30", "completed", "social", 50, 26);
            addEvent("Guest Lecture: AI & ML", "2026-09-10", "15:00", "16:30", "upcoming", "academic", 50, 29);
            addEvent("Dance Competition", "2026-08-10", "17:00", "20:00", "cancelled", "cultural", 50, 8);
            addEvent("Coding Hackathon", "2026-09-15", "09:00", "18:00", "ongoing", "tech", 50, 40);
        }
        eventsMenu();
    }

    static void addEvent(String title, String date, String start, String end, String statusStr, String cat, int cap, int p){
        EventStatus status=EventStatus.valueOf(statusStr.toUpperCase());
        Event e;
        if(cat.equals("tech")) e=new TechEvent(nextId++, title, date, start, end, status, cap, p);
        else if(cat.equals("cultural")) e=new CulturalEvent(nextId++, title, date, start, end, status, cap, p);
        else if(cat.equals("sports")) e=new SportsEvent(nextId++, title, date, start, end, status, cap, p);
        else if(cat.equals("workshop")) e=new WorkshopEvent(nextId++, title, date, start, end, status, cap, p);
        else if(cat.equals("social")) e=new SocialEvent(nextId++, title, date, start, end, status, cap, p);
        else e=new AcademicEvent(nextId++, title, date, start, end, status, cap, p);
        events.add(e);
        eventsByTheirId.put(e.getId(), e);
        DatabaseManager.saveEvent(e);
    }

    static boolean hasCollision(String date,String startTime,String endTime){
        for(Event e:events){
            if(e.getDate().equals(date)){
                if(startTime.compareTo(e.getEndTime()) < 0 && endTime.compareTo(e.getStartTime()) > 0){
                    return true;
                }
            }
        }
        return false;
    }

    static Event find(int id) throws EventNotFound{
        Event e=eventsByTheirId.get(id);
        if(e == null){
            throw new EventNotFound(id);
        }
        return e;
    }

    static void eventsMenu(){
        boolean manager=isManager();
        while(true){
            System.out.println("\n--- Events Menu ---");
            if(manager){
                System.out.println("1. Show all\n2. Create\n3. Mark completed\n4. Change status\n5. Delete");
                System.out.println("6. Approve attendance\n7. Reject attendance\n8. Filter by status\n9. Filter by category");
                System.out.println("10. Popular\n11. Sort by ID\n12. Sort by title\n13. Process participation queue\n14. Back to Main Menu");
            } 
            else{
                System.out.println("1. Show all\n2. Participate\n3. Cancel participation\n4. Request attendance");
                System.out.println("5. Filter by status\n6. Filter by category\n7. Popular\n8. Sort by title\n9. Back to Main Menu");
            }
            System.out.print("Choice: ");
            String c=sc.nextLine().trim();
            if(manager){
                switch(c){
                    case "1": showAll(); break;
                    case "2": createEvent(); break;
                    case "3": markCompleted(); break;
                    case "4": changeStatus(); break;
                    case "5": deleteEvent(); break;
                    case "6": approveAttendance(); break;
                    case "7": rejectAttendance(); break;
                    case "8": filterByStatus(); break;
                    case "9": filterByCategory(); break;
                    case "10": popular(); break;
                    case "11": sorteventsByTheirId(); break;
                    case "12": sortByTitle(); break;
                    case "13": processParticipationQueue(); break;
                    case "14": return;
                    default: System.out.println("Invalid choice");
                }
            } 
            else{
                switch(c){
                    case "1": showAll(); break;
                    case "2": participate(); break;
                    case "3": cancelParticipation(); break;
                    case "4": requestAttendance(); break;
                    case "5": filterByStatus(); break;
                    case "6": filterByCategory(); break;
                    case "7": popular(); break;
                    case "8": sortByTitle(); break;
                    case "9": return;
                    default: System.out.println("Invalid choice");
                }
            }
        }
    }

    static void showAll(){
        System.out.println("\n--- All Events(" + events.size() + ") ---");
        for(Event e:events){
            if(isManager()){
                e.show();
            }
            else{
                e.showForUser();
            }
        }
    }

    static void createEvent(){
        try{
            System.out.print("Enter Title: ");
            String t=sc.nextLine().trim();
            if(t.isEmpty()){
                throw new InvalidInputException("Title cannot be blank");
            }

            System.out.print("Enter Date: ");
            String d=sc.nextLine().trim();

            if(d.isEmpty()){
                throw new InvalidInputException("Date cannot be blank");
            }

            System.out.print("Enter Start Time(HH:mm): ");
            String start=sc.nextLine().trim();
            
            System.out.print("Enter End Time(HH:mm): ");
            String end=sc.nextLine().trim();

            if(hasCollision(d, start, end)){
                throw new InvalidInputException("Collision detected! Another event is scheduled at this time.");
            }

            System.out.print("Enter Category: ");
            String cat=sc.nextLine().trim().toLowerCase();
            if(cat.isEmpty()){
                throw new InvalidInputException("Category cannot be blank");
            }

            System.out.print("Enter Capacity: ");
            int cap=Integer.parseInt(sc.nextLine().trim());
            if(cap <= 0){
                throw new InvalidInputException("Capacity must be > 0");
            }

            addEvent(t, d, start, end, "upcoming", cat, cap, 0);
            System.out.println("Event created successfully");
        } 
        catch(InvalidInputException ex){
            System.out.println("Invalid input: " + ex.getMessage());
        } 
        catch(NumberFormatException ex){
            System.out.println("Invalid input: Capacity must be a number.");
        }
    }

    static void markCompleted(){
        try{
            System.out.print("Enter Event ID: ");
            Event e=find(Integer.parseInt(sc.nextLine().trim()));
            e.setStatus(EventStatus.COMPLETED);
            DatabaseManager.saveEvent(e);
            System.out.println("Marked completed.");
        } 
        catch(EventNotFound ex){ 
            System.out.println(ex.getMessage()); 
        }
    }

    static void changeStatus(){
        try{
            System.out.print("Enter Event ID: ");
            Event e=find(Integer.parseInt(sc.nextLine().trim()));
            System.out.print("Enter New Status(UPCOMING/ONGOING/COMPLETED/CANCELLED): ");
            e.setStatus(EventStatus.valueOf(sc.nextLine().trim().toUpperCase()));
            DatabaseManager.saveEvent(e);
            System.out.println("Status updated.");
        } 
        catch(Exception ex){ 
            System.out.println("Invalid input!"); 
        }
    }

    static void deleteEvent(){
        try{
            System.out.print("Enter Event ID: ");
            int id=Integer.parseInt(sc.nextLine().trim());
            events.remove(find(id));
            eventsByTheirId.remove(id);
            DatabaseManager.deleteEvent(id);
            System.out.println("Deleted.");
        } 
        catch(EventNotFound ex){ 
            System.out.println(ex.getMessage()); 
        }
    }

    static void participate(){
        try{
            System.out.print("Enter Event ID: ");
            int id=Integer.parseInt(sc.nextLine().trim());
            Event e=find(id);

            if(!capacityRule.isEligible(e, ClubPilotApp.currentUser)){
                if(e.getStatus() == EventStatus.CANCELLED){
                    System.out.println("Cannot join: This event has been cancelled.");
                }
                else if(e.getStatus() == EventStatus.COMPLETED){
                    System.out.println("Cannot join: This event is already completed.");
                }
                else if(e.getStatus() == EventStatus.ONGOING){
                    System.out.println("Cannot join: This event has already started (ongoing).");
                }
                else if(e.getParticipants() >= e.getCapacity()){
                    System.out.println("Cannot join: The event is full (" + e.getParticipants() + "/" + e.getCapacity() + " seats taken).");
                }
                else if(e.joined.contains(ClubPilotApp.currentUser)){
                    System.out.println("Cannot join: You have already joined this event.");
                }
                return;
            }

            participationQueue.produce(ClubPilotApp.currentUser + "|" + id);
            System.out.println("Request queued successfully.");
        } 
        catch(EventNotFound ex){ 
            System.out.println(ex.getMessage()); 
        }
    }

    static void cancelParticipation(){
        try{
            System.out.print("Enter Event ID: ");
            Event e=find(Integer.parseInt(sc.nextLine().trim()));
            if(!e.joined.contains(ClubPilotApp.currentUser)){
                System.out.println("Cannot cancel: you are not joined.");
                return;
            }
            if(e.getStatus() == EventStatus.ONGOING){
                System.out.println("Cannot cancel: event is ongoing.");
                return;
            }
            e.cancel(ClubPilotApp.currentUser);
            try(java.sql.Connection conn=DatabaseManager.getConnection();
                java.sql.PreparedStatement pstmt=conn.prepareStatement("DELETE FROM joined_user_jdbc WHERE event_id=? AND username=?")){
                pstmt.setInt(1, e.getId()); 
                pstmt.setString(2, ClubPilotApp.currentUser); 
                pstmt.executeUpdate();
            } 
            catch(Exception ex){

            }
            DatabaseManager.deleteAttendance(e.getId(), ClubPilotApp.currentUser);
            DatabaseManager.saveEvent(e);
            System.out.println("Cancelled.");
        } 
        catch(EventNotFound ex){ 
            System.out.println(ex.getMessage()); 
        }
    }

    static void requestAttendance(){
        try{
            System.out.print("Enter Event ID: ");
            Event e=find(Integer.parseInt(sc.nextLine().trim()));
            if(!e.joined.contains(ClubPilotApp.currentUser)){
                System.out.println("Cannot request: not approved by admin yet.");
                return;
            }
            if(e.getStatus() != EventStatus.ONGOING){
                System.out.println("Cannot request: event is not ongoing.");
                return;
            }
            if(e.pending.contains(ClubPilotApp.currentUser)){
                System.out.println("Cannot request: pending already.");
                return;
            }

            e.requestAttendance(ClubPilotApp.currentUser);
            DatabaseManager.saveAttendance(e.getId(), ClubPilotApp.currentUser, "PENDING");
            System.out.println("Attendance requested.");
        } 
        catch(EventNotFound ex){ 
            System.out.println(ex.getMessage()); 
        }
    }

    static void approveAttendance(){
        try{
            System.out.print("Enter Event ID: ");
            Event e=find(Integer.parseInt(sc.nextLine().trim()));
            if(e.pending.isEmpty()){
                System.out.println("No user pending for this event.");
                return;
            }
            System.out.println("\nPending: " + e.pending);
            System.out.print("Enter Username to APPROVE: ");
            String user=sc.nextLine().trim();
            if(e.approve(user)) {
                DatabaseManager.saveAttendance(e.getId(), user, "ATTENDED");
                System.out.println("Approved.");
            }
            else {
                System.out.println("Failed.");
            }
        } 
        catch(EventNotFound ex){
             System.out.println(ex.getMessage()); 
        }
    }

    static void rejectAttendance(){
        try{
            System.out.print("Enter Event ID: ");
            Event e=find(Integer.parseInt(sc.nextLine().trim()));
            if(e.pending.isEmpty()){
                System.out.println("No user pending for this event.");
                return;
            }
            System.out.println("\nPending: " + e.pending);
            System.out.print("Enter Username to REJECT: ");
            String user=sc.nextLine().trim();
            if(e.reject(user)) {
                DatabaseManager.saveAttendance(e.getId(), user, "REJECTED"); 
                System.out.println("Rejected.");
            }
            else {
                System.out.println("Failed.");
            }
        } 
        catch(EventNotFound ex){ 
            System.out.println(ex.getMessage()); 
        }
    }

    static void filterByStatus(){
        System.out.print("Enter Status: ");
        try{
            EventStatus searchStatus=EventStatus.valueOf(sc.nextLine().trim().toUpperCase());
            for(Event e : events){
                if(e.getStatus() == searchStatus){
                    if(isManager()) e.show(); else e.showForUser();
                }
            }
        } 
        catch(IllegalArgumentException ex){ 
            System.out.println("Invalid status!"); 
        }
    }

    static void filterByCategory(){
        System.out.print("Enter Category: ");
        String c=sc.nextLine().trim();
        for(Event e : events){
            if(e.getCategory().equalsIgnoreCase(c)){
                if(isManager()) e.show(); else e.showForUser();
            }
        }
    }

    static void popular(){
        System.out.println("\n--- Popular Events ---");
        List<Event> copy=new ArrayList<>(events);
        copy.sort(new SortByParticipants());
        for(int i=0; i < 3 && i < copy.size(); i++){
            System.out.println("  " + copy.get(i).getTitle() + " - " + copy.get(i).getParticipants());
        }
    }

    static void sorteventsByTheirId(){
        System.out.println("\n--- Events sorted by ID ---");
        List<Event> copy=new ArrayList<>(events);
        Collections.sort(copy);
        for(Event e : copy) System.out.println("  [" + e.getId() + "] " + e.getTitle());
    }

    static void sortByTitle(){
        System.out.println("\n--- Events sorted by title ---");
        List<Event> copy=new ArrayList<>(events);
        copy.sort(new SortByTitle());
        for(Event e : copy) System.out.println("  " + e.getTitle());
    }

    static void processParticipationQueue(){
        if(participationQueue.isEmpty()){
            System.out.println("No pending participation requests.");
            return;
        }

        System.out.println("\n--- Processing Participation Queue ---");
        List<String> requests=new ArrayList<>();
        int pending=participationQueue.size();
        for(int i=0; i < pending; i++){
            requests.add(participationQueue.consume());
        }

        List<Thread> workers=new ArrayList<>();
        List<JoinThread> jobs=new ArrayList<>();

        for(String req : requests){
            String[] parts=req.split("\\|");
            Event e=eventsByTheirId.get(Integer.parseInt(parts[1]));
            if(e != null){
                JoinThread jt=new JoinThread(e, parts[0]);
                Thread t=new Thread(jt, "Worker-" + parts[0]);
                t.start();
                workers.add(t);
                jobs.add(jt);
            }
        }

        for(Thread t : workers){
            try{ 
                t.join(); 
            } 
            catch(InterruptedException ex){ 
                Thread.currentThread().interrupt(); 
            }
        }

        int count=0;
        for(JoinThread jt : jobs){
            if(jt.success){
                System.out.println("JOINED: " + jt.user + " -> " + jt.e.getTitle());
                DatabaseManager.saveJoinedUser(jt.e.getId(), jt.user);
                count++;
            } 
            else{
                System.out.println("FAILED: " + jt.user + " -> " + jt.e.getTitle());
            }
        }
        
        for(Event e : events) DatabaseManager.saveEvent(e);
        System.out.println("\nProcessed " + count + " participation(s). ALL DONE.");
        participationQueue.reset();
    }
}

class SortByParticipants implements Comparator<Event>{
    public int compare(Event a, Event b){ 
        return b.getParticipants() - a.getParticipants(); 
    }
}

class SortByTitle implements Comparator<Event>{
    public int compare(Event a, Event b){ 
        return a.getTitle().compareTo(b.getTitle()); 
    }
}
