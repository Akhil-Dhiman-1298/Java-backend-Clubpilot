public class User {
    private String username;
    private String displayName;
    private String email;
    private boolean manager;   // true for admin/lead1, false for user1/user2

    public User(String username, String displayName, String email, boolean manager) {
        this.username = username;
        this.displayName = displayName;
        this.email = email;
        this.manager = manager;
    }

    public String getUsername()    { return username; }
    public String getDisplayName() { return displayName; }
    public String getEmail()       { return email; }
    public boolean isManager()     { return manager; }

    public void setDisplayName(String d) { this.displayName = d; }
    public void setEmail(String e)       { this.email = e; }
}