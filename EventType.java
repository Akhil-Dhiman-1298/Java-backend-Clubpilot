class TechEvent extends Event {
    TechEvent(int id, String title, String date, EventStatus status, int capacity, int participants) {
        super(id, title, date, status, "tech", capacity, participants);
    }
    public String getCategory(){ return "tech"; }
}

class CulturalEvent extends Event {
    CulturalEvent(int id, String title, String date, EventStatus status, int capacity, int participants) {
        super(id, title, date, status, "cultural", capacity, participants);
    }
    public String getCategory(){ return "cultural"; }
}

class SportsEvent extends Event {
    SportsEvent(int id, String title, String date, EventStatus status, int capacity, int participants) {
        super(id, title, date, status, "sports", capacity, participants);
    }
    public String getCategory(){ return "sports"; }
}

class WorkshopEvent extends Event {
    WorkshopEvent(int id, String title, String date, EventStatus status, int capacity, int participants) {
        super(id, title, date, status, "workshop", capacity, participants);
    }
    public String getCategory(){ return "workshop"; }
}

class SocialEvent extends Event {
    SocialEvent(int id, String title, String date, EventStatus status, int capacity, int participants) {
        super(id, title, date, status, "social", capacity, participants);
    }
    public String getCategory(){ return "social"; }
}

class AcademicEvent extends Event {
    AcademicEvent(int id, String title, String date, EventStatus status, int capacity, int participants) {
        super(id, title, date, status, "academic", capacity, participants);
    }
    public String getCategory(){ return "academic"; }
}

class EventNotFound extends Exception {
    EventNotFound(int id) { super("Event "+id+" not found"); }
}

class SlotFull extends Exception {
    SlotFull(String title) { super("Event full: "+title); }
}