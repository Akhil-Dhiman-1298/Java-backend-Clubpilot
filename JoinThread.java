class JoinThread implements Runnable {
    Event e;
    String user;
    boolean success;

    JoinThread(Event e, String user) {
        this.e = e;
        this.user = user;
    }

    public void run() {
        try {
            Thread.sleep(100); 
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return;
        }
        success = e.join(user);
    }
}