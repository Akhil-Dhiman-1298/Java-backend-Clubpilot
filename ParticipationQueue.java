import java.util.LinkedList;

class ParticipationQueue {
    private LinkedList<String> queue = new LinkedList<>();
    private int capacity = 10;
    private boolean done = false;

    public synchronized void produce(String request) {
        while (queue.size() == capacity) {
            try { wait(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
        }
        queue.add(request);
        System.out.println("[Producer] queued: " + request);
        notifyAll();
    }

    public synchronized String consume() {
        while (queue.isEmpty() && !done) {
            try { wait(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return null; }
        }
        if (queue.isEmpty() && done) return null;
        String r = queue.remove();
        notifyAll();
        return r;
    }

    public synchronized void signalDone() {
        done = true;
        notifyAll();
    }

    public synchronized boolean isEmpty() { return queue.isEmpty(); }
    public synchronized int size() { return queue.size(); }
    public synchronized void reset() { done = false; }
}