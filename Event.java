import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

abstract class Event implements Comparable<Event> {
    private int id;
    private String title;
    private String date;
    private EventStatus status;
    private String category;
    private int capacity;
    private int participants;

    private final ReentrantLock lock = new ReentrantLock();

    LinkedHashSet<String> joined = new LinkedHashSet<>();
    TreeSet<String> attended = new TreeSet<>();
    LinkedList<String> pending = new LinkedList<>();
    HashSet<String> rejected = new HashSet<>();

    Event(int id, String title, String date, EventStatus status, String category, int capacity, int participants) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.status = status;
        this.category = category;
        this.capacity = capacity;
        this.participants = participants;
    }

    public abstract String getCategory();

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDate() { return date; }
    public EventStatus getStatus() { return status; }
    public int getCapacity() { return capacity; }
    public int getParticipants() { return participants; }

    public void setTitle(String title) { this.title = title; }
    public void setStatus(EventStatus status) { this.status = status; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    boolean join(String user) {
        lock.lock();
        try {
            if (participants >= capacity) return false;
            if (joined.contains(user)) return false;
            joined.add(user);
            participants++;
            return true;
        } 
        finally {
            lock.unlock();
        }
    }

    synchronized boolean cancel(String user) {
        if (!joined.contains(user)) return false;
        if (status == EventStatus.ONGOING) return false;
        joined.remove(user);
        participants--;
        return true;
    }

    synchronized boolean requestAttendance(String user) {
        if (status != EventStatus.ONGOING) return false;
        if (!joined.contains(user)) return false;
        if (pending.contains(user)) return false;
        pending.add(user);
        return true;
    }

    synchronized boolean approve(String user) {
        if (!pending.contains(user)) return false;
        pending.remove(user);
        attended.add(user);
        return true;
    }

    synchronized boolean reject(String user) {
        if (!pending.contains(user)) return false;
        pending.remove(user);
        rejected.add(user);
        return true;
    }

    public int compareTo(Event other) {
        return Integer.compare(this.id, other.id);
    }

    void show() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID       : ").append(id).append(" | ").append(title).append(" [").append(getCategory()).append("]\n");
        sb.append("Date     : ").append(date).append("\n");
        sb.append("Status   : ").append(status.name().toLowerCase()).append("\n");
        sb.append("Seats    : ").append(participants).append("/").append(capacity).append("\n");
        sb.append("Joined   : ").append(joined).append("\n");
        sb.append("Attended : ").append(attended).append("\n");
        sb.append("Pending  : ").append(pending).append("\n");
        sb.append("Rejected : ").append(rejected).append("\n");
        System.out.println(sb);
    }

    void showForUser() {
        System.out.println("ID       : " + id + " | " + title + " [" + getCategory() + "]");
        System.out.println("Date     : " + date);
        System.out.println("Status   : " + status.name().toLowerCase());
        System.out.println("Seats    : " + participants + "/" + capacity);
        System.out.println();
    }
}