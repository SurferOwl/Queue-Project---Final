package Model;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server extends Thread {
    private final LinkedBlockingQueue<Clients> queue;
    private final AtomicInteger waitingTime;
    private final AtomicInteger totalServiceTime;

    private boolean running = true;

    public void terminate() {
        running = false;
        synchronized (this) {
            notifyAll();
        }
    }

    public Server() {
        queue = new LinkedBlockingQueue<>();
        waitingTime = new AtomicInteger(0);
        totalServiceTime = new AtomicInteger(0);
    }

    public synchronized void addTask(Clients c) {
        try {
            queue.put(c);
            totalServiceTime.addAndGet(c.getServiceTime().intValue());
            waitingTime.addAndGet(c.getServiceTime().intValue());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public AtomicInteger getWaitingTime() {
        return waitingTime;
    }

    public AtomicInteger getTotalServiceTime() {
        return totalServiceTime;
    }

    public LinkedBlockingQueue<Clients> getQueue() {
        return queue;
    }

    public void run() {
        while (running) {
            Clients c = null;
            c = queue.peek();
            if (c != null) {
                try {
                    while(c.getServiceTime().intValue() > 0){
                    Thread.sleep(1000);
                    c.getServiceTime().decrementAndGet();}
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                try {
                    queue.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                    waitingTime.addAndGet(-c.getServiceTime().intValue());

            }
        }
    }
}
