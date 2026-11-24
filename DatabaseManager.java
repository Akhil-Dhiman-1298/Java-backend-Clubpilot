import java.sql.*;
import java.util.*;

public class DatabaseManager{
    private static final String URL="jdbc:mysql://localhost:3306/clubpilot-jdbc";
    private static final String USER="root";
    private static final String PASS="akhilsql2006";

    static{
        try{ 
            Class.forName("com.mysql.cj.jdbc.Driver"); 
        } 
        catch(Exception e){}
    }

    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static List<Event> loadEvents(){
        List<Event> loaded=new ArrayList<>();
        String sql="SELECT * FROM event_jdbc";
        try(Connection conn=getConnection(); 
            Statement stmt=conn.createStatement(); 
            ResultSet rs=stmt.executeQuery(sql)){
            while(rs.next()){
                int id=rs.getInt("id");
                String title=rs.getString("title");
                String date=rs.getString("date");
                String startTime=rs.getString("start_time");
                String endTime=rs.getString("end_time");
                EventStatus status=EventStatus.valueOf(rs.getString("status"));
                String cat=rs.getString("category");
                int cap=rs.getInt("capacity");
                int part=rs.getInt("participants");

                Event e=null;
                if(cat.equals("tech")) e=new TechEvent(id, title, date, startTime, endTime, status, cap, part);
                else if(cat.equals("cultural")) e=new CulturalEvent(id, title, date, startTime, endTime, status, cap, part);
                else if(cat.equals("sports")) e=new SportsEvent(id, title, date, startTime, endTime, status, cap, part);
                else if(cat.equals("workshop")) e=new WorkshopEvent(id, title, date, startTime, endTime, status, cap, part);
                else if(cat.equals("social")) e=new SocialEvent(id, title, date, startTime, endTime, status, cap, part);
                else e=new AcademicEvent(id, title, date, startTime, endTime, status, cap, part);

                String joinSql="SELECT username FROM joined_user_jdbc WHERE event_id=?";
                try(PreparedStatement pstmt=conn.prepareStatement(joinSql)){
                    pstmt.setInt(1, id);
                    try(ResultSet jrs=pstmt.executeQuery()){
                        while(jrs.next()){
                            e.joined.add(jrs.getString("username"));
                        }
                    }
                }
                loaded.add(e);
            }
        } 
        catch(SQLException e){ 
            System.out.println("DB Load Error: " + e.getMessage()); 
        }

        loadAttendanceData(loaded);
        return loaded;
    }

    public static void saveEvent(Event e){
        String sql="REPLACE INTO event_jdbc (id, title, date, start_time, end_time, status, category, capacity, participants) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try(Connection conn=getConnection(); 
            PreparedStatement pstmt=conn.prepareStatement(sql)){
            pstmt.setInt(1, e.getId());
            pstmt.setString(2, e.getTitle());
            pstmt.setString(3, e.getDate());
            pstmt.setString(4, e.getStartTime());
            pstmt.setString(5, e.getEndTime());
            pstmt.setString(6, e.getStatus().name());
            pstmt.setString(7, e.getCategory());
            pstmt.setInt(8, e.getCapacity());
            pstmt.setInt(9, e.getParticipants());
            pstmt.executeUpdate();
        } 
        catch(SQLException ex){ 
            System.out.println("DB Save Error: " + ex.getMessage()); 
        }
    }

    public static void saveJoinedUser(int eventId, String username){
        String sql="INSERT IGNORE INTO joined_user_jdbc (event_id, username) VALUES (?, ?)";
        try(Connection conn=getConnection(); 
            PreparedStatement pstmt=conn.prepareStatement(sql)){
            pstmt.setInt(1, eventId);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } 
        catch(SQLException ex){ 
            System.out.println("DB Join Error: " + ex.getMessage()); 
        }
    }

    public static void deleteEvent(int id){
        try(Connection conn=getConnection()){
            try(PreparedStatement p1=conn.prepareStatement("DELETE FROM joined_user_jdbc WHERE event_id=?")){
                p1.setInt(1, id); 
                p1.executeUpdate();
            }
            try(PreparedStatement p2=conn.prepareStatement("DELETE FROM event_jdbc WHERE id=?")){
                p2.setInt(1, id); 
                p2.executeUpdate();
            }
            try(PreparedStatement p3=conn.prepareStatement("DELETE FROM attendance_jdbc WHERE event_id=?")){
                p3.setInt(1, id); 
                p3.executeUpdate();
            }
        } 
        catch(SQLException ex){ 
            System.out.println("DB Delete Error: " + ex.getMessage()); 
        }
    }

    public static void saveTask(Task t){
        String sql="REPLACE INTO tasks_jdbc (task_id, title, description, priority, status, deadline, assigned_to, assigned_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try(Connection conn=getConnection(); PreparedStatement pstmt=conn.prepareStatement(sql)){
            pstmt.setInt(1, t.getTaskId()); 
            pstmt.setString(2, t.getTitle()); 
            pstmt.setString(3, t.getDescription());
            pstmt.setString(4, t.getPriority().name()); 
            pstmt.setString(5, t.getStatus().name()); 
            pstmt.setString(6, t.getDeadline());
            pstmt.setString(7, t.getAssignedTo()); 
            pstmt.setString(8, t.getAssignedBy());
            pstmt.executeUpdate();
        } 
        catch(SQLException ex){ 
            System.out.println("DB Task Save Error: " + ex.getMessage()); 
        }
    }

    public static void deleteTask(int taskId){
        String sql="DELETE FROM tasks_jdbc WHERE task_id=?";
        
        try(Connection conn=getConnection(); PreparedStatement pstmt=conn.prepareStatement(sql)){
            pstmt.setInt(1, taskId); 
            pstmt.executeUpdate();
        } 
        catch(SQLException ex){ 
            System.out.println("DB Task Delete Error: " + ex.getMessage()); 
        }
    }

    public static List<Task> loadTasks(){
        List<Task> loaded=new ArrayList<>();
        String sql="SELECT * FROM tasks_jdbc";
        
        try(Connection conn=getConnection(); Statement stmt=conn.createStatement(); ResultSet rs=stmt.executeQuery(sql)){
            while(rs.next()){
                loaded.add(new Task(rs.getInt("task_id"), rs.getString("title"), rs.getString("description"), Priority.valueOf(rs.getString("priority")), TaskStatus.valueOf(rs.getString("status")), rs.getString("deadline"), rs.getString("assigned_to"), rs.getString("assigned_by")));
            }
        } 
        catch(SQLException e){ 
            System.out.println("DB Task Load Error: " + e.getMessage()); 
        }
        return loaded;
    }

    public static void saveUser(String username, String password, String displayName, String email, String role){
        String sql="REPLACE INTO users_jdbc (username, password, display_name, email, role) VALUES (?, ?, ?, ?, ?)";
        try(Connection conn=getConnection(); 
            PreparedStatement pstmt=conn.prepareStatement(sql)){
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, displayName);
            pstmt.setString(4, email);
            pstmt.setString(5, role);
            pstmt.executeUpdate();
        } 
        catch(SQLException ex){ 
            System.out.println("DB User Save Error: " + ex.getMessage()); 
        }
    }

    public static void deleteUser(String username){
        String sql="DELETE FROM users_jdbc WHERE username=?";
        try(Connection conn=getConnection(); 
            PreparedStatement pstmt=conn.prepareStatement(sql)){
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } 
        catch(SQLException ex){ 
            System.out.println("DB User Delete Error: " + ex.getMessage()); 
        }
    }

    public static void updateUserRole(String username, String newRole){
        String sql="UPDATE users_jdbc SET role=? WHERE username=?";
        try(Connection conn=getConnection(); 
            PreparedStatement pstmt=conn.prepareStatement(sql)){
            pstmt.setString(1, newRole);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } 
        catch(SQLException ex){ 
            System.out.println("DB Role Update Error: " + ex.getMessage()); 
        }
    }

    public static void loadUsers(){
        String sql="SELECT * FROM users_jdbc";
        try(Connection conn=getConnection(); 
            Statement stmt=conn.createStatement(); 
            ResultSet rs=stmt.executeQuery(sql)){
            while(rs.next()){
                String u=rs.getString("username");
                String p=rs.getString("password");
                String n=rs.getString("display_name");
                String e=rs.getString("email");
                String r=rs.getString("role");
                
                ClubPilotApp.users.put(u, p);
                ClubPilotApp.displayNames.put(u, n);
                ClubPilotApp.emails.put(u, e);
                ClubPilotApp.roles.put(u, r);
            }
        } 
        catch(SQLException ex){ 
            System.out.println("DB User Load Error: " + ex.getMessage()); 
        }
    }

    public static void saveAttendance(int eventId, String username, String status){
        String sql="REPLACE INTO attendance_jdbc (event_id, username, status) VALUES (?, ?, ?)";
        try(Connection conn=getConnection(); 
            PreparedStatement pstmt=conn.prepareStatement(sql)){
            pstmt.setInt(1, eventId);
            pstmt.setString(2, username);
            pstmt.setString(3, status);
            pstmt.executeUpdate();
        } 
        catch(SQLException ex){ 
            System.out.println("DB Attendance Save Error: " + ex.getMessage()); 
        }
    }

    public static void deleteAttendance(int eventId, String username){
        String sql="DELETE FROM attendance_jdbc WHERE event_id=? AND username=?";
        try(Connection conn=getConnection(); 
            PreparedStatement pstmt=conn.prepareStatement(sql)){
            pstmt.setInt(1, eventId);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } 
        catch(SQLException ex){ 
            System.out.println("DB Attendance Delete Error: " + ex.getMessage()); 
        }
    }

    public static void loadAttendanceData(List<Event> loadedEvents){
        String sql="SELECT * FROM attendance_jdbc";
        try(Connection conn=getConnection(); 
            Statement stmt=conn.createStatement(); 
            ResultSet rs=stmt.executeQuery(sql)){
            while(rs.next()){
                int eventId=rs.getInt("event_id");
                String username=rs.getString("username");
                String status=rs.getString("status");

                for(Event e : loadedEvents){
                    if(e.getId() == eventId){
                        if(status.equals("PENDING")){
                            e.pending.add(username);
                        }
                        else if(status.equals("ATTENDED")){
                            e.attended.add(username);
                        }
                        else if(status.equals("REJECTED")){
                            e.rejected.add(username);
                        }
                        break;
                    }
                }
            }
        } 
        catch(SQLException ex){ 
            System.out.println("DB Attendance Load Error: " + ex.getMessage()); 
        }
    }
}
